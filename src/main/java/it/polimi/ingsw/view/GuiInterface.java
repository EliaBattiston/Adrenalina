package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GamePhase;
import it.polimi.ingsw.controller.Interaction;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GuiInterface implements UserInterface{
    private GuiExchanger exchanger;

    public GuiInterface(){
        String[] args = new String[2];
        exchanger = GuiExchanger.getInstance();

        new Thread(()-> javafx.application.Application.launch(Gui.class, args)).start();
    }

    /**
     * Update the actual gameView to the client
     *
     * @param matchView current game view
     */
    @Override
    public void updateGame(MatchView matchView) {
        exchanger.setRequest(Interaction.UPDATEVIEW, "Updating view...", matchView, false);
        exchanger.waitFreeToUse();
    }

    /**
     * Asks the user for the nickname
     *
     * @return user's nickname
     */
    @Override
    public String getNickname() {
        exchanger.setRequest(Interaction.GETNICKNAME, "Inserisci il tuo nickname", null, true);
        exchanger.waitFreeToUse();
        return (String)exchanger.getAnswer();
    }

    /**
     * Asks the user for the effect phrase
     *
     * @return user's effect phrase
     */
    @Override
    public String getPhrase() {
        exchanger.setRequest(Interaction.GETPHRASE, "Inserisci la tua frase di battaglia", null, true);
        exchanger.waitFreeToUse();
        return (String)exchanger.getAnswer();
    }

    /**
     * Asks the user fot the fighter
     *
     * @return user's fighter
     */
    @Override
    public Fighter getFighter(List<Fighter> available) {
        exchanger.setRequest(Interaction.GETFIGHTER, "Scegli il tuo combattente", available, true);
        exchanger.waitFreeToUse();
        return (Fighter) exchanger.getAnswer();
    }

    /**
     * Asks the user how many skulls he wants in the play
     *
     * @return skulls number
     */
    @Override
    public Integer getSkullNum() {
        exchanger.setRequest(Interaction.GETSKULLSNUM, "Scegli il numero di teschi da usare", null, true); //todo (after merge) fix the request
        exchanger.waitFreeToUse();
        return (Integer) exchanger.getAnswer();
    }

    /**
     * Asks the user to choose between a set of actions he can use
     *
     * @param available  List of available actions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen action
     */
    @Override
    public Action chooseAction(List<Action> available, boolean mustChoose) {
        //if id "a-*" -> run, shoot...
        if(available.get(0).getLambdaID().contains("a-")) {
            exchanger.setRequest(Interaction.CHOOSEBASEACTION, "Scegli un'azione di base", available, true);
            exchanger.waitFreeToUse();
            return (Action) exchanger.getAnswer();
        }

        //todo implement the powers and weapons

        return available.get(new Random().nextInt(available.size()));
        //if "p-" power

        //if w- action of weapon

    }

    /**
     * Asks the user to choose between a set of his weapons
     *
     * @param available  List of available weapons
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    @Override
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSEWEAPON, "Scegli un'arma con cui sparare", available, mustChoose);
        exchanger.waitFreeToUse();
        return (Weapon)exchanger.getAnswer();
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     *
     * @param grabbable  List of weapons that can be picked up by the player
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    @Override
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSEWEAPON, "Scegli un'arma da raccogliere", grabbable, mustChoose);
        exchanger.waitFreeToUse();
        return (Weapon)exchanger.getAnswer();
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     *
     * @param reloadable Weapons that are currently not loaded
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Weapon to be reloaded
     */
    @Override
    public Weapon reload(List<Weapon> reloadable, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSEWEAPON, "Scegli un'arma da ricaricare", reloadable, mustChoose);
        exchanger.waitFreeToUse();
        return (Weapon)exchanger.getAnswer();
    }

    /**
     * Asks the user to choose which weapon to discard
     *
     * @param inHand     List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    @Override
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSEWEAPON, "Scegli un'arma da scartare", inHand, mustChoose);
        exchanger.waitFreeToUse();
        return (Weapon)exchanger.getAnswer();
    }

    /**
     * Asks the user to choose a power to use
     *
     * @param inHand     List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    @Override
    public Power choosePower(List<Power> inHand, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSEPOWER, "Scegli una power da usare", inHand, mustChoose);
        exchanger.waitFreeToUse();
        return (Power)exchanger.getAnswer();
    }

    /**
     * Asks the user to discard one power card
     *
     * @param powers     List of power cards in player's hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Card to be discarded
     */
    @Override
    public Power discardPower(List<Power> powers, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSEPOWER, "Scegli una power da scartare", powers, mustChoose);
        exchanger.waitFreeToUse();
        return (Power)exchanger.getAnswer();
    }

    /**
     * Asks the user where he wants to movePlayer
     *
     * @param destinations Possible destinations for the user
     * @param mustChoose   If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the player will be when he's done moving
     */
    @Override
    public Point movePlayer(List<Point> destinations, boolean mustChoose) {
        exchanger.setRequest(Interaction.MOVEPLAYER, "Scegli dove muoverti", destinations, mustChoose);
        exchanger.waitFreeToUse();
        return (Point) exchanger.getAnswer();
    }

    /**
     * Asks the user to choose a precise position on the map
     *
     * @param positions  list of possible positions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen position
     */
    @Override
    public Point choosePosition(List<Point> positions, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSEPOSITION, "Scegli una posizione", positions, mustChoose);
        exchanger.waitFreeToUse();
        return (Point) exchanger.getAnswer();
    }

    /**
     * Asks the user where to movePlayer an enemy
     *
     * @param enemy        Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose   If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the enemy will be after being moved
     */
    @Override
    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose) {
        exchanger.setRequest(Interaction.MOVEENEMY, "Scegli dove muovere il nemico", destinations, mustChoose);
        exchanger.waitFreeToUse();
        return (Point) exchanger.getAnswer();
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     *
     * @param targets    List of player that can be targeted
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen target
     */
    @Override
    public Player chooseTarget(List<Player> targets, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSETARGET, "Scegli un nemico", targets, mustChoose);
        exchanger.waitFreeToUse();
        return (Player) exchanger.getAnswer();
    }

    /**
     * Asks the user to choose a room
     *
     * @param rooms      list of possible rooms
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen room
     */
    @Override
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSEROOM, "Scegli una stanza", rooms, mustChoose);
        exchanger.waitFreeToUse();
        return (Integer)exchanger.getAnswer();
    }

    /**
     * Asks the player to choose a direction
     *
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     */
    @Override
    public Direction chooseDirection(List<Direction> possible, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSEDIRECTION, "Scegli una direzione cardinale", possible, mustChoose);
        exchanger.waitFreeToUse();
        return (Direction) exchanger.getAnswer();
    }

    /**
     * Asks the user to choose which map he wants to use
     *
     * @return Number of the chosen map
     */
    @Override
    public Integer chooseMap() {
        exchanger.setRequest(Interaction.CHOOSEROOM, "Con quale mappa vuoi giocare?", null, true);
        exchanger.waitFreeToUse();
        return (Integer) exchanger.getAnswer();
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     *
     * @return True for final Frenzy mode, false elsewhere
     */
    @Override
    public Boolean chooseFrenzy() {
        exchanger.setRequest(Interaction.CHOOSEFRENZY, "Vuoi usare la modalità frenesia?", null, true);
        exchanger.waitFreeToUse();
        return (boolean)exchanger.getAnswer();
    }

    /**
     * Sends to the client the list of players in winning order and notifies the end of the game
     * @param winnerList Ordered players' list
     */
    public void endGame(List<Player> winnerList) {
        exchanger.setActualInteraction(Interaction.CLOSEAPP);
    }

    /**
     * Prints out a general message to the client interface
     * @param message Message to be printed
     */
    public void generalMessage(String message){
        ;//todo implement
    }

    /**
     * Asks the user to choose between TCP and RMI connection
     * @return true in case of RMI connection, false elsewhere
     */
    public boolean useRMI(){
        exchanger.setRequest(Interaction.RMIORSOCKET, "Vuoi usare RMI o Socket?", null, true);
        exchanger.waitFreeToUse();
        return (boolean)exchanger.getAnswer();
    }

    /**
     * Asks the user for the IP address of the server
     * @return Server's IP address
     */
    public String getIPAddress(){
        exchanger.setRequest(Interaction.SERVERIP, "Inserisci l'IP del server.", null, true);
        exchanger.waitFreeToUse();
        return (String)exchanger.getAnswer();
    }

    /**
     * Asks the user for the IP address of the local machine
     * @return Server's IP address
     */
    public String getLocalAddress(List<String> possibleIP){
        return possibleIP.get(0);//todo add the gui method for this
    }
}
