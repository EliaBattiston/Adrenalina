package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ServerNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.MatchView;
import it.polimi.ingsw.view.UserInterface;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class RMIClient extends UnicastRemoteObject implements Client, Serializable
{
    /**
     * Remote registry instance used to connect to the server
     */
    private Registry hostRegistry;

    /**
     * User gui/cli interface
     */
    private UserInterface user;

    /**
     * CLient RMI constructor, it gets the Server interface and binds its interface to the server registry
     * @param host IP address of the server
     * @param userint gui/cli user interface
     * @throws RemoteException in case of connection error
     */
    public RMIClient(String host, UserInterface userint) throws RemoteException, ServerNotFoundException
    {
        try {
            hostRegistry = LocateRegistry.getRegistry(host, 1099);
            RMIConnHandler RMIServer = (RMIConnHandler) hostRegistry.lookup("AM06");

            RMIServer.newConnection(this);
            user = userint;

            Thread t = new Thread(()-> {
                try {
                    while(RMIServer.ping()) {
                        sleep(2000);
                    }
                }
                catch (Exception e) {
                }
                user.generalMessage("Server disconnesso inaspettatamente, rilancia il client e riprova\n");
                System.exit(0);
            });
            t.start();

        }
        catch (AlreadyBoundException | NotBoundException ex) {
            Logger.getGlobal().log( Level.SEVERE, ex.toString(), ex );
            throw new ServerNotFoundException();
        }
    }

    /**
     * Receive the actual gameView to the client
     * @param matchView current game view
     */
    @Override
    public void updateGame(MatchView matchView)
    {
        user.updateGame(matchView);
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available, boolean mustChoose)
    {
        return user.chooseAction(available, mustChoose);
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @return Chosen weapon
     */
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose)
    {
        return user.chooseWeapon(available, mustChoose);
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose)
    {
        return user.grabWeapon(grabbable, mustChoose);
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @return Weapon to be reloaded
     */
    public Weapon reload(List<Weapon> reloadable, boolean mustChoose)
    {
        return user.reload(reloadable, mustChoose);
    }

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @return Point where the player will be when he's done moving
     */
    public Point movePlayer(List<Point> destinations, boolean mustChoose)
    {
        return user.movePlayer(destinations, mustChoose);
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets, boolean mustChoose)
    {
        return user.chooseTarget(targets, mustChoose);
    }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @return Point where the enemy will be after being moved
     */

    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose)
    {
        return user.moveEnemy(enemy, destinations, mustChoose);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) { return user.discardPower(powers, mustChoose); }

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen room
     */
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) { return user.chooseRoom(rooms, mustChoose); }

    /**
     * Asks the player to choose a direction
     * @param possible Directions you can choose
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     */
    public Direction chooseDirection(List<Direction> possible, boolean mustChoose) { return user.chooseDirection(possible, mustChoose); }

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen position
     */
    public Point choosePosition(List<Point> positions, boolean mustChoose) {return user.choosePosition(positions, mustChoose); }

    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose) { return user.discardWeapon(inHand, mustChoose); }


    /**
     * Asks the user for the nickname
     * @return user's nickname
     */
    public String getNickname() {

        return user.getNickname();
    }

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     */
    public String getPhrase() {
        return user.getPhrase();
    }

    /**
     * Asks the user fot the fighter
     * @param available List of available fighters
     * @return user's fighter
     */
    public Fighter getFighter(List<Fighter> available) {
        return user.getFighter(available);
    }

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     */
    public Integer getSkullNum() { return user.getSkullNum(); }

    /**
     * Asks the user to choose which map he wants to use
     * @return Number of the chosen map
     */
    public Integer chooseMap() {
        return user.chooseMap();
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     */
    public Boolean chooseFrenzy() {
        return user.chooseFrenzy();
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose) {
        return user.choosePower(inHand, mustChoose);
    }

    /**
     * Asks the user which ammo he wants to use
     * @param available List of powers on the player's board which can be used
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Color of the chosen ammo
     */
    public Color chooseAmmo(List<Color> available, boolean mustChoose) { return available.get(0); /*TODO make UI method for this*/ }

    /**
     * Sends to the client the list of players in winning order and notifies the end of the game
     * @param winnerList Ordered players' list
     */
    public void endGame(List<Player> winnerList) {
        user.endGame(winnerList);
    }

    /**
     * Sends a general message to the user to be displayed
     * @param payload Message payload
     */
    public void sendMessage(String payload) {
        user.generalMessage(payload);
    }

    /**
     * Returns true indifferently, needed from the server to ping the client
     * @return True
     */
    public Boolean clientPing() { return true; }
}

