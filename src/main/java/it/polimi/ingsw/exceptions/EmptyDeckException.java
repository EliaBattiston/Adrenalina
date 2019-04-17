package it.polimi.ingsw.exceptions;

/**
 * Exception thrown when trying to draw from an empty deck
 */
public class EmptyDeckException extends RuntimeException {
    public EmptyDeckException(){
        super("Can't draw a card from an empty deck.");
    }
}
