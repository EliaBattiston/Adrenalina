package it.polimi.ingsw.model;

public class Power extends Card{
    public final int id;
    public final String name;
    public final Action base;
    public final Color color;

    public Power(int id, String name, Action base, Color color) {
        this.id = id;
        this.name = name;
        this.base = base;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Action getBase() {
        return base;
    }

    public Color getColor() {
        return color;
    }
}
