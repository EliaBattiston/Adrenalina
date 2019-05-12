package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ServerDisconnectedException;
import it.polimi.ingsw.exceptions.ServerNotFoundException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.view.CLInterface;
import it.polimi.ingsw.view.UserInterface;

import java.io.IOException;
import java.rmi.RemoteException;
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
        String ip = "localhost";
        ui = new AInterface();

        try {
            connection = new SocketClient(ip, 1906, ui);
        }
        catch (ServerNotFoundException e) {
            ui.generalMessage("Server non trovato, riprova\n");
        }
        catch (ServerDisconnectedException e) {
            ui.generalMessage("Server disconnesso inaspettatamente, rilancia il client e riprova\n");
            return;
        }
    }

    /**
     * Creates a new AIMain referring a specified IP address for the server
     * @param ip server IP
     */
    public AIMain(String ip)
    {
        ui = new AInterface();

        try {
            connection = new SocketClient(ip, 1906, ui);
        }
        catch (ServerNotFoundException e) {
            ui.generalMessage("Server non trovato, riprova\n");
        }
        catch (ServerDisconnectedException e) {
            ui.generalMessage("Server disconnesso inaspettatamente, rilancia il client e riprova\n");
            return;
        }
    }

    public static void main(String[] args)
    {
        AIMain base = new AIMain();
    }
}
