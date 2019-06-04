package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.clientmodel.MatchView;

import java.security.SecureRandom;
import java.util.List;

public class AIConnection implements Connection
{
    /**
     * Send the actual matchView to the client
     * @param matchView current match view
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public void updateGame(MatchView matchView) throws ClientDisconnectedException
    {
        ;
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen action
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Action chooseAction(List<Action> available, boolean mustChoose) throws ClientDisconnectedException{ return available.get( new SecureRandom().nextInt(available.size()) ); }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose) throws ClientDisconnectedException{ return available.get( new SecureRandom().nextInt(available.size()) ); }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose) throws ClientDisconnectedException{ return grabbable.get( new SecureRandom().nextInt(grabbable.size()) ); }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Weapon to be reloaded
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Weapon reload(List<Weapon> reloadable, boolean mustChoose) throws ClientDisconnectedException{ return reloadable.get( new SecureRandom().nextInt(reloadable.size()) ); }

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the player will be when he's done moving
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Point movePlayer(List<Point> destinations, boolean mustChoose) throws ClientDisconnectedException{ return destinations.get( new SecureRandom().nextInt(destinations.size()) ); }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen target
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Player chooseTarget(List<Player> targets, boolean mustChoose) throws ClientDisconnectedException{ return targets.get( new SecureRandom().nextInt(targets.size()) ); }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the enemy will be after being moved
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose) throws ClientDisconnectedException
    {
        return destinations.get(new SecureRandom().nextInt(destinations.size()));
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Card to be discarded
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) throws ClientDisconnectedException{ return powers.get( new SecureRandom().nextInt(powers.size()) ); }

    /**
     * Asks the user to choose a room
     * @param rooms List of possible rooms
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen room
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) throws ClientDisconnectedException{ return rooms.get( new SecureRandom().nextInt(rooms.size()) ); }

    /**
     * Asks the player to choose a direction
     * @param possible Directions you can choose
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Direction chooseDirection(List<Direction> possible, boolean mustChoose) throws ClientDisconnectedException{ return possible.get( new SecureRandom().nextInt(possible.size()) ); }

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen position
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Point choosePosition(List<Point> positions, boolean mustChoose) throws ClientDisconnectedException{ return positions.get( new SecureRandom().nextInt(positions.size()) ); }

    /**
     * Asks the user for the nickname
     * @return user's nickname
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public String getNickname() throws ClientDisconnectedException
    {
        String nick = Integer.toString( new SecureRandom().nextInt() );
        System.out.println("Giocatore " +  nick);
        return nick;
    }

    /**
     * Asks the user for the effect phrase
     * @return User's effect phrase
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public String getPhrase() throws ClientDisconnectedException
    {
        return "Testing is hard work, but someone has to do it!";
    }

    /**
     * Asks the user for the fighter
     * @param available List of available fighters
     * @return User's fighter
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Fighter getFighter(List<Fighter> available) throws ClientDisconnectedException{ return available.get( new SecureRandom().nextInt(available.size()) ); }

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Integer getSkullNum() throws ClientDisconnectedException
    {
        return 8;
    }

    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose) throws ClientDisconnectedException{ return inHand.get( new SecureRandom().nextInt(inHand.size()) ); }

    /**
     * Asks the user to choose which map he wants to use
     * @return Number of the chosen map
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Integer chooseMap() throws ClientDisconnectedException
    {
        return new SecureRandom().nextInt(3)+1;
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Boolean chooseFrenzy() throws ClientDisconnectedException
    {
        return true;
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose) throws ClientDisconnectedException{ return inHand.get( new SecureRandom().nextInt(inHand.size()) ); }

    /**
     * Asks the user which ammo he wants to use
     * @param available List of powers on the player's board which can be used
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Color of the chosen ammo
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Color chooseAmmo(List<Color> available, boolean mustChoose) throws ClientDisconnectedException{ return available.get( new SecureRandom().nextInt(available.size()) ); }

    /**
     * Sends to the client the list of players in winning order and notifies the end of the game
     * @param winnerList Ordered players' list
     * @throws ClientDisconnectedException In case of client unexpected disconnection
     */
    public void endGame(List<Player> winnerList) throws ClientDisconnectedException{ ; }

    /**
     * Sends a general message to the user to be displayed
     * @param payload Message payload
     * @throws ClientDisconnectedException If the client disconnects
     */
    public void sendMessage(String payload) throws ClientDisconnectedException
    {
        ;
    }

    /**
     * Returns true indifferently, needed from the server to ping the client
     * @throws ClientDisconnectedException If the client disconnects
     */
    public void clientPing() throws ClientDisconnectedException
    {
        ;
    }

    /**
     * Cancels current connection
     */
    public void cancelConnection()
    {
        ;
    }
}
