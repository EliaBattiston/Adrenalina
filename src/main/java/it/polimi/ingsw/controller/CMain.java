package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ServerDisconnectedException;
import it.polimi.ingsw.exceptions.ServerNotFoundException;
import it.polimi.ingsw.view.CLInterface;
import it.polimi.ingsw.view.UserInterface;

import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for the client's executable
 */
public class CMain
{
    /**
     * Endpoint for interface control
     */
    private UserInterface ui;

    /**
     * Creates a new CMain
     * @param gui True to start the GUI interface, false for CLI
     */
    public CMain(boolean gui)
    {
        Client connection;

        //Temporary test for the login

        String buffer = "";
        String ip;
        boolean socket = true;

        Scanner stdin = new Scanner(System.in);

        if(!gui) {
            ui = new CLInterface();
        }
        else {
            //TODO Instance GUI Interface
            ui = null;
        }

        //RMI or Socket?
        while (!buffer.equalsIgnoreCase("r") && !buffer.equalsIgnoreCase("s"))
        {
            System.out.print("Connessione con [S]ocket o con [R]mi? ");
            buffer = stdin.nextLine();
        }

        if(buffer.equalsIgnoreCase("r"))
            socket = false;

        boolean instanced = false;
        do {
            //Ask for IP address
            System.out.print("Indirizzo IP del server: ");
            buffer = stdin.nextLine();
            //TODO check if IP is correctly written
            ip = buffer;

            try {
                if (socket)
                    connection = new SocketClient(ip, 1906, ui);
                else
                    connection = new RMIClient(ip, ui);
                instanced = true;
                ui.generalMessage("Connesso al server Adrenalina");
            }
            catch (ServerNotFoundException e) {
                ui.generalMessage("Server non trovato, riprova\n");
            }
            catch (ServerDisconnectedException e) {
                ui.generalMessage("Server disconnesso inaspettatamente, rilancia il client e riprova\n");
                return;
            }
            catch (RemoteException e) {
                Logger.getGlobal().log(Level.SEVERE, e.toString(), e);
            }

        }
        while (!instanced);
    }

}
