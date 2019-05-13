package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ServerDisconnectedException;
import it.polimi.ingsw.exceptions.ServerNotFoundException;
import it.polimi.ingsw.view.CLInterface;
import it.polimi.ingsw.view.UserInterface;

import java.io.IOException;

public class AIThread extends Thread {
    private boolean done;

    public boolean isDone() {
        return done;
    }

    @Override
    public void run() {
        String[] args = new String[2];
        Client connection;
        UserInterface ui = new AInterface();

        done = false;

        try {
            connection = new SocketClient("localhost", 1906, ui);
            done = true;
        }
        catch (ServerNotFoundException e) {
            ui.generalMessage("Server non trovato, riprova\n");
        }
        catch (ServerDisconnectedException e) {
            ui.generalMessage("Server disconnesso inaspettatamente, rilancia il client e riprova\n");
            return;
        }
    }
}
