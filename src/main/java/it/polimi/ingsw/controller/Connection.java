package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

/**
 * Connection to a single player
 */
public abstract class Connection
{
    /**
     * Corresponding player that resides in the game class of the same match
     */
    private Player player;

    /**
     * Creates a new empty connection
     */
    Connection()
    {

    }

    /**
     * Sends the payload to the client
     * @param payload Content to be delivered to the client
     */
    public abstract void send(String payload);

    /**
     * Waits for a response of the client
     * @return Payload sent by the client
     */
    public abstract String receive();

    /**
     * Gives the player linked to the connection
     * @return The player linked to the connection
     */
    public Player getPlayer()
    {
        return this.player;
    }
}
