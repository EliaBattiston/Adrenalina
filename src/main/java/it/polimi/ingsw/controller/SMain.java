package it.polimi.ingsw.controller;

import java.util.List;

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
    {}

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