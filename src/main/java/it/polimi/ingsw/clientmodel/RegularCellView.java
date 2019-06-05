package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.Loot;
import it.polimi.ingsw.model.Side;

import java.io.Serializable;
import java.util.List;

public class RegularCellView extends CellView implements Serializable
{
    private Loot loot;

    public RegularCellView(List<PlayerView> pawns, int roomNumber, Side[] sides, Loot loot)
    {
        super(pawns, roomNumber, sides);
        this.loot = loot;
    }

    public Loot getLoot()
    {
        return loot;
    }
}
