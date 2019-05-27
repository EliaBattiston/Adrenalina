package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Match;
import it.polimi.ingsw.exceptions.ClientDisconnectedException;

import java.util.*;
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
    public void pickItem(Player pl, EndlessDeck<Loot> lootDeck, EndlessDeck<Power> powersDeck, List<Player> messageReceivers)
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
                        List<Power> inHand = new ArrayList<>(Arrays.asList(powers));
                        inHand.add(newPower);
                        try {
                            discarded = pl.getConn().discardPower(inHand, true);
                        }
                        catch(ClientDisconnectedException e) {
                            Match.disconnectPlayer(pl, messageReceivers);
                            discarded = inHand.get(new Random().nextInt(inHand.size()));
                        }

                        powersDeck.scrapCard(discarded);

                        Match.broadcastMessage(pl.getNick() + " scarta " + discarded.getName() + " per pescare un nuovo potenziamento", messageReceivers);
                    }

                    int empty = Arrays.asList(powers).indexOf(discarded);

                    if(empty != -1) //if it was null put in the first null, if it was a specific card discarded, put where the card was
                        powers[empty] = newPower;
                }
                else
                    ammo.add(c, 1);
            }
        }));
        lootDeck.scrapCard(picked);

        Match.broadcastMessage(pl.getNick() + " raccoglie delle munizioni", messageReceivers);
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
