package it.polimi.ingsw.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
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
    private Match waiting;

    /**
     * List containing all currently active matches
     */
    private List<Match> matches;

    /**
     * Creates a new SMain
     */
    public SMain()
    {
        try {
            socket = new SocketServer(1906);
            rmi = new RMIServer();
            matches = new ArrayList<>();
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
        System.out.println("I'm a server");
    }

    /**
     * Starts listening for new users' connections
     */
    public void listen()
    {

    }
}