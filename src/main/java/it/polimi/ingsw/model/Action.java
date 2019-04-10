package it.polimi.ingsw.model;

import java.util.List;

/**
 * The class describes a single action a weapon or a power can make
 */
public class Action {
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
    Action(String name, String description, List<Color> cost, String lambdaIdentifier)
    {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.lambdaIdentifier = lambdaIdentifier;
    }

    /**
     * executes the action's lambda function
     * @param pl player who executes the action
     * @param m map of the active board
     * @param playersList list of targeted player(s) (if present)
     */
    public void execute(Player pl, Map m, List<Player> playersList)
    {
        ActionLambda lambda = ActionLambdaMap.getActionsLambda().get(lambdaIdentifier);
        lambda.execute(pl, m, playersList);
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
}
