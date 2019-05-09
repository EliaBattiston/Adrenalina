package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.view.UserInterface;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for the AIClient
 */
public class AIMain
{
    /**
     * Client's representation of the game data
     */
    private Game game;

    /**
     * Connection to the server
     */
    private Client connection;


    /**
     * Endpoint for interface control
     */
    private UserInterface ui;

    /**
     * Creates a new AIMain
     */
    public AIMain()
    {
        //Client used to test

        String ip;
        ip = "localhost";

        try {
            connection = new AIClient(ip, 1906);
        }catch (IOException e){
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
        }
    }

    public static void main(String[] args)
    {
        AIMain base = new AIMain();
    }
}
