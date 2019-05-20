package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.model.Player;

public class ClientConnThread implements Runnable {
    private Player player;
    private SMain main;

    public ClientConnThread(SMain main, Player player) {
        this.player = player;
        this.main = main;
    }

    public synchronized void run() {
        try {
            while(player.getConn() != null) {
                player.getConn().clientPing();
                wait(1000);
            }
            throw new ClientDisconnectedException();
        }
        catch (ClientDisconnectedException e) {
            main.cancelConnection(player.getNick());
            return;
        }
        catch (InterruptedException e) { ; }
    }
}
