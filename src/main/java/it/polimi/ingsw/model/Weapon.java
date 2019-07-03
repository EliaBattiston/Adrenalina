package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.List;

/**
 * Class representing a weapon that give access to it's final attributes and let set the loaded property
 */
public class Weapon extends Card implements Serializable {
    /**
     * Name of the weapon
     */
    private final String name;
    /**
     * Info about the weapon
     */
    private final String notes;
    /**
     * if the weapon is loaded
     */
    private boolean loaded;
    /**
     * base action of the weapon
     */
    private final Action base;
    /**
     * additional actions of the weapon
     */
    private final List<Action> additional;
    /**
     * alternative actions of the weapon
     */
    private final Action alternative;
    /**
     * color of the weapon, not payed when you buy the card from a SpawnCell
     */
    private final Color color;

    /**
     * Create the Weapon Class
     * @param id unique id of the card
     * @param name name of the weapon
     * @param notes notes of the card
     * @param base base action of the card, it's always already fully payed if the card is loaded
     * @param additional additional action that can be added to the base one
     * @param alternative alternative action that can be done instead of the base one
     * @param color color of the weapon
     */
    public Weapon(int id, String name, String notes, Action base, List<Action> additional, Action alternative, Color color)
    {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.base = base;
        this.additional = additional;
        this.alternative = alternative;
        this.color = color;

        this.loaded = true;
    }

    /**
     * The name of the weapon
     * @return The name
     */
    public String getName() { return name; }

    /**
     *
     * @return the notes of the weapon
     */
    public String getNotes() { return notes; }

    /**
     *
     * @return if the weapon is loaded
     */
    public boolean isLoaded() { return loaded; }

    /**
     * Set the loaded attribute of the weapon
     * @param l the new loaded value
     */
    public void setLoaded(boolean l){ loaded = l; }

    /**
     *
     * @return the base actions of the weapon
     */
    public Action getBase() { return base; }

    /**
     *
     * @return the additional actions of the weapon
     */
    public List<Action> getAdditional() { return additional; }

    /**
     *
     * @return the alternative actions of the weapon
     */
    public Action getAlternative() { return alternative; }

    /**
     *
     * @return the color of the weapon
     */
    public Color getColor() { return color; }
}