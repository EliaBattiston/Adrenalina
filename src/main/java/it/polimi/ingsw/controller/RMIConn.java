package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.WrongPointException;
import it.polimi.ingsw.model.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RMIConn implements Connection
{
    Client client;
    String registryID;

    RMIConn(Client client, String ID) {
        this.client = client;
        registryID = ID;
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available)
    {
        try {
            return client.chooseAction(available);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @return Chosen weapon
     */
    public Weapon chooseWeapon(List<Weapon> available)
    {
        try {
            return client.chooseWeapon(available);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable)
    {
        try {
            return client.grabWeapon(grabbable);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @return Weapons to be reloaded
     */
    public List<Weapon> reload(List<Weapon> reloadable)
    {
        try {
            return client.reload(reloadable);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user where he wants to move
     * @param destinations Possible destinations for the user
     * @return Point where the player will be when he's done moving
     */
    public Point move(List<Point> destinations)
    {
        try {
            return client.move(destinations);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets)
    {
        try {
            return client.chooseTarget(targets);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user where to move an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @return Point where the enemy will be after being moved
     */

    public Point displace(Player enemy, List<Point> destinations)
    {
        try {
            return client.displace(enemy, destinations);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers) {
        try {
            return client.discardPower(powers);
        }
        catch (RemoteException e) {
            return null;
        }
    }
}
