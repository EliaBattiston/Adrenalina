package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.MatchView;

import java.rmi.RemoteException;
import java.util.List;

public class RMIConn implements Connection
{
    /**
     * Client instance
     */
    Client client;

    /**
     * Client Registry ID
     */
    String registryID;

    RMIConn(Client client, String ID) {
        this.client = client;
        registryID = ID;
    }

    /**
     * Send the actual matchView to the client
     * @param matchView current match view
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    @Override
    public void updateGame(MatchView matchView) throws ClientDisconnectedException {
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
    public Action chooseAction(List<Action> available, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            String lambdaID = client.chooseAction(available, mustChoose).getLambdaID();
            for(Action a : available)
                if(a.getLambdaID().equals(lambdaID))
                    return a;
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
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            int id = client.chooseWeapon(available, mustChoose).getId();
            for(Weapon w : available)
                if(w.getId() == id)
                    return w;
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
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            int id = client.grabWeapon(grabbable, mustChoose).getId();
            for(Weapon w : grabbable)
                if(w.getId() == id)
                    return w;
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
    public Weapon reload(List<Weapon> reloadable, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            int id = client.reload(reloadable, mustChoose).getId();
            for(Weapon w : reloadable)
                if(w.getId() == id)
                    return w;
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
    public Point movePlayer(List<Point> destinations, boolean mustChoose) throws ClientDisconnectedException
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
    public Player chooseTarget(List<Player> targets, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            String nickChosen = client.chooseTarget(targets, mustChoose).getNick();
            for(Player p : targets)
                if(p.getNick().equals(nickChosen))
                    return p;
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

    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose) throws ClientDisconnectedException
    {
        try {
            return client.moveEnemy(enemy, destinations, mustChoose);
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
            int id = client.discardPower(powers, mustChoose).getId();
            return powers.stream().filter(p->p.getId()==id).findFirst().orElse(null);
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
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) throws ClientDisconnectedException {
        try {
            Integer room = client.chooseRoom(rooms, mustChoose);
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
    public Direction chooseDirection(List<Direction> possible, boolean mustChoose) throws ClientDisconnectedException {
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
    public Point choosePosition(List<Point> positions, boolean mustChoose) throws ClientDisconnectedException {
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
    public String getNickname() throws ClientDisconnectedException {
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
    public String getPhrase() throws ClientDisconnectedException {
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
    public Fighter getFighter(List<Fighter> available) throws ClientDisconnectedException {
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
    public Integer getSkullNum() throws ClientDisconnectedException {
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
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose) throws ClientDisconnectedException
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
    public Integer chooseMap() throws ClientDisconnectedException {
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
    public Boolean chooseFrenzy() throws ClientDisconnectedException {
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
    public Power choosePower(List<Power> inHand, boolean mustChoose) throws ClientDisconnectedException {
        try {
            Power chosen = client.choosePower(inHand, mustChoose);
            return inHand.stream().filter(p -> p.getId() == chosen.getId()).findFirst().orElse(null);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    /**
     * Sends a general message to the user to be displayed
     * @param payload Message payload
     * @throws ClientDisconnectedException
     */
    public void sendMessage(String payload) throws ClientDisconnectedException {
        try {
            client.sendMessage(payload);
        }
        catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

}
