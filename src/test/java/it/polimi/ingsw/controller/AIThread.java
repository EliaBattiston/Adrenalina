package it.polimi.ingsw.controller;

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
        /*String[] args = new String[2];
        Client connection;
        UserInterface ui = new CLInterface();

        done = false;

        try {
            connection = new AIClient("localhost", 1906, ui);
            done = true;
        }catch (IOException e){
            ;
        }*/
    }
}
