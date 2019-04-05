package it.polimi.ingsw.model;

/**
 * Exception thrown when trying to use an array with the wrong dimension
 */
public class ArrayDimensionException extends Exception {
    public ArrayDimensionException(String msg) {
        super(msg);
    }
}
