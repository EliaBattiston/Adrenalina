package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.Weapon;

import java.io.Serializable;
import java.util.List;

public class SpawnCellView extends CellView implements Serializable
{
    List<Weapon> weapons;

    public SpawnCellView(List<PlayerView> pawns, List<Weapon> weapons)
    {
        super(pawns);
        this.weapons = weapons;
    }

    public List<Weapon> getWeapons()
    {
        return weapons;
    }
}
