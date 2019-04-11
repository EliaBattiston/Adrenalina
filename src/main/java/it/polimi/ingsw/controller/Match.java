package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;

import java.util.List;

/**
 * Class containing every information needed for the execution of a single match
 */
public class Match
{
    /**
     * Model of the match's game, containing all relevant information
     */
    private Game game;

    /**
     * Connections of all the players participating the match
     */
    private List<Connection> players;

    /**
     * Controller of this match's logic
     */
    private Logic controller;

    /**
     * Creates a new empty match
     */
    Match()
    {

    }

    /**
     * Gets the game model of the match
     * @return The match's game model
     */
    public Game getGame()
    {
        return game;
    }

    /**
     * Gets the list of player's connections
     * @return List of connections
     */
    public List<Connection> getPlayers()
    {
        return players;
    }

    /**
     * Gets the match's controller
     * @return The match's controller
     */
    public Logic getController()
    {
        return controller;
    }
}
