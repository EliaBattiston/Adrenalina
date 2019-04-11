package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

/**
 * Connection to a single player
 */
public interface Connection
{
    /**
     * Sends the payload to the client
     * @param payload Content to be delivered to the client
     */
    void send(String payload);

    /**
     * Waits for a response of the client
     * @return Payload sent by the client
     */
    String receive();
}
