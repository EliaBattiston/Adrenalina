package it.polimi.ingsw.exceptions;

/**
 * Exception thrown when server disconnects unexpectedly
 */
public class ServerDisconnectedException extends Exception {
    public ServerDisconnectedException()
    {
        super("Server disconnected unexpectedly");
    }
}
