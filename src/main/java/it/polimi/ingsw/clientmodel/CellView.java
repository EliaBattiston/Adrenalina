package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Direction;
import it.polimi.ingsw.model.Side;

import java.io.Serializable;
import java.util.List;

public class CellView implements Serializable
{
    private List<PlayerView> pawns;

    private int roomNumber;

    private Side[] sides;

    public CellView(List<PlayerView> pawns, int roomNumber, Side[] sides)
    {
        this.pawns = pawns;
        this.roomNumber = roomNumber;
        this.sides = sides;
    }

    public List<PlayerView> getPawns()
    {
        return pawns;
    }

    public int getRoomNumber()
    {
        return roomNumber;
    }

    public boolean hasSpawn(Color c)
    {
        return false;
    }

    public Side getSide(Direction dir)
    {
        return sides[dir.ordinal()];
    }
}
