package it.polimi.ingsw.exceptions;

//TODO add to UML
public class UsedNameException extends Exception
{
    public UsedNameException()
    {
        super("Another player with the same name is already in this match");
    }
}
