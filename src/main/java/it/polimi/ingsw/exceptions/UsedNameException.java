package it.polimi.ingsw.exceptions;

public class UsedNameException extends RuntimeException
{
    public UsedNameException()
    {
        super("Another player with the same name is already in this match");
    }
}
