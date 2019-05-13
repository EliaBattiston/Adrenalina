package it.polimi.ingsw.controller;

/**
 * Server waiting for connections
 */
public interface Server
{
    /**
     * returns a new connection to the server (blocking method)
     * @return new Connection object
     */
    Connection getConnection();
}
