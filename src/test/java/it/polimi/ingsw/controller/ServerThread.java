package it.polimi.ingsw.controller;

public class ServerThread extends Thread {
    @Override
    public void run() {
        String[] args = new String[2];
        SMain.main(args);
    }
}
