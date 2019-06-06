package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ServerDisconnectedException;
import it.polimi.ingsw.exceptions.ServerNotFoundException;
import it.polimi.ingsw.view.GuiAInterface;
import it.polimi.ingsw.view.UserInterface;

/**
 * Main class for the AInterface
 */
public class AIMain
{
    /**
     * Endpoint for interface control
     */
    private UserInterface ui;

    /**
     * Creates a new AIMain
     */
    public AIMain(boolean gui)
    {
        String ip = "localhost";

        if(gui)
            ui = new GuiAInterface();
        else
            ui = new AInterface();

        try {
            new SocketClient(ip, 1906, ui);
        }
        catch (ServerNotFoundException e) {
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
            new SocketClient(ip, 1906, ui);
        }
        catch (ServerNotFoundException e) {
            ui.generalMessage("Server non trovato, riprova\n");
        }
        catch (ServerDisconnectedException e) {
            ui.generalMessage("Server disconnesso inaspettatamente, rilancia il client e riprova\n");
        }
    }
}
