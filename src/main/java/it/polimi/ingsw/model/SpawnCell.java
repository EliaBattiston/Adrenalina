package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The class represents a spawning cell
 */
public class SpawnCell extends Cell{
    /**
     * spawn represents the color of the cell (needed in spawning phase to identify the correct spawning point)
     */
    private Color spawn;
    /**
     * Weapons in cell that can be collected from the players
     */
    private Weapon[] weapons;

    /**
     * instantiates the spawning cell with the superclass constructor, adding the cell color
     * @param sides sides of the cell
     * @param roomNumber room identification number
     * @param spawn cell color
     */
    SpawnCell(Side[] sides, int roomNumber, Color spawn)
    {
        super(sides, roomNumber);
        this.spawn = spawn;
        weapons = new Weapon[3];
        /*for (int i = 0; i < weapons.length; i++) {
            weapons[i] = null;
        }*/
    }
    /**
     * returns a list with the weapons inside the cell, without modifying the list itself
     * @return list of weapons inside the cell
     */
    public List<Weapon> getWeapons()
    {
        ArrayList<Weapon> dispweapons = new ArrayList<>();
        for (int i = 0; i < weapons.length; i++) {
            if(weapons[i] != null) {
                dispweapons.add(weapons[i]);
            }
        }
        return dispweapons;
    }

    /**
     * Removes the given weapon from the array and returns it (if exists)
     * @param chosen chosen weapon to pick
     * @return picked weapon (null in case of insuccess)
     */
    public Weapon pickWeapon(Weapon chosen)
    {
        for(int i = 0; i < weapons.length; i++) {
            if(weapons[i].equals(chosen)) {
                Weapon returnWeapon = weapons[i];
                weapons[i] = null;
                return returnWeapon;
            }
        }
        return null;
    }

    /**
     * add the given weapon to the cell weapon array (if the array length is lower than 3)
     * @param refillWeapon
     * @return true in case of success, false otherwise
     */
    public void refillWeapon(Weapon refillWeapon)
    {
        for(int i = 0; i < weapons.length; i++) {
            if(weapons[i] == null) {
                weapons[i] = refillWeapon;
                //return true;
                return;
            }
        }
        //return false;
    }

    /**
     * Returns the spawning color of the cell
     * @return spawn Color
     */
    public Color getSpawn() {
        return spawn;
    }
}
