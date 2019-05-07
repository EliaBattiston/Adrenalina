package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.CLInterface;
import it.polimi.ingsw.view.UserInterface;

import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Main class for the client's executable
 */
public class CMain
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
    public CMain()
    {
        //Temporary test for the login

        String buffer = "";
        String ip;
        boolean socket = true;

        Scanner stdin = new Scanner(System.in);

        while (!buffer.toLowerCase().equals("g") && !buffer.toLowerCase().equals("c"))
        {
            System.out.print("Interfaccia GUI [g] o CLI [c]? ");
            buffer = stdin.nextLine();
        }

        if(buffer.toLowerCase().equals("c")) {
            //TODO implemet CLInterface declaration
        }

        //RMI or Socket?
        while (!buffer.toLowerCase().equals("r") && !buffer.toLowerCase().equals("s"))
        {
            System.out.print("Connessione con [S]ocket o con [R]mi? ");
            buffer = stdin.nextLine();
        }

        if(buffer.toLowerCase() == "r")
            socket = false;

        //Ask for IP address
        System.out.print("Indirizzo IP del server: ");
        buffer = stdin.nextLine();
        //TODO check if IP is correctly written
        ip = buffer;

        if(socket)
        {
            connection = new SocketClient(ip, 1906, ui);
        }
        else
        {
            try
            {
                connection = new RMIClient(ip, ui);
            }
            catch(RemoteException e)
            {
                System.out.println("Errore di connessione RMI");
            }
        }
    }

    public static void main(String[] args)
    {
        CMain base = new CMain();
    }
}
