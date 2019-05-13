package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GamePhase;
import it.polimi.ingsw.model.Player;

/**
 * From the controller.Match are missing: frenzyKills
 */
public class MatchView {
    /**
     * Model of the match's game, containing all relevant information
     */
    private GameView game;

    /**
     * Currently active player
     */
    private Player active;

    /**
     * The player who is running the client
     */
    private Player myPlayer;

    /**
     * Number of remaining actions for the currently active player
     */
    private int actionsNumber;

    /**
     * Phase of the game
     */
    private GamePhase phase;

    /**
     * True if there will be a frenzy turn at the end of the game
     */
    private boolean useFrenzy;

    /**
     * The first player to play his turn in frenzy mode
     */
    private Player firstFrenzy;

    public MatchView(GameView game, Player active, Player viewer, int actionsNumber, GamePhase phase, boolean useFrenzy, Player firstFrenzy) {
        this.game = game;
        this.active = active;
        this.myPlayer = viewer;
        this.actionsNumber = actionsNumber;
        this.phase = phase;
        this.useFrenzy = useFrenzy;
        this.firstFrenzy = firstFrenzy;
    }

    public GameView getGame() {
        return game;
    }

    public void setGame(GameView game) {
        this.game = game;
    }

    public Player getActive() {
        return active;
    }

    public void setActive(Player active) {
        this.active = active;
    }

    public Player getMyPlayer() {
        return myPlayer;
    }

    public GamePhase getPhase() {
        return phase;
    }
}
