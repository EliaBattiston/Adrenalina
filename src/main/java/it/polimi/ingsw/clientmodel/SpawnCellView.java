package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Side;
import it.polimi.ingsw.model.Weapon;

import java.io.Serializable;
import java.util.List;

/**
 * View version of the SpawnCell Class
 */
public class SpawnCellView extends CellView implements Serializable
{
    private List<Weapon> weapons;

    private Color spawn;

    public SpawnCellView(List<PlayerView> pawns, int roomNumber, Side[] sides, List<Weapon> weapons, Color spawn)
    {
        super(pawns, roomNumber, sides);
        this.weapons = weapons;
        this.spawn = spawn;
    }

    public List<Weapon> getWeapons()
    {
        return weapons;
    }

    @Override
    public boolean hasSpawn(Color c)
    {
        return c == spawn;
    }
}
