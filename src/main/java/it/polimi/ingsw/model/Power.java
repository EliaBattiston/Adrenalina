package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * Represent one of the four kind of powers inside the game and show it's values
 */
class Power extends Card{
    private final int id;
    private final String name;
    private final Action base;
    private final Color color;

    /**
     *
     * @param id unique identifier of the card
     * @param name name of the card
     * @param base action that handle the effect of the card
     * @param color color of the card
     */
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
