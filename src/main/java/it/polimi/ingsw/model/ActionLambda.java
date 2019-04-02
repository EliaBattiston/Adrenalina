package it.polimi.ingsw.model;

import java.util.List;

@FunctionalInterface
public interface ActionLambda {
    public void execute(Player pl, Map m, List<Player> playersList);
}
