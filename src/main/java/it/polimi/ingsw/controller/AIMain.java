package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ServerDisconnectedException;
import it.polimi.ingsw.exceptions.ServerNotFoundException;
import it.polimi.ingsw.view.AInterface;
import it.polimi.ingsw.view.GuiAInterface;
import it.polimi.ingsw.view.UserInterface;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

/**
 * Main class for the AInterface
 */
public class AIMain
{
    private static final int SOCKETPORT = 1906;

    /**
     * Endpoint for interface control
     */
    private UserInterface ui;

    public static void main(String[] args) {
        System.setProperty("java.security.policy", "AM06.policy");
        List<String> flags = Arrays.asList(args);

        boolean rmi = false;
        if(flags.contains("-rmi"))
            rmi = true;
        if(flags.contains("-g"))
            new AIMain(true, rmi);
        else
            new AIMain(false, rmi);
    }

    /**
     * Creates a new AIMain
     */
    public AIMain(boolean gui, boolean rmi)
    {
        String ip = "localhost";

        if(gui)
            ui = new GuiAInterface();
        else
            ui = new AInterface();

        try {
            if(rmi)
                new RMIClient("localhost", ui);
            else
                new SocketClient(ip, SOCKETPORT, ui);
        }
        catch (ServerNotFoundException | RemoteException e) {
            ui.generalMessage("Server non trovato, riprova\n");
        }
        catch (ServerDisconnectedException e) {
            ui.generalMessage("Server disconnesso inaspettatamente, rilancia il client e riprova\n");
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
            new SocketClient(ip, SOCKETPORT, ui);
        }
        catch (ServerNotFoundException e) {
            ui.generalMessage("Server non trovato, riprova\n");
        }
        catch (ServerDisconnectedException e) {
            ui.generalMessage("Server disconnesso inaspettatamente, rilancia il client e riprova\n");
        }
    }
}
