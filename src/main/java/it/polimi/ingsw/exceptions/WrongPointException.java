package it.polimi.ingsw.exceptions;

/**
 * This exception is thrown when trying to set a point outside the map
 */
public class WrongPointException extends Exception
{
    public WrongPointException()
    {
        super("The point has a value outside the map");
    }
}
