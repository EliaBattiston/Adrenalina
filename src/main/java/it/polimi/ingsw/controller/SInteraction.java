package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.WrongPointException;
import it.polimi.ingsw.model.*;

import java.util.List;

/**
 * Methodes that can be used to talk with a client instance, by asking how to complete different actions. The corresponding class on the client is CInteraction
 */
public class SInteraction
{
    private SInteraction()
    {}

    /**
     * Asks the user to choose between a set of actions he can use
     * @param conn Connection of the current user
     * @param available List of available actions
     * @return Chosen action
     */
    public static Action chooseAction(Connection conn, List<Action> available)
    {
        Action tempAction = new Action("name", "description", null, null);
        return tempAction;
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param conn Connection of the current user
     * @param available List of available weapons
     * @return Chosen weapon
     */
    public static Weapon chooseWeapon(Connection conn, List<Weapon> available)
    {
        Weapon tempWeapon = new Weapon(0, "name", "notes", null, null, null, null);
        return tempWeapon;
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param conn Connection of the current user
     * @param cell SpawnCell from which the user can pick up the weapon
     * @return Chosen weapon
     */
    public static Weapon grabWeapn(Connection conn, SpawnCell cell)
    {
        Weapon tempWeapon = new Weapon(0, "name", "notes", null, null, null, null);
        return tempWeapon;
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param conn Connection of the current user
     * @return Weapons to be reloaded
     */
    public static List<Weapon> reload(Connection conn)
    {
        return null;
    }

    /**
     * Asks the user where he wants to move
     * @param conn Connection of the current user
     * @param maxDistance Maximum number of steps the player is allowed to move
     * @return Point where the player will be when he's done moving
     */
    public static Point move(Connection conn, int maxDistance)
    {
        try
        {
            return new Point(0, 0);
        }
        catch(WrongPointException e)
        {
            return null;
        }
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param conn Connection of the current user
     * @param targets List of player that can be targeted
     * @return Chosen target
     */
    public static Player chooseTarget(Connection conn, List<Player> targets)
    {
        return null;
    }

    /**
     * Asks the user where to move an enemy
     * @param conn Connection of the current user
     * @param enemy Enemy to be moved by the player
     * @param maxDistance Maximum number of steps the enemy can by moved by the current effect
     * @return Point where the enemy will be after being moved
     */
    public static Point displace(Connection conn, Player enemy, int maxDistance)
    {
        try
        {
            return new Point(0, 0);
        }
        catch(WrongPointException e)
        {
            return null;
        }
    }
}
