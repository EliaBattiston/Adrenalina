package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class represents a non-spawning (regular) cell
 */
public class RegularCell extends Cell {
    /**
     * Represents the pickable loot inside the cell (3x ammo or 2x ammo plus power card)
     */
    private Loot loot;

    /**
     * Applies the superclass constructor
     * @param sides sides of the cell (wall, door, nothing)
     * @param roomNumber room identification number
     */
    RegularCell(Side[] sides, int roomNumber)
    {
        super(sides, roomNumber);
        loot = null;
    }

    /**
     * Returns the loot in the cell without removing it
     * @return Cell's loot
     */
    public Loot getLoot()
    {
        return loot;
    }

    /**
     * Returns the loot and removes it from the cell
     * @return Cell's loot
     */
    public Loot pickLoot()
    {
        Loot pickedLoot = loot;
        loot = null;
        return pickedLoot;
    }

    /**
     * Refill the loot of the cell (if empty)
     * @param refillLoot loot for refilling
     */
    public void refillLoot(Loot refillLoot)
    {
        if(loot == null) {
            loot = refillLoot;
        }
    }

    /**
     * The function tells whether it is worth to move in this cell for picking up items
     * @param pl Player who would like to pick an item
     * @return True if the cell has items, false otherwise
     */
    public boolean hasItems(Player pl){
        return this.loot != null;
    }

    /**
     * Executes the acquisition of an item from the cell
     * @param pl Player who picks
     * @param lootDeck Loot cards' deck
     * @param powersDeck Power cards' deck
     */
    public void pickItem(Player pl, EndlessDeck<Loot> lootDeck, EndlessDeck<Power> powersDeck)
    {
        Loot picked = pickLoot();

        pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
            for(Color c : picked.getContent())
            {
                if(c == Color.POWER)
                {
                    Power newPower = powersDeck.draw();
                    Power discarded = null;

                    if(Arrays.stream(powers).noneMatch(Objects::isNull))
                    {
                        System.out.println(pl.getNick() + " deve scartare un potenziamento");
                        List<Power> inHand = new ArrayList<>(Arrays.asList(powers));
                        inHand.add(newPower);
                        discarded = pl.getConn().discardPower(inHand, true);

                        if(discarded == newPower)
                            discarded = null;
                        else
                            powersDeck.scrapCard(discarded);
                    }

                    int empty = Arrays.asList(powers).indexOf(discarded);

                    if(empty != -1)
                        powers[empty] = newPower;
                    else{
                        Logger.getGlobal().log(Level.WARNING, "Scrapped new card"); //TODO do we need the logger?
                        powersDeck.scrapCard(newPower);
                    }
                }
                else
                    ammo.add(c, 1);
            }
        }));
        lootDeck.scrapCard(picked);

        System.out.println(pl.getNick() + " ha raccolto un loot");
    }

    /**
     * Refill the cell's items if needed
     * @param game Game which contains needed decks
     */
    public void refill(Game game){
        if(loot == null)
            refillLoot(game.getAmmoDeck().draw());
    }

    /**
     * Tells if the cell has a spawn point of color c
     * @param c Desired spawn color
     * @return True if the cell has the desired spawn point
     */
    public boolean hasSpawn(Color c){
        return false;
    }
}
