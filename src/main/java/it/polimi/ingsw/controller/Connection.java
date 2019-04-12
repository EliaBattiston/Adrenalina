package it.polimi.ingsw.controller;

/**
 * Connection to a single player
 */
public interface Connection
{
    /**
     * Sends the payload to the client
     * @param payload Content to be delivered to the client
     * @return true on success, false in case of connection error
     */
    boolean send(String payload);

    /**
     * Waits for a response of the client
     * @return Payload sent by the client (null in case of connection error)
     */
    String receive();
}
