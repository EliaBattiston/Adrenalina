package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * Card is an abstract class that collect under a single 'family' of Classes the following ones: POWER, Loot and Weapon
 * It's useful for the description of the Decks that can only be made of Cards
 */
public abstract class Card implements Serializable {
    /**
     * Id of the card
     */
    protected int id;

    /**
     * Returns the id of the card
     * @return The id
     */
    public int getId() { return id; }
}
