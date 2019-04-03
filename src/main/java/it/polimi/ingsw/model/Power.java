package it.polimi.ingsw.model;

/**
 * Represent one of the four kind of powers inside the game and show it's values
 */
class Power extends Card{
    private final int id;
    private final String name;
    private final Action base;
    private final Color color;

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
