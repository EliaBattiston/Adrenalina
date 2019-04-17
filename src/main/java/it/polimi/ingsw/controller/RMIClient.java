package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.io.Serializable;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RMIClient extends UnicastRemoteObject implements Client, Serializable
{
    Registry registry;

    public RMIClient(String host, String username) throws RemoteException
    {
        try {
            registry = LocateRegistry.getRegistry(host);
            RMIConnHandler RMIServer = (RMIConnHandler) registry.lookup("AM06");
            String bindName = "AM06-" + username;
            registry.bind(bindName, this);
            RMIServer.newConnection(bindName);
        }
        catch(NotBoundException e) { }
        catch(AlreadyBoundException e) { }
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available)
    {
        return available.get(0);
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @return Chosen weapon
     */
    public Weapon chooseWeapon(List<Weapon> available)
    {
        return available.get(0);
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable)
    {
        return grabbable.get(0);
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @return Weapons to be reloaded
     */
    public List<Weapon> reload(List<Weapon> reloadable)
    {
        return reloadable;
    }

    /**
     * Asks the user where he wants to move
     * @param destinations Possible destinations for the user
     * @return Point where the player will be when he's done moving
     */
    public Point move(List<Point> destinations)
    {
        return destinations.get(0);
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets)
    {
        return targets.get(0);
    }

    /**
     * Asks the user where to move an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @return Point where the enemy will be after being moved
     */

    public Point displace(Player enemy, List<Point> destinations)
    {
        System.out.println(enemy.getNick() + " -> (" + destinations.get(0).getX() + ", " + destinations.get(0).getY() + ")");
        return destinations.get(0);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers) { return powers.get(0); }
}

