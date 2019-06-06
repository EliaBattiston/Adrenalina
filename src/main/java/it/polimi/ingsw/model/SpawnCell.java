package it.polimi.ingsw.model;

import it.polimi.ingsw.clientmodel.CellView;
import it.polimi.ingsw.clientmodel.PlayerView;
import it.polimi.ingsw.clientmodel.SpawnCellView;
import it.polimi.ingsw.controller.Match;
import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.exceptions.EmptyDeckException;

import java.util.ArrayList;
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
            if(weapons[i] != null && weapons[i].equals(chosen)) {
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
     * @param pl Player who would like to pick an item
     * @return True if the cell has items, false otherwise
     */
    public boolean hasItems(Player pl){
        List<Weapon> available = getWeapons();
        List<Weapon> purchasable = new ArrayList<>(available);
        List<Color> cost = new ArrayList<>();

        for (Weapon w : available)
        {
            cost.clear();
            if(w.getBase().getCost() != null)
                cost.addAll(w.getBase().getCost());

            if(pl.getAmmo(Color.RED) < cost.stream().filter(c -> c == Color.RED).count()
                || pl.getAmmo(Color.BLUE) < cost.stream().filter(c -> c == Color.BLUE).count()
                || pl.getAmmo(Color.YELLOW) < cost.stream().filter(c -> c == Color.YELLOW).count())
            {
                purchasable.remove(w);
            }
        }

        return !purchasable.isEmpty();
    }

    /**
     * Executes the acquisition of an item from the cell
     * @param pl Player who picks
     * @param lootDeck Loot cards' deck
     * @param powersDeck Power cards' deck
     */
    public void pickItem(Player pl, EndlessDeck<Loot> lootDeck, EndlessDeck<Power> powersDeck, List<Player> messageReceivers) throws ClientDisconnectedException
    {
        List<Weapon> available = getWeapons();
        List<Weapon> purchasable = new ArrayList<>(available);
        List<Color> cost = new ArrayList<>();

        for (Weapon w : available)
        {
            cost.clear();
            if(w.getBase().getCost() != null)
                cost.addAll(w.getBase().getCost());

            if(pl.getAmmo(Color.RED) < cost.stream().filter(c -> c == Color.RED).count()
                    || pl.getAmmo(Color.BLUE) < cost.stream().filter(c -> c == Color.BLUE).count()
                    || pl.getAmmo(Color.YELLOW) < cost.stream().filter(c -> c == Color.YELLOW).count())
            {
                purchasable.remove(w);
            }
        }

        Weapon picked = pl.getConn().grabWeapon(purchasable, true);
        pickWeapon(picked);

        //If the player already has 3 weapons he has to discard one
        if(pl.getWeapons().size() >= 3)
        {
            Weapon discard = pl.getConn().discardWeapon(pl.getWeapons(), true);

            pl.applyEffects(EffectsLambda.removeWeapon(discard, this));
        }

        //Pay for the weapon's price
        cost.clear();
        if(picked.getBase().getCost() != null)
            cost.addAll(picked.getBase().getCost());
        pl.applyEffects(EffectsLambda.payAmmo(cost));

        //Give the new weapon to the player
        pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
            int pos;
            for(pos=0; pos<3 && weapons[pos] != null; pos++)
                ;
            if(pos<=3 && weapons[pos] == null)
            {
                weapons[pos] = picked;
                picked.setLoaded(true);
            }
            else
            {
                Logger.getGlobal().log(Level.SEVERE, "No space for new weapon in players hand", pl);
            }
        }));

        Match.broadcastMessage(pl.getNick() + " ha comprato " + picked.getName(), messageReceivers);
    }

    /**
     * Refill the cell's items if needed
     * @param game Game which contains needed decks
     */
    public void refill(Game game){
        boolean empty = false;
        while (getWeapons().size() < 3 && !empty)
            try {
                refillWeapon(game.getWeaponsDeck().draw());
            }
            catch(EmptyDeckException ignore) {
                empty = true;
            }
    }

    /**
     * Tells if the cell has a spawn point of color c
     * @param c Desired spawn color
     * @return True if the cell has the desired spawn point
     */
    public boolean hasSpawn(Color c){
        return getSpawn() == c;
    }

    public CellView getView()
    {
        List<PlayerView> pawnsView = new ArrayList<>();
        for(Player p: getPawns())
        {
            pawnsView.add(p.getView());
        }

        return new SpawnCellView(pawnsView, getRoomNumber(), getSides(), getWeapons(), spawn);
    }
}
