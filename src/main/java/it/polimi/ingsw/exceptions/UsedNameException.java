package it.polimi.ingsw.exceptions;

/**
 * Exception thrown when there's another player already in the game queue with the same Nick
 */
public class UsedNameException extends RuntimeException
{
    public UsedNameException()
    {
        super("Another player with the same name is already in this match");
    }
}
