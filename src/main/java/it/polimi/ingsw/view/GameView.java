package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;

import java.util.List;

/**
 * View of the game for the current player.
 * From the model.Game class the missing parts are: the decks, the other players data
 * From the controller.Match class it gets:
 */
public class GameView {
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

    public GameView(Map map, List<Player> players, Kill[] skullsBoard) {
        this.map = map;
        this.players = players;
        this.skullsBoard = skullsBoard;
    }
}
