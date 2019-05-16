package it.polimi.ingsw.exceptions;

/**
 *Exception thrown when the server is not found (wrong ip and/or port)
 */
public class ServerNotFoundException extends Exception {
    public ServerNotFoundException()
    {
        super("Server not found");
    }
}
