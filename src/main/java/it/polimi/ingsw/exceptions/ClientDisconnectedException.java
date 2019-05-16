package it.polimi.ingsw.exceptions;

/**
 * Exception thrown when a client disconnects
 */
public class ClientDisconnectedException extends Exception {
    public ClientDisconnectedException() {super("Client disconnected"); }
}
