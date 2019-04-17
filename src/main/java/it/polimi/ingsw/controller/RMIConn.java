package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.rmi.RemoteException;
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
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available, boolean mustChoose)
    {
        try {
            return client.chooseAction(available, mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen weapon
     */
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose)
    {
        try {
            return client.chooseWeapon(available, mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose)
    {
        try {
            return client.grabWeapon(grabbable, mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Weapons to be reloaded
     */
    public List<Weapon> reload(List<Weapon> reloadable, boolean mustChoose)
    {
        try {
            return client.reload(reloadable, mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Point where the player will be when he's done moving
     */
    public Point movePlayer(List<Point> destinations, boolean mustChoose)
    {
        try {
            return client.movePlayer(destinations, mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets, boolean mustChoose)
    {
        try {
            return client.chooseTarget(targets, mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Point where the enemy will be after being moved
     */

    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose)
    {
        try {
            return client.moveEnemy(enemy, destinations, mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) {
        try {
            return client.discardPower(powers, mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen room
     */
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) {
        try {
            return client.chooseRoom(rooms, mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the player to choose a direction
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen direction
     */
    public Direction chooseDirection(boolean mustChoose) {
        try {
            return client.chooseDirection(mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen position
     */
    public Point choosePosition(List<Point> positions, boolean mustChoose) {
        try {
            return client.choosePosition(positions, mustChoose);
        }
        catch (RemoteException e) {
            return null;
        }
    }
}
