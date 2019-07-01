package it.polimi.ingsw.controller;

public class ConnectionThreadHandler implements Runnable {
    private Server server;
    private SMain main;

    public ConnectionThreadHandler(SMain main, Server server) {
        this.server = server;
        this.main = main;
    }

    public synchronized void run() {
        while(!main.inStopProcedure()) {
            Connection newConn = server.getConnection();
            Thread t = new Thread(()-> main.newConnection(newConn));
            t.start();
        }
    }
}
