package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ArrayDimensionException;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Represent a loot made of tree colors (or two plus a 'pick a power')
 */
public class Loot extends Card {
    private Color[] content;

    /**
     *
     * @param content is the array of loot colors inside the loot card (power means the "pick a power card" loot)
     * @throws ArrayDimensionException is thrown if you try to create the card with an array with a length!=3
     */
    public Loot(Color[] content) throws ArrayDimensionException
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

    public String getContentAsString(){
        return content[0].toString().substring(0,1)+content[1].toString().substring(0,1)+content[2].toString().substring(0,1);
    }
}
