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

import static it.polimi.ingsw.controller.Match.broadcastMessage;

/**
 * Main class for the server's executable
 */
public class SMain
{
    private static final int MINSKULLS = 5;
    private static final int MAXSKULLS = 8;
    private static final int SOCKETPORT = 1906;
    private static final int MAX_PLAYERS_NUMBER = 5;

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

    private Timer[] timer;
    private final Object lock;
    private final Object cancelLock;
    private final Object[] matchLock;
    private final int minPlayers;
    private boolean[] startedTimer;


    public static void main(String[] args) {
        System.setProperty("java.security.policy", "AM06.policy");
        List<String> flags = Arrays.asList(args);
        if(flags.contains("-l"))
            new SMain("localhost");
        else
            new SMain(null);
    }
    /**
     * Creates a new SMain
     * @param passedIp IP address to use
     */
    public SMain(String passedIp)
    {
        minPlayers = Configuration.getInstance().getMinPlayers();

        lock = new Object();
        cancelLock = new Object();
        matchLock = new Object[MAXSKULLS - MINSKULLS + 1];
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
                    println("Seleziona l'IP della macchina");
                    for (int i = 0; i < addresses.size(); i++) {
                        println("[" + (i + 1) + "] " + addresses.get(i));
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

            socket = new SocketServer(SOCKETPORT);
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
                            if(file.delete())
                                println("File" + file.getName() + " correctly deleted");
                            else
                                println("Couldn't delete file" + file.getName());

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

            waiting = new Match[MAXSKULLS - MINSKULLS + 1];
            timer = new Timer[MAXSKULLS - MINSKULLS + 1];
            startedTimer = new boolean[MAXSKULLS - MINSKULLS + 1];

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

            if(!loadedMatches.isEmpty())
                println("Sono state caricate " + loadedMatches.size() + " partite salvate");
            println("Server IP: " + localIP + "\n");
            println("Adrenalina Server ready\n");

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
            println("Impossibile trovate interfacce di rete, riprova");
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

    protected void println(String payload) {
        System.out.println(payload);
    }

    public boolean inStopProcedure() { return false; }

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

                    List<Match> allMatches = new ArrayList<>();
                    allMatches.addAll(loadedMatches);
                    allMatches.addAll(matches);

                    for (Match m : allMatches) {
                        for (Player p : m.getGame().getPlayers()) {
                            if (p.getNick().equals(nickname)) {
                                if (p.getConn() != null)
                                    acceptedNick = false;
                                else {
                                    player = p;
                                    List<Player> broadcast = new ArrayList<>();
                                    broadcast.addAll(m.getGame().getPlayers());
                                    broadcast.remove(p);
                                    broadcastMessage(p.getNick() + " si è riconnesso", broadcast);
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
                    boolean complete = true;
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

                    player = new Player(nickname, phrase, fighter);
                    player.setConn(connection);
                    ClientConnThread clientT = new ClientConnThread(this, player);
                    new Thread(clientT).start();

                    if (waiting[index] == null)
                        waiting[index] = new Match(skulls);

                    waiting[index].getGame().addPlayer(player);
                }
                player.getConn().sendMessage("Benvenuto in Adrenalina!");

                broadcastMessage("Utenti attualmente connessi alla waiting room: " + waiting[index].getGame().getPlayers().size(), waiting[index].getGame().getPlayers());

                println("Il giocatore " + player.getNick() + " si è connesso.");

                if (waiting[index].getGame().getPlayers().size() >= minPlayers) {
                    if (!startedTimer[index]) {
                        matchTimer(skulls);
                        startedTimer[index] = true;
                    } else if (waiting[index].getGame().getPlayers().size() == MAX_PLAYERS_NUMBER) {
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
                            if (waiting[i].getGame().getPlayers().size() == minPlayers - 1)
                            {
                                cancelTimer(i + MINSKULLS);
                                broadcastMessage("Avvio partita annullato", waiting[i].getGame().getPlayers());
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

            if(waiting[index].getGame().getPlayers().size() >= minPlayers) {
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
                    catch (InterruptedException ignore) {
                        Thread.currentThread().interrupt();
                    }
                });
                waitingComplete.start();

                startedTimer[index] = false;
            }
            else {
                println("Ripristino - giocatori insufficienti");
                broadcastMessage("Errore - troppi utenti disconnessi. Ripristino a stanza di attesa", waiting[index].getGame().getPlayers());
            }
        }
    }

}