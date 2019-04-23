package it.polimi.ingsw.model;

/**
 * Lambda used to determine if an action is feasible in the current context
 */
@FunctionalInterface
public interface FeasibleLambda
{
    /**
     * Runs the operations needed to determine if the action is currently feasible
     * @param pl Player who would execute the action
     * @param map Map of the game
     * @param memory additional parameter used by some lambdas for additional actions
     * @return True if the action is currently feasible, false otherwise
     */
    boolean execute(Player pl, Map map, Object memory);
}
