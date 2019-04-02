package it.polimi.ingsw.model;

import java.util.Arrays;
import java.util.List;

public class SpawnCell extends Cell{
    private Color spawn;
    private Weapon[] weapons;

    SpawnCell(Side[] sides, int roomNumber, Color spawn)
    {
        super(sides, roomNumber);
        this.spawn = spawn;
    }

    public List<Weapon> getWeapons()
    {
        return Arrays.asList(weapons);
    }

    public Weapon pickWeapon(Weapon chosen)
    {
        return weapons[0];
    }

    public void refillWeapon(Weapon refillWeapon)
    {}
}
