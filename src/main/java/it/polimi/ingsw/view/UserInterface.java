package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;

import java.util.List;

public interface UserInterface
{
    /**
     * Update the actual gameView to the client
     * @param matchView current game view
     */
    void updateGame(MatchView matchView);

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen action
     */
    Action chooseAction(List<Action> available, boolean mustChoose);

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     *
     */
    Weapon chooseWeapon(List<Weapon> available, boolean mustChoose);

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose);

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Weapon to be reloaded
     */
    Weapon reload(List<Weapon> reloadable, boolean mustChoose);

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the player will be when he's done moving
     */
    Point movePlayer(List<Point> destinations, boolean mustChoose);

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen target
     */
    Player chooseTarget(List<Player> targets, boolean mustChoose);

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the enemy will be after being moved
     */
    Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose);

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Card to be discarded
     */
    Power discardPower(List<Power> powers, boolean mustChoose);

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen room
     */
    Integer chooseRoom(List<Integer> rooms, boolean mustChoose);

    /**
     * Asks the player to choose a direction
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     */
    Direction chooseDirection(List<Direction> possible, boolean mustChoose);

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen position
     */
    Point choosePosition(List<Point> positions, boolean mustChoose);

    /**
     * Asks the user for the nickname
     * @return user's nickname
     */
    String getNickname();

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     */
    String getPhrase();

    /**
     * Asks the user fot the fighter
     * @param available List of available fighters
     * @return user's fighter
     */
    Fighter getFighter(List<Fighter> available);

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     */
    Integer getSkullNum();

    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose);

    /**
     * Asks the user to choose which map he wants to use
     * @return Number of the chosen map
     */
    Integer chooseMap();

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     */
    Boolean chooseFrenzy();

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    Power choosePower(List<Power> inHand, boolean mustChoose);

    /**
     * Prints out a general message to the client interface
     * @param message Message to be printed
     */
    void generalMessage(String message);

    /**
     * Asks the user to choose between TCP and RMI connection
     * @return true in case of RMI connection, false elsewhere
     */
    boolean useRMI();

    /**
     * Asks the user for the IP address of the server
     * @return Server's IP address
     */
    String getIPAddress();

    /**
     * Asks the user for the IP address of the local machine
     * @return Server's IP address
     */
    String getLocalAddress(List<String> possibleIP);
}