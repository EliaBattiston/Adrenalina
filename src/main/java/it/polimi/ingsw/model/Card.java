package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * Card is an abstract class that collect under a single 'family' of Classes the following ones: POWER, Loot and Weapon
 * It's useful for the description of the Decks that can only be made of Cards
 */
public abstract class Card implements Serializable{
    protected int id;

    public int getId() { return id; }
}
