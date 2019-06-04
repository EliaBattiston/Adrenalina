package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.Loot;

import java.io.Serializable;
import java.util.List;

public class RegularCellView extends CellView implements Serializable
{
    private Loot loot;

    public RegularCellView(List<PlayerView> pawns, Loot loot)
    {
        super(pawns);
        this.loot = loot;
    }

    public Loot getLoot()
    {
        return loot;
    }
}
