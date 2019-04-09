package it.polimi.ingsw.model;

import java.util.List;

/**
 * Class representing a weapon that give access to it's final attributes and let set the loaded property
 */
public class Weapon extends Card{
    private final int id;
    private final String name;
    private final String notes;
    private boolean loaded;
    private final Action base;
    private final List<Action> additional;
    private final List<Action> alternative;
    private final Color color;

    /**
     *
     * @param id unique id of the card
     * @param name name of the weapon
     * @param notes notes of the card
     * @param base base action of the card, it's always already fully payed if the card is loaded
     * @param additional additional action that can be added to the base one
     * @param alternative alternative action that can be done instead of the base one
     * @param color color of the weapon
     */
    public Weapon(int id, String name, String notes, Action base, List<Action> additional, List<Action> alternative, Color color)
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

    public int getId() { return id; }

    public String getName() { return name; }

    public String getNotes() { return notes; }

    public boolean isLoaded() { return loaded; }

    public void setLoaded(boolean l){ loaded = l; }

    public Action getBase() { return base; }

    public List<Action> getAdditional() { return additional; }

    public List<Action> getAlternative() { return alternative; }

    public Color getColor() { return color; }
}