package it.polimi.ingsw.model;

import java.util.List;

public interface ActionLambda {

    //TODO: add @FunctionalInterface
    public void execute(Player pl, Map m, List<Player> playersList);
}
