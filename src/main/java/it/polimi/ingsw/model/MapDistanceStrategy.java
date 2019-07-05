package it.polimi.ingsw.model;

/**
 * Strategy pattern interface used for Map's methods
 */
public interface MapDistanceStrategy {
    boolean calculate(Player p1, Player p2);
}
