package it.polimi.ingsw.model;

/**
 * Exception thrown when trying to draw from an empty deck
 */
public class EmptyDeckException extends Exception {
    public EmptyDeckException(){
        super("Can't draw a card from an empty deck.");
    }
}
