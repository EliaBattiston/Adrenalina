package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GamePhase;
import it.polimi.ingsw.model.Player;

import java.io.Serializable;

/**
 * From the controller.Match are missing: frenzyKills
 */
public class MatchView implements Serializable {
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

    /**
     * Instances a new MatchView object with current parameters
     * @param game Active game
     * @param active Active player
     * @param viewer Player who calls the view method
     * @param actionsNumber Number of remaining actions for the currently active player
     * @param phase Game phase
     * @param useFrenzy true in case of Frenzy turn, false otherwise
     * @param firstFrenzy First Frenzy player
     */
    public MatchView(GameView game, Player active, Player viewer, int actionsNumber, GamePhase phase, boolean useFrenzy, Player firstFrenzy) {
        this.game = game;
        this.active = active;
        this.myPlayer = viewer;
        this.actionsNumber = actionsNumber;
        this.phase = phase;
        this.useFrenzy = useFrenzy;
        this.firstFrenzy = firstFrenzy;
    }

    /**
     * Returns the GameView instance of the game
     * @return GameView instance referred to the match
     */
    public GameView getGame() {
        return game;
    }

    /**
     * Sets the GameView instance of the object
     * @param game GameView instance
     */
    public void setGame(GameView game) {
        this.game = game;
    }

    /**
     * Returns active player
     * @return Active player
     */
    public Player getActive() {
        return active;
    }

    /**
     * Set the active player in the view
     * @param active Active player to be set
     */
    public void setActive(Player active) {
        this.active = active;
    }

    /**
     * Returns the Player viewer instance
     * @return Player viewer instance
     */
    public Player getMyPlayer() {
        return myPlayer;
    }

    /**
     * Return the actual game phase
     * @return Actual game phase
     */
    public GamePhase getPhase() {
        return phase;
    }
}
