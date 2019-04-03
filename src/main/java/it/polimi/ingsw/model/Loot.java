package it.polimi.ingsw.model;

/**
 * Represent the type of ammos inside a Loot Card
 */
public class Loot extends Card {
    private Color[] content;

    /**
     *
     * @param content is the array of loot colors inside the loot card (power means the "pick a power card" loot)
     * @throws ArrayDimensionException is thrown if you try to create the card with an array with a length!=3
     */
    Loot(Color[] content) throws ArrayDimensionException
    {
        if(content.length != 3)
            throw new ArrayDimensionException("Loot must have 3 colors.");
        this.content = content;
    }

    /**
     *
     * @return the array of colors that are inside the card
     */
    public Color[] getContent()
    {
        return content;
    }
}
