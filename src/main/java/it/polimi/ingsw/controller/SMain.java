package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.CellAdapter;
import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Player;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
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

    /**
     * List of matches saved from precedent session
     */
    private List<Match> loadedMatches;

    private boolean stop;
    private Timer[] timer;
    Object lock, cancelLock;
    Object matchLock[];
    private static final int MINSKULLS = 5;
    private boolean[] startedTimer;

    /**
     * Creates a new SMain
     * @param passedIp IP address to use
     */
    public SMain(String passedIp)
    {

        lock = new Object();
        cancelLock = new Object();
        matchLock = new Object[8 - MINSKULLS + 1];
        for(int i = 0; i <  matchLock.length; i++) {
            matchLock[i] = new Object();
        }

        try {
            String localIP;

            if(passedIp == null || passedIp.equals("")) {
                List<String> addresses = new ArrayList<>();

                Enumeration<NetworkInterface> nInterfaces = NetworkInterface.getNetworkInterfaces();
                while (nInterfaces.hasMoreElements()) {
                    Enumeration<InetAddress> inetAddresses = nInterfaces
                            .nextElement().getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        String address = inetAddresses.nextElement()
                                .getHostAddress();
                        if (address.contains(".")) {
                            String[] split = address.split("\\.");
                            if (!split[0].equals("127") && !split[0].equals("169"))
                                addresses.add(address);
                        }
                    }
                }

                if (addresses.size() > 1) {
                    System.out.println("Seleziona l'IP della macchina");
                    for (int i = 0; i < addresses.size(); i++) {
                        System.out.println("[" + (i + 1) + "] " + addresses.get(i));
                    }
                    Scanner in = new Scanner(System.in);
                    int pos;
                    do {
                        System.out.print("Selezione [1-" + addresses.size() + "]: ");
                        localIP = in.nextLine();
                        pos = Integer.parseInt(localIP) - 1;
                    }
                    while (pos < 0 || pos >= addresses.size());
                    localIP = addresses.get(pos);
                } else {
                    localIP = addresses.get(0);
                }

            }
            else
                localIP = passedIp;

            System.setProperty("java.rmi.server.hostname", localIP);

            socket = new SocketServer(1906);
            rmi = new RMIServer();
            matches = new ArrayList<>();
            loadedMatches = new ArrayList<>();

            //Persistency
            Gson gson = new GsonBuilder().registerTypeAdapter(Cell.class, new CellAdapter()).create();
            JsonReader reader;
            File matchesDir = new File("matches/");
            File[] listOfMatches = matchesDir.listFiles();

            if(listOfMatches != null) {
                for (File file : listOfMatches) {
                    if (file.isFile()) {
                        try {
                            reader = new JsonReader(new FileReader(file));
                            loadedMatches.add(gson.fromJson(reader, Match.class));
                            reader.close();
                            file.delete();
                        }
                        catch(Exception e){
                            Logger.getGlobal().log(Level.SEVERE, file.getName() + " is corrupted");
                        }
                    }
                }

                for (Match m : loadedMatches) {
                    m.getGame().getMap().fixPawns(m.getGame().getPlayers());
                    m.fixActive();
                }
            }

            stop = false;

            waiting = new Match[8 - MINSKULLS + 1];
            timer = new Timer[8 - MINSKULLS + 1];
            startedTimer = new boolean[8 - MINSKULLS + 1];

            for(int i = 0; i < timer.length; i++) {
                timer[i] = new Timer();
                startedTimer[i] = false;
            }

            println("    ___    __  _______  _____    _____                          ");
            println("   /   |  /  |/  / __ \\/ ___/   / ___/___  ______   _____  _____");
            println("  / /| | / /|_/ / / / / __ \\    \\__ \\/ _ \\/ ___/ | / / _ \\/ ___/");
            println(" / ___ |/ /  / / /_/ / /_/ /   ___/ /  __/ /   | |/ /  __/ /    ");
            println("/_/  |_/_/  /_/\\____/\\____/   /____/\\___/_/    |___/\\___/_/     ");
            println("");

            println("Adrenalina Server ready");

            Runtime.getRuntime().addShutdownHook(
                new Thread("app-shutdown-hook") {
                    @Override
                    public void run() {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Cell.class, new CellAdapter()).create();
                        for(Match m: loadedMatches) {
                            try (PrintWriter out = new PrintWriter("matches/" + m.hashCode() + ".adr")) {
                                out.println(gson.toJson(m));
                            } catch (FileNotFoundException e) {
                                Logger.getGlobal().log(Level.SEVERE, "Error in writing persistance file", e);
                            }
                        }
                    }
                });

            listen();
        }
        catch (SocketException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
            System.out.println("Impossibile trovate interfacce di rete, riprova");
        }
        catch (RemoteException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
            println("Errore nell'avvio del server RMI");
        }
        catch (IOException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
            println("Errore nell'avvio del server Socket");
        }
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
        println("Rilevata nuova connessione");
        try {
            boolean acceptedNick = true;
            Player player = null;
            String nickname;
            synchronized (lock) {
                do {
                    acceptedNick = true;

                    nickname = connection.getNickname();

                    for (Match m : loadedMatches) {
                        for (Player p : m.getGame().getPlayers()) {
                            if (p.getNick().equals(nickname)) {
                                if (p.getConn() != null)
                                    acceptedNick = false;
                                else {
                                    player = p;
                                    List<Player> broadcast = new ArrayList<>();
                                    broadcast.addAll(m.getGame().getPlayers());
                                    broadcast.remove(p);
                                    m.broadcastMessage(p.getNick() + " si è riconnesso", broadcast);
                                }
                            }
                        }
                    }

                    for (Match m : matches) {
                        for (Player p : m.getGame().getPlayers()) {
                            if (p.getNick().equals(nickname)) {
                                if (p.getConn() != null)
                                    acceptedNick = false;
                                else {
                                    player = p;
                                    List<Player> broadcast = new ArrayList<>();
                                    broadcast.addAll(m.getGame().getPlayers());
                                    broadcast.remove(p);
                                    m.broadcastMessage(p.getNick() + " si è riconnesso", broadcast);
                                }
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
            }

            if (player != null) {
                player.setConn(connection);

                for(int i = 0; i < loadedMatches.size(); i++) {
                    Match m = loadedMatches.get(i);
                    Boolean complete = true;
                    for(Player p: m.getGame().getPlayers()) {
                        if(p.getConn() == null)
                            complete = false;
                    }
                    if(complete) {
                        matches.add(m);
                        Thread matchThread = new Thread (m);
                        matchThread.start();
                        loadedMatches.remove(m);
                        i--;
                    }
                }

                println("Il giocatore " + player.getNick() + " si è riconnesso.");
                player.getConn().sendMessage("Bentornato in Adrenalina! La tua partita è ancora in corso, aspetta il caricamento dalla mappa");
                ClientConnThread clientT = new ClientConnThread(this, player);
                new Thread(clientT).start();
            }
            else {
                int skulls = connection.getSkullNum();
                int index = skulls - MINSKULLS;

                String phrase = connection.getPhrase();
                Fighter fighter;

                synchronized (matchLock[index]) {
                    List<Fighter> available = new ArrayList<>();
                    Collections.addAll(available, Fighter.values());
                    if (waiting[index] != null) {
                        for (Player p : waiting[index].getGame().getPlayers()) {
                            available.remove(p.getCharacter());
                        }
                    }

                    fighter = connection.getFighter(available);
                }
                player = new Player(nickname, phrase, fighter);
                player.setConn(connection);
                ClientConnThread clientT = new ClientConnThread(this, player);
                new Thread(clientT).start();

                if (waiting[index] == null) {
                    try {
                        waiting[index] = new Match(skulls);
                    } catch (FileNotFoundException e) {
                        Logger.getGlobal().log(Level.SEVERE, e.toString(), e);
                    }
                }
                waiting[index].getGame().addPlayer(player);
                player.getConn().sendMessage("Benvenuto in Adrenalina!");

                waiting[index].broadcastMessage("Utenti attualmente connessi alla waiting room: " + waiting[index].getGame().getPlayers().size(), waiting[index].getGame().getPlayers());

                println("Il giocatore " + player.getNick() + " si è connesso.");

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
        catch (ClientDisconnectedException e) {
            println("Nuova connessione annullata");
        }
    }

    public void cancelConnection(String nickname) {
        synchronized (cancelLock) {
            boolean found = false;
            int i;
            for (i = 0; i < waiting.length && !found; i++) {
                if(waiting[i] != null) {
                    for (Player p : waiting[i].getGame().getPlayers()) {
                        if (p.getNick().equals(nickname)) {
                            String nick = p.getNick();
                            println("Giocatore " + nick + " rimosso dalla lista di attesa");
                            waiting[i].getGame().removePlayer(p);
                            if (waiting[i].getGame().getPlayers().size() == 2)
                            {
                                cancelTimer(i + MINSKULLS);
                                waiting[i].broadcastMessage("Avvio partita annullato", waiting[i].getGame().getPlayers());
                            }
                            found = true;
                        }
                    }
                }
            }
            if (!found)  {
                for (Match m : matches) {
                    for (Player p : m.getGame().getPlayers()) {
                        if (p.getNick().equals(nickname)) {
                            println("Giocatore " + p.getNick() + " disconnesso");
                            p.getConn().cancelConnection();
                            p.setConn(null);
                        }
                    }
                }
            }
        }
    }

    private void cancelTimer(int skulls) {
        int index = skulls - MINSKULLS;
        timer[index].cancel();
        timer[index].purge();
        timer[index] = null;
        startedTimer[index] = false;

    }

    private void matchTimer(int skulls) {
        long seconds = Configuration.getInstance().getStartMatchSeconds();
        timer[skulls - MINSKULLS] = new Timer();
        timer[skulls - MINSKULLS].schedule(new TimerTask() {
            @Override
            public void run() {
                startMatch(skulls);
            }
        }, seconds*1000);
        for(Player p: waiting[skulls - MINSKULLS].getGame().getPlayers()) {
            try {
                p.getConn().sendMessage("La partita si avvierà fra " + seconds + " secondi");
            }
            catch (ClientDisconnectedException e) {
                cancelConnection(p.getNick());
            }
        }
        println("Timer partita avviato");
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
                    cancelConnection(p.getNick());
                }
            }

            if(waiting[index].getGame().getPlayers().size() >= 3) {
                println("Partita avviata");
                Match match = waiting[index];
                waiting[index] = null;
                matches.add(match);
                Thread matchThread = new Thread (match);
                matchThread.start();

                Thread waitingComplete = new Thread(() -> {
                    try {
                        matchThread.join();
                        matches.remove(match);
                    }
                    catch (InterruptedException e) {
                        ;
                    }
                });
                waitingComplete.start();

                startedTimer[index] = false;
            }
            else {
                println("Ripristino - giocatori insufficienti");
                waiting[index].broadcastMessage("Errore - troppi utenti disconnessi. Ripristino a stanza di attesa", waiting[index].getGame().getPlayers());
            }
        }
    }
}