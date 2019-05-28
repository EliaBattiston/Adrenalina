package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ClientDisconnectedException;

import java.io.Serializable;
import java.util.List;

/**
 * The class describes a single action a weapon or a power can make
 */
public class Action implements Serializable {
    /**
     * description of the action and its effects
     */
    private String description;
    /**
     * game's action name
     */
    private String name;
    /**
     * activation cost of the action (it can be an empty list for cost zero actions)
     */
    private List<Color> cost;
    /**
     * Lambda function describing the action's effects
     */
    private String lambdaIdentifier;

    /**
     * Instantiates an Action object, setting up all the parameters
     * @param name name of the action
     * @param description of the action
     * @param cost of the action (it can also be an empty list)
     * @param lambdaIdentifier id of the lambda function describing the action's effects
     */
    public Action(String name, String description, List<Color> cost, String lambdaIdentifier)
    {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.lambdaIdentifier = lambdaIdentifier;
    }

    /**
     * Executes the action's lambda function
     * @param pl Player who executes the action
     * @param map Map of the active board
     * @param memory Used by some actions with side effects
     * @throws ClientDisconnectedException If the client disconnects
     */
    public void execute(Player pl, Map map, Object memory) throws ClientDisconnectedException
    {
        ActionLambda lambda = ActionLambdaMap.getLambda(lambdaIdentifier);
        lambda.execute(pl, map, memory);
    }

    /**
     * Determines whether the lambda is currently feasible
     * @param pl Lambda's player
     * @param map Lambda's map
     * @param memory Lambda's memory
     * @return True if feasible, false otherwise
     */
    public boolean isFeasible(Player pl, Map map, Object memory){
        return FeasibleLambdaMap.isFeasible(lambdaIdentifier, pl, map, memory);
    }

    /**
     * returns the description of the action
     * @return action description
     */
    public String getDescription() {
        return description;
    }

    /**
     * returns the name of the action
     * @return action name
     */
    public String getName() {
        return name;
    }

    /**
     * returns the cost of the action in form of a list of Color
     * @return action cost
     */
    public List<Color> getCost() {
        return cost;
    }

    /**
     * Returns the identifier of the lambda, useful for type identification
     * @return Lambda identifier
     */
    public String getLambdaID() { return lambdaIdentifier; }
}
