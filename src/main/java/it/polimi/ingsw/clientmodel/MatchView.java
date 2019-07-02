package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.controller.GamePhase;

import java.io.Serializable;

/**
 * Client's version of the model, which only gives information about what the receiving player knows
 */
public class MatchView implements Serializable {

    private MyPlayerView viewer;

    private GameView game;

    private PlayerView active;

    private GamePhase phase;

    private long playerTurnDuration;

    /**
     * Instances a MatchView object representing the current Match
     * @param viewer myPlayer
     * @param active the actual active player
     * @param gv the gameview
     * @param phase the current phase of the match
     * @param playerTurnDuration the maximum duration of a player turn
     */
    public MatchView(MyPlayerView viewer, PlayerView active, GameView gv, GamePhase phase, long playerTurnDuration) {
        this.viewer = viewer;
        this.active = active;
        this.game = gv;
        this.phase = phase;
        this.playerTurnDuration = playerTurnDuration;
    }

    public GameView getGame(){ return game; }

    public MyPlayerView getMyPlayer(){ return viewer; }

    public PlayerView getActive(){ return active; }

    public GamePhase getPhase() { return phase; }

    public long getTimeForAction() { return playerTurnDuration; }
}
