package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.UsedNameException;
import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
            System.out.println("Server ready");
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

    public static void main(String[] args)
    {
        System.out.println("Welcome to Adrenalina game server!");
        SMain server = new SMain();
        server.listen();
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

                for (int i=0; i < waiting.length; i++) {
                    if(waiting[i] != null)
                    {
                        for (Player p : waiting[i].getGame().getPlayers())
                        {
                            if (p.getNick().equals(nickname))
                            {
                                acceptedNick = false;
                            }
                        }
                    }
                }
            } while (!acceptedNick);

            if (player != null) {
                player.setConn(connection);

                System.out.println("Il giocatore " + player.getNick() + " si è riconnesso.");
            } else {
                String phrase = connection.getPhrase();
                Fighter fighter = connection.getFighter();
                player = new Player(nickname, phrase, fighter);
                player.setConn(connection);

                System.out.println("Il giocatore " + player.getNick() + " si è connesso.");

                int skulls = connection.getSkullNum();

                int index = skulls - MINSKULLS;
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
                        waiting[i].getGame().removePlayer(p);
                        found = true;
                    }
                }
            }
            if (!found)  {
                for (Match m : matches) {
                    for (Player p : m.getGame().getPlayers()) {
                        if (p.getNick().equals(nickname)) {
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
    }

    private void matchTimer(int skulls) {
        //TODO make configuration file to set waiting time
        timer[skulls - MINSKULLS].schedule(new TimerTask() {
            @Override
            public void run() {
                startMatch(skulls);
            }
        }, 3*1000);
    }

    private void startMatch(int skulls) {
        synchronized (lock) {
            int index = skulls - MINSKULLS;
            matches.add(waiting[index]);
            matches.get(matches.indexOf(waiting[index])).run();
            waiting[index] = null;
            startedTimer[index] = false;
        } //FIXME AI throwed StackOverflowException for GSON
    }
}