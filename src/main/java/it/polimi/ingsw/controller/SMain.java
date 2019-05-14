package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for the server's executable
 */
public class SMain
{
    /**
     * Socket server, waiting for socket clients' connections
     */
    private Server socket;
    /**
     * RMI server, waiting for RMI clients' connections
     */
    private Server rmi;

    /**
     * Match that is currently waiting to have enough players to start
     */
    private Match[] waiting;

    /**
     * List containing all currently active matches
     */
    private List<Match> matches;

    private boolean stop;
    private Timer[] timer;
    Object lock = new Object();
    private static final int MINSKULLS = 5;
    private boolean[] startedTimer;

    /**
     * Creates a new SMain
     */
    public SMain()
    {
        try {
            socket = new SocketServer(1906);
            rmi = new RMIServer();
            matches = new ArrayList<>();
            stop = false;

            waiting = new Match[8 - MINSKULLS + 1];
            timer = new Timer[8 - MINSKULLS + 1];
            startedTimer = new boolean[8 - MINSKULLS + 1];

            for(int i = 0; i < timer.length; i++) {
                timer[i] = new Timer();
                startedTimer[i] = false;
            }
            System.out.println("Adrenalina Server ready");
            System.out.println("Server IP: " + Inet4Address.getLocalHost().getHostAddress());
            listen();
        }
        catch (RemoteException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
            System.out.println("Errore nell'avvio del server RMI");
        }
        catch (IOException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
            System.out.println("Errore nell'avvio del server Socket");
        }
        listen();
    }

    private void println(String payload) {
        System.out.println(payload);
    }

    /**
     * Starts listening for new users' connections
     */
    public void listen()
    {
        ConnectionThreadHandler socketHandler = new ConnectionThreadHandler(this, socket);
        ConnectionThreadHandler rmiHandler = new ConnectionThreadHandler(this, rmi);
        Thread socketT = new Thread(socketHandler);
        Thread rmiT = new Thread(rmiHandler);
        socketT.start();
        rmiT.start();
    }

    /**
     * Checks for server going to stop (running stopping routine)
     * @return true in case of running stopping  routine, false elsewhere
     */
    public boolean inStopProcedure() {
        return stop;
    }

    public void newConnection(Connection connection) {
        synchronized (lock) {
            println("Rilevata nuova connessione");
            try {
                boolean acceptedNick = true;
                Player player = null;
                String nickname;
                do {
                    acceptedNick = true;

                    nickname = connection.getNickname();
                    for (Match m : matches) {
                        for (Player p : m.getGame().getPlayers()) {
                            if (p.getNick().equals(nickname)) {
                                if (p.getConn() != null)
                                    acceptedNick = false;
                                else
                                    player = p;
                            }
                        }
                    }

                    for (int i = 0; i < waiting.length; i++) {
                        if (waiting[i] != null) {
                            for (Player p : waiting[i].getGame().getPlayers()) {
                                if (p.getNick().equals(nickname)) {
                                    acceptedNick = false;
                                }
                            }
                        }
                    }
                } while (!acceptedNick);

                if (player != null) {
                    player.setConn(connection);

                    println("Il giocatore " + player.getNick() + " si è riconnesso.");
                } else {
                    int skulls = connection.getSkullNum();
                    int index = skulls - MINSKULLS;

                    List<Fighter> available = new ArrayList<>();
                    Collections.addAll(available, Fighter.values());
                    if(waiting[index] != null) {
                        for(Player p: waiting[index].getGame().getPlayers()) {
                            available.remove(p.getCharacter());
                        }
                    }

                    String phrase = connection.getPhrase();
                    Fighter fighter = connection.getFighter(available);
                    player = new Player(nickname, phrase, fighter);
                    player.setConn(connection);

                    if (waiting[index] == null) {
                        try {
                            waiting[index] = new Match(skulls);
                        } catch (FileNotFoundException e) {
                            Logger.getGlobal().log(Level.SEVERE, e.toString(), e);
                        }
                    }
                    waiting[index].getGame().addPlayer(player);
                    if (waiting[index].getGame().getPlayers().size() >= 3) {
                        if (!startedTimer[index]) {
                            matchTimer(skulls);
                            startedTimer[index] = true;
                        } else if (waiting[index].getGame().getPlayers().size() == 5) {
                            cancelTimer(skulls);
                            startMatch(skulls);
                        }
                    }

                    println("Il giocatore " + player.getNick() + " si è connesso.");
                }
                player.getConn().sendMessage("Benvenuto in Adrenalina!");
            }
            catch (ClientDisconnectedException e) {
                Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
                println("Nuova connessione annullata");
            }
        }
    }

    public void cancelConnection(String nickname) {
        synchronized (lock) {
            boolean found = false;
            int i;
            for (i = 0; i < waiting.length && !found; i++) {
                for (Player p : waiting[i].getGame().getPlayers()) {
                    if (p.getNick().equals(nickname)) {
                        String nick = p.getNick();
                        println("Giocatore " + nick + " disconnesso");
                        waiting[i].getGame().removePlayer(p);
                        found = true;
                    }
                }
            }
            if (!found)  {
                for (Match m : matches) {
                    for (Player p : m.getGame().getPlayers()) {
                        if (p.getNick().equals(nickname)) {
                            println("Giocatore " + p.getNick() + " rimosso dalla lista di attesa");
                            p.setConn(null);
                        }
                    }
                }
            }
        }
    }

    private void cancelTimer(int skulls) {
        timer[skulls - MINSKULLS].cancel();
        timer[skulls - MINSKULLS].purge();
        timer[skulls - MINSKULLS] = null;
    }

    private void matchTimer(int skulls) {
        //TODO make configuration file to set waiting time
        timer[skulls - MINSKULLS] = new Timer();
        timer[skulls - MINSKULLS].schedule(new TimerTask() {
            @Override
            public void run() {
                startMatch(skulls);
            }
        }, ((long)10)*1000);
    }

    private void startMatch(int skulls) {
        synchronized (lock) {
            println("Partita in avvio...");
            int index = skulls - MINSKULLS;

            for(Player p: waiting[index].getGame().getPlayers()) {
                try {
                    p.getConn().sendMessage("Partita in avvio...");
                }
                catch (ClientDisconnectedException e) {
                    Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
                    cancelConnection(p.getNick());
                }
            }

            if(waiting[index].getGame().getPlayers().size() >= 3) {
                println("Partita avviata");
                matches.add(waiting[index]);
                Thread matchThread = new Thread (matches.get(matches.indexOf(waiting[index])));
                matchThread.start();
                waiting[index] = null;
                startedTimer[index] = false;
            }
            else {
                println("Ripristino - giocatori insufficienti");
                for(Player p: waiting[index].getGame().getPlayers()) {
                    try {
                        p.getConn().sendMessage("Errore - troppi utenti disconnessi. Ripristino a stanza di attesa");
                    }
                    catch (ClientDisconnectedException e) {
                        Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
                        cancelConnection(p.getNick());
                    }
                }
            }
        }
    }
}