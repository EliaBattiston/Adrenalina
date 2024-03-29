package it.polimi.ingsw.controller;

import it.polimi.ingsw.clientmodel.MatchView;
import it.polimi.ingsw.clientmodel.PlayerView;
import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.model.*;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * The RMI implementation of a Connection
 */
public class RMIConn implements Connection, Serializable
{
    /**
     * Client instance
     */
    Client client;

    RMIConn(Client client) {
        this.client = client;
    }

    /**
     * Send the actual matchView to the client
     * @param matchView current match view
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    @Override
    synchronized public void updateGame(MatchView matchView) throws ClientDisconnectedException {
        try {
            client.updateGame(matchView);
        }
        catch (RemoteException e) {

            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen action
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Action chooseAction(List<Action> available, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            Action lambda = client.chooseAction(available, mustChoose);
            if(lambda != null) {
                String lambdaID = lambda.getLambdaID();
                for (Action a : available)
                    if (a.getLambdaID().equals(lambdaID))
                        return a;
            }
            return null;
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen weapon
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            Weapon chosen = client.chooseWeapon(available, mustChoose);
            if(chosen != null) {
                int id = chosen.getId();
                for (Weapon w : available)
                    if (w.getId() == id)
                        return w;
            }
            return null;
        }
        catch (Exception e) {

            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen weapon
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            Weapon chosen = client.grabWeapon(grabbable, mustChoose);
            if(chosen != null) {
                int id = chosen.getId();
                for (Weapon w : grabbable)
                    if (w.getId() == id)
                        return w;
            }
            return null;
        }
        catch (Exception e) {

            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Weapon to be reloaded
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Weapon reload(List<Weapon> reloadable, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            Weapon chosen = client.reload(reloadable, mustChoose);
            if(chosen != null) {
                int id = chosen.getId();
                for (Weapon w : reloadable)
                    if (w.getId() == id)
                        return w;
            }
            return null;
        }
        catch (Exception e) {

            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Point where the player will be when he's done moving
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Point movePlayer(List<Point> destinations, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            return client.movePlayer(destinations, mustChoose);
        }
        catch (Exception e) {

            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen target
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Player chooseTarget(List<Player> targets, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            List<PlayerView> targetsViews = new ArrayList<>();
            for(Player t: targets)
            {
                targetsViews.add(t.getView());
            }

            PlayerView chosen = client.chooseTarget(targetsViews, mustChoose);
            if(chosen != null) {
                String nickChosen = chosen.getNick();
                for (Player p : targets)
                    if (p.getNick().equals(nickChosen))
                        return p;
            }
            return null;
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Point where the enemy will be after being moved
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */

    synchronized public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            return client.moveEnemy(enemy.getView(), destinations, mustChoose);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Card to be discarded
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) throws ClientDisconnectedException {
        try {
            Power chosen = client.discardPower(powers, mustChoose);
            if(chosen != null) {
                int id = chosen.getId();
                return powers.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
            }
            return null;
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen room
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) throws ClientDisconnectedException {
        try {
            Integer room = client.chooseRoom(rooms, mustChoose);
            if(room == null)
                return null;
            return rooms.stream().filter( r -> r.equals(room)).findFirst().orElse(null);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the player to choose a direction
     * @param possible Directions you can choose
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Direction chooseDirection(List<Direction> possible, boolean mustChoose) throws ClientDisconnectedException {
        try {
            return client.chooseDirection(possible, mustChoose);
        }
        catch (Exception e) {

            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen position
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Point choosePosition(List<Point> positions, boolean mustChoose) throws ClientDisconnectedException {
        try {
            return client.choosePosition(positions, mustChoose);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user for the nickname
     * @return user's nickname
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public String getNickname() throws ClientDisconnectedException {
        try {
            return client.getNickname();
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public String getPhrase() throws ClientDisconnectedException {
        try {
            return client.getPhrase();
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user fot the fighter
     * @param available List of available fighters
     * @return user's fighter
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Fighter getFighter(List<Fighter> available) throws ClientDisconnectedException {
        try {
            return client.getFighter(available);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Integer getSkullNum() throws ClientDisconnectedException {
        try {
            return client.getSkullNum();
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }
    
    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            return client.discardWeapon(inHand, mustChoose);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user to choose which map he wants to use
     * @return Number of the chosen map
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Integer chooseMap() throws ClientDisconnectedException {
        try {
            return client.chooseMap();
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Boolean chooseFrenzy() throws ClientDisconnectedException {
        try {
            return client.chooseFrenzy();
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Power choosePower(List<Power> inHand, boolean mustChoose) throws ClientDisconnectedException {
        try {
            Power chosen = client.choosePower(inHand, mustChoose);
            if(chosen == null)
                return null;
            return inHand.stream().filter(p -> p.getId() == chosen.getId()).findFirst().orElse(null);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Asks the user which ammo he wants to use
     * @param available List of powers on the player's board which can be used
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Color of the chosen ammo
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public Color chooseAmmo(List<Color> available, boolean mustChoose) throws ClientDisconnectedException {
        try {
            return client.chooseAmmo(available, mustChoose);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Sends to the client the list of players in winning order and notifies the end of the game
     * @param winnerList Ordered players' list
     * @throws ClientDisconnectedException In case of client unexpected disconnection
     */
    synchronized public void endGame(List<Player> winnerList) throws ClientDisconnectedException {
        try {
            List<PlayerView> winners = new ArrayList<>();
            for(Player p : winnerList)
            {
                winners.add(p.getView());
            }

            client.endGame(winners);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Sends a general message to the user to be displayed
     * @param payload Message payload
     * @throws ClientDisconnectedException If the client disconnects
     */
    synchronized public void sendMessage(String payload) throws ClientDisconnectedException {
        try {
            client.sendMessage(payload);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Returns true indifferently, needed from the server to ping the client
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    synchronized public void clientPing() throws ClientDisconnectedException {
        try {
            client.clientPing();
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Cancels current connection
     */
    synchronized public void cancelConnection() {
        //Method called by server to interrupt connection, no need to do anything in RMI
    }

}
