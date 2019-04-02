package it.polimi.ingsw.model;

import java.util.List;

public abstract class Cell {
    private Side[] sides;
    private List<Player> pawns;
    private int roomNumber;

    Cell(Side[] sides, int roomNumber)
    {
        this.sides = sides;
        this.roomNumber = roomNumber;
    }

    public Side[] getSides() {
        return sides;
    }

    public List<Player> getPawns() {
        return pawns;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void addPawn(Player pl)
    {}

    public void removePawn(Player pl)
    {}
}
