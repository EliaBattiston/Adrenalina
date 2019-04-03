package it.polimi.ingsw.model;

@FunctionalInterface
public interface PlayerLambda {
    public void execute(Player[] damage, Player[] marks, Point position, Weapon[] weapons, Power[] powers, Ammunitions ammo);
}
