package it.polimi.ingsw.model;

import java.util.List;

/**
 * this interface implements a lambda function representing an action a player can do
 */
@FunctionalInterface
public interface ActionLambda {
    /**
     * action to be executed by the player
     * @param pl player who makes the action
     * @param m map representing the actual game board
     * @param memory additional parameter used by some lambdas for additional actions
     */
    void execute(Player pl, Map m, Object memory);
}
