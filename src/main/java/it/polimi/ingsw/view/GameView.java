package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.model.*;

import java.io.Serializable;
import java.util.List;

/**
 * View of the game for the current player.
 * From the model.Game class the missing parts are: the decks, the other players data
 * From the controller.Match class it gets:
 */
public class GameView implements Serializable {
    /**
     * Map used in this match
     */
    private Map map;
    /**
     * Players participating the match
     */
    private List<Player> players;
    /**
     * Killshot track, made of 8 Kills
     */
    private Kill[] skullsBoard;

    /**
     * Creates a GameView instance
     * @param map Current gaming map
     * @param players List of players in game
     * @param skullsBoard Skulls board instance
     */
    public GameView(Map map, List<Player> players, Kill[] skullsBoard) {
        this.map = map;
        this.players = players;
        this.skullsBoard = skullsBoard;
    }

    /**
     * Returns the map instance
     * @return Map instance of the game
     */
    public Map getMap() {
        return map;
    }

    /**
     * Returns the list of players in the game
     * @return List of playing players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Returns the Skulls Board
     * @return Skulls board
     */
    public Kill[] getSkullsBoard() {
        return skullsBoard;
    }

    /**
     * Set the map instance of the GameView
     * @param map Current map
     */
    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * Set the list of players
     * @param players List of players in game
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Set the Skulls Board
     * @param skullsBoard Current skulls board
     */
    public void setSkullsBoard(Kill[] skullsBoard) {
        this.skullsBoard = skullsBoard;
    }
}
