package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;

import java.util.List;

public interface UserInterface
{
    /**
     * Update the actual gameView to the client
     * @param gameView current game view
     */
    public void updateGame(GameView gameView);

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available, boolean mustChoose);

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     *
     */
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose);

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose);

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Weapon to be reloaded
     */
    public Weapon reload(List<Weapon> reloadable, boolean mustChoose);

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the player will be when he's done moving
     */
    public Point movePlayer(List<Point> destinations, boolean mustChoose);

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets, boolean mustChoose);

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the enemy will be after being moved
     */
    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose);

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers, boolean mustChoose);

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen room
     */
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose);

    /**
     * Asks the player to choose a direction
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     */
    public Direction chooseDirection(boolean mustChoose);

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen position
     */
    public Point choosePosition(List<Point> positions, boolean mustChoose);

    /**
     * Asks the user for the nickname
     * @return user's nickname
     */
    public String getNickname();

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     */
    public String getPhrase();

    /**
     * Asks the user fot the fighter
     * @return user's fighter
     */
    public Fighter getFighter();

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     */
    public Integer getSkullNum();

    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose);

    /**
     * Asks the user to choose which map he wants to use
     * @return Number of the chosen map
     */
    public Integer chooseMap();

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     */
    public Boolean chooseFrenzy();

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose);
}