package it.polimi.ingsw.model;

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
}
