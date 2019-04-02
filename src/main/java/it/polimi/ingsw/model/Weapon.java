package it.polimi.ingsw.model;

import java.util.List;

public class Weapon extends Card{
    private final int id;
    private final String name;
    private boolean loaded;
    private final Action base;
    private final List<Action> additional;
    private final List<Action> alternative;
    private final Color color;

    Weapon(int id, String name, Action base, List<Action> additional, List<Action> alternative, Color color)
    {
        this.id = id;
        this.name = name;
        this.base = base;
        this.additional = additional;
        this.alternative = alternative;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Action getBase() {
        return base;
    }

    public List<Action> getAdditional() {
        return additional;
    }

    public List<Action> getAlternative() {
        return alternative;
    }

    public Color getColor() {
        return color;
    }

    public void setLoaded(boolean l)
    {}
}