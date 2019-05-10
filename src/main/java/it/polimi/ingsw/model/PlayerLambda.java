package it.polimi.ingsw.model;

import java.util.List;

/**
 * The PlayerLambda interface is used to provide the class with a lambda function that instructs it how to apply card's effects to itself
 */
@FunctionalInterface
public interface PlayerLambda {
    public void execute(String[] damage, List<String> marks, Point position, Weapon[] weapons, Power[] powers, Ammunitions ammo);
}
