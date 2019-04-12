package it.polimi.ingsw.controller;

/**
 * Client's connection to the socket
 */
public interface Client
{
    /**
     * Sends the payload to the client
     * @param payload Content to be delivered to the client
     * @return true on success, false in case of connection error
     */
    boolean send(String payload);

    /**
     * Waits for a response of the client
     * @return Payload sent by the client
     */
    String receive();
}
