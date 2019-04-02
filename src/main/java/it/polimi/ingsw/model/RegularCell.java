package it.polimi.ingsw.model;

public class RegularCell extends Cell {
    private Loot loot;

    RegularCell(Side[] sides, int roomNumber)
    {
        super(sides, roomNumber);
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
        return loot;
    }

    public void refillLoot(Loot refillLoot)
    {}
}
