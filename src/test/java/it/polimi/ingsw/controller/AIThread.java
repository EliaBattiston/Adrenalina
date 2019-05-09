package it.polimi.ingsw.controller;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AIThread extends Thread {
    private boolean done;

    public boolean isDone() {
        return done;
    }

    @Override
    public void run() {
        String[] args = new String[2];
        Client connection;

        done = false;

        try {
            connection = new AIClient("localhost", 1906);
            done = true;
        }catch (IOException e){
            ;
        }
    }
}
