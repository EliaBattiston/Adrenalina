package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

/**
 * Manager for the whole logic of turns and game events
 */
public class Logic implements Runnable
{
    /**
     * Currently active player
     */
    private Player active;

    /**
     * Number of remaining actions for the currently active player
     */
    private int actionsNumber;

    /**
     * Creates an empty logic controller
     */
    Logic()
    {

    }

    /**
     * Runs the main logic of the game in a thread
     */
    public void run()
    {}
}
