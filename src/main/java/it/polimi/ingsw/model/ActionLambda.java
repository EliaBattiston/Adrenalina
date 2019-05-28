package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ClientDisconnectedException;

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
     * @throws ClientDisconnectedException If the client disconnects
     */
    void execute(Player pl, Map m, Object memory) throws ClientDisconnectedException;
}
