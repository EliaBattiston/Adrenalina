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
     * Creates a new CMain
     */
    public AIMain()
    {
        //Temporary test for the login

        String buffer = "";
        String ip;
        boolean socket = true;

        Scanner stdin = new Scanner(System.in);

        //RMI or Socket?
        /*while (!buffer.toLowerCase().equals("r") && !buffer.toLowerCase().equals("s"))
        {
            System.out.print("Connessione con [S]ocket o con [R]mi? ");
            buffer = stdin.nextLine();
        }

        if(buffer.toLowerCase() == "r")
            socket = false;*/

        /*//Ask for IP address
        System.out.print("Indirizzo IP del server: ");
        buffer = stdin.nextLine();
        //TODO check if IP is correctly written
        ip = buffer;*/

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
