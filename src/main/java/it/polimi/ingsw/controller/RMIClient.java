package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.io.Serializable;
import java.net.NetworkInterface;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIClient extends UnicastRemoteObject implements Client, Serializable
{
    Registry registry;

    public RMIClient(String host) throws RemoteException
    {
        try {
            registry = LocateRegistry.getRegistry(host);
            RMIConnHandler RMIServer = (RMIConnHandler) registry.lookup("AM06");
            String bindName = "AM06-" + new Random().nextInt();
            registry.bind(bindName, this);
            RMIServer.newConnection(bindName);
        }
        catch(NotBoundException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
        }
        catch(AlreadyBoundException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
        }
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available, boolean mustChoose)
    {
        return available.get(0);
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @return Chosen weapon
     */
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose)
    {
        return available.get(0);
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose)
    {
        return grabbable.get(0);
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @return Weapons to be reloaded
     */
    public List<Weapon> reload(List<Weapon> reloadable, boolean mustChoose)
    {
        return reloadable;
    }

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @return Point where the player will be when he's done moving
     */
    public Point movePlayer(List<Point> destinations, boolean mustChoose)
    {
        return destinations.get(0);
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets, boolean mustChoose)
    {
        return targets.get(0);
    }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @return Point where the enemy will be after being moved
     */
    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose)
    {
        System.out.println(enemy.getNick() + " -> (" + destinations.get(0).getX() + ", " + destinations.get(0).getY() + ")");
        return destinations.get(0);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) { return powers.get(0); }

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen room
     */
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) { return rooms.get(0); }

    /**
     * Asks the player to choose a direction
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen direction
     */
    public Direction chooseDirection(boolean mustChoose) { return Direction.NORTH; }

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen position
     */
    public Point choosePosition(List<Point> positions, boolean mustChoose) { return positions.get(0); }

    /**
     * Asks the user for the nickname
     * @return user's nickname
     */
    public String getNickname() {
        Scanner stdin = new Scanner(System.in);

        System.out.print("Il tuo nickname: ");
        return stdin.nextLine();
    }

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     */
    public String getPhrase() {
        Scanner stdin = new Scanner(System.in);

        System.out.print("La tua esclamazione: ");
        return stdin.nextLine();
    }

    /**
     * Asks the user fot the fighter
     * @return user's fighter
     */
    public Fighter getFighter() {
        System.out.println("[1] Dstruttor3");
        System.out.println("[2] Banshee");
        System.out.println("[3] Dozer");
        System.out.println("[4] Violetta");
        System.out.println("[5] Sprog");

        Scanner stdin = new Scanner(System.in);
        int chosen = 0;
        while(chosen < 1 || chosen > 5)
        {
            System.out.println("Scegli il tuo personaggio (1-5): ");
            chosen = stdin.nextInt();
        }

        return Fighter.values()[chosen-1];
    }

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     */
    public Integer getSkullNum() {return 5; }

    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose)
    {
        return inHand.get(0);
    }

    /**
     * Asks the user to choose which map he wants to use
     * @return Number of the chosen map
     */
    public Integer chooseMap() {
        return 1;
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     */
    public Boolean chooseFrenzy() {
        return true;
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose) {
        return inHand.get(0);
    }
}

