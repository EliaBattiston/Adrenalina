package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.UsedNameException;

public class ConnectionThreadHandler implements Runnable {
    private Server server;
    private SMain main;

    public ConnectionThreadHandler(SMain main, Server server) {
        this.server = server;
        this.main = main;
    }

    public synchronized void run() {
        while(!main.inStopProcedure()) {
            main.newConnection(server.getConnection());
        }
    }
}
