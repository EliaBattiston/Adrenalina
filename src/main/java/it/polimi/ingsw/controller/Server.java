package it.polimi.ingsw.controller;

import java.io.Serializable;

/**
 * Server waiting for connections
 */
public interface Server extends Serializable
{
    /**
     * returns a new connection to the server (blocking method)
     * @return new Connection object
     */
    Connection getConnection();
}
