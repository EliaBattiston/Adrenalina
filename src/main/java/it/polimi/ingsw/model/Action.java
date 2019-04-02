package it.polimi.ingsw.model;

import java.util.List;

public class Action {
    private String description;
    private String name;
    private List<Color> cost;
    private ActionLambda lambda;

    Action(String name, String description, List<Color> cost, ActionLambda lambda)
    {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.lambda = lambda;
    }

    public void execute(Player pl, Map m, List<Player> playersList)
    {
        lambda.execute(pl, m, playersList);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public List<Color> getCost() {
        return cost;
    }
}
