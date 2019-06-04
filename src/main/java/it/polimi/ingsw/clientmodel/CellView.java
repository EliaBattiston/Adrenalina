package it.polimi.ingsw.clientmodel;

import java.io.Serializable;
import java.util.List;

public class CellView implements Serializable
{
    List<PlayerView> pawns;

    public CellView(List<PlayerView> pawns)
    {
        this.pawns = pawns;
    }

    public List<PlayerView> getPawns()
    {
        return pawns;
    }
}
