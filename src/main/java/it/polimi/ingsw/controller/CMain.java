package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.UserInterface;

import java.util.Scanner;

/**
 * Main class for the client's executable
 */
public class CMain
{
    /**
     * Client's representation of the game data
     */
    Game game;

    /**
     * Connection to the server
     */
    Client connection;


    /**
     * Endpoint for interface control
     */
    UserInterface ui;

    /**
     * Creates a new CMain
     */
    public CMain()
    {}

    public static void main(String[] args)
    {
        System.out.println("I'm a client");
    }
}
