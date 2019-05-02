package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * @param refillWeapon Weapon to be placed in the cell
     */
    public void refillWeapon(Weapon refillWeapon)
    {
        for(int i = 0; i < weapons.length; i++) {
            if(weapons[i] == null) {
                weapons[i] = refillWeapon;
                return ;
            }
        }
    }

    /**
     * Returns the spawning color of the cell
     * @return spawn Color
     */
    public Color getSpawn() {
        return spawn;
    }

    /**
     * The function tells whether it is worth to move in this cell for picking up items
     * @return True if the cell has items, false otherwise
     */
    public boolean hasItems(){
        return true;
    }

    /**
     * Executes the acquisition of an item from the cell
     * @param pl Player who picks
     * @param lootDeck Loot cards' deck
     * @param powersDeck Power cards' deck
     */
    public void pickItem(Player pl, EndlessDeck<Loot> lootDeck, EndlessDeck<Power> powersDeck)
    {
        //TODO make the player pay the weapon's price
        Weapon picked = pl.getConn().chooseWeapon(this.getWeapons(), true);
        pickWeapon(picked);

        //If the player already has 3 weapons he has to discard one
        if(pl.getWeapons().size() >= 3)
        {
            Weapon discard = pl.getConn().discardWeapon(pl.getWeapons(), true);

            pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                int pos = Arrays.asList(weapons).indexOf(discard);
                if(pos>-1 && pos<=3)
                {
                    weapons[pos].setLoaded(false);
                    refillWeapon(weapons[pos]);
                    weapons[pos] = null;
                }
                else
                {
                    Logger.getGlobal().log(Level.SEVERE, "Weapon to be discarded is not in the player's hand", pl);
                }
            }));
        }

        //Give the new weapon to the player
        pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
            int pos;
            for(pos=0; pos<3 && weapons[pos] != null; pos++)
                ;
            if(pos<=3 && weapons[pos] == null)
            {
                weapons[pos] = picked;
            }
            else
            {
                Logger.getGlobal().log(Level.SEVERE, "No space for new weapon in player's hand", pl);
            }
        }));
    }

    /**
     * Refill the cell's items if needed
     * @param game Game which contains needed decks
     */
    public void refill(Game game){
        while (getWeapons().size() < 3)
            refillWeapon(game.getWeaponsDeck().draw());
    }

    /**
     * Tells if the cell has a spawn point of color c
     * @param c Desired spawn color
     * @return True if the cell has the desired spawn point
     */
    public boolean hasSpawn(Color c){
        return getSpawn() == c;
    }
}
