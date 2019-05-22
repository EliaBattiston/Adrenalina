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

    public static void main(String[] args){
        new GuiInterface(args);

        while(true)
            ;
    }

    public GuiInterface(String[] args){
        exchanger = GuiExchanger.getInstance();

        new Thread(()-> javafx.application.Application.launch(Gui.class, args)).start();

        try {
            Thread.sleep(1000);
            System.out.println("Start test gui");

            String ip = getIPAddress();
            boolean isRMI = this.useRMI();
            String nick = getNickname();
            String phrase = getPhrase();
            //Fighter f= getFighter();
            int s = getSkullNum();

            chooseFrenzy();


            System.out.println("IP choosen: " + ip + ", RMI: " + isRMI + "  " + nick + "   " + phrase);

            //System.out.println(f.toString() + "  " + s);

            this.updateGame(initForTest());

            List<Action> a = new ArrayList<>();
            a.addAll(Activities.getInstance().getAvailable(4, false, false));

            Action cho = chooseAction(a, true);

            System.out.println(cho.getName());

            List<Point> dest = new ArrayList<>();
            dest.add(new Point(0,0));
            dest.add(new Point(0,1));
            dest.add(new Point(2,0));

            Point chosenP = movePlayer(dest, true);
            System.out.println(chosenP.getX() + " y: "+ chosenP.getY());

            List<Weapon> goodW = new ArrayList<>();
            goodW.add(new Weapon(1, "", "", null, null, null, Color.RED));
            goodW.add(new Weapon(2, "", "", null, null, null, Color.RED));
            goodW.add(new Weapon(3, "", "", null, null, null, Color.RED));
            goodW.add(new Weapon(4, "", "", null, null, null, Color.RED));
            goodW.add(new Weapon(5, "", "", null, null, null, Color.RED));
            goodW.add(new Weapon(6, "", "", null, null, null, Color.RED));
            goodW.add(new Weapon(7, "", "", null, null, null, Color.RED));
            goodW.add(new Weapon(8, "", "", null, null, null, Color.RED));
            goodW.add(new Weapon(9, "", "", null, null, null, Color.RED));

            Weapon w = chooseWeapon(goodW, false);

            Thread.sleep(200);

            System.out.println("Done, chosen weapon: " + w.getName());

            Thread.sleep(500);
            //getFighter();

            //getNickname();

            List<Integer> rooms = new ArrayList<>();
            rooms.add(1);
            rooms.add(2);
            rooms.add(3);

            Integer choosen = chooseRoom(rooms, false);


            /*List<Player> players = new ArrayList<>();
            players.add(new Player("aaa", "yay", Fighter.SPROG));
            players.add(new Player("aaa", "yay", Fighter.DOZER));

            chooseTarget(players, true);*/

        }catch (InterruptedException e){

        }
    }

    private MatchView initForTest() {
        //Settings for testing
        try {
            Game allGame = Game.jsonDeserialize("baseGame.json");
            ArrayList<Player> players = new ArrayList<>();
            players.add(new Player("p1", "!", Fighter.VIOLETTA));
            players.add(new Player("p2", "!", Fighter.DSTRUTTOR3));
            players.add(new Player("p3", "!", Fighter.SPROG));
            players.add(new Player("p4", "!", Fighter.BANSHEE));
            players.add(new Player("p5", "!", Fighter.DOZER));

            Player me = players.get(0);

            me.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                allGame.getWeaponsDeck().shuffle();
                weapons[0] = allGame.getWeaponsDeck().draw();
                weapons[1] = allGame.getWeaponsDeck().draw();

                allGame.getPowersDeck().shuffle();
                powers[0] = allGame.getPowersDeck().draw();
                powers[1] = allGame.getPowersDeck().draw();

                ammo.add(Color.YELLOW, 2);
                ammo.add(Color.BLUE, 1);

                damage[0] = "p2";
                damage[1] = "p2";
                damage[2] = "p3";

                marks.addAll(Arrays.asList("p2", "p3", "p3"));
                marks.addAll(Arrays.asList("p4", "p5", "p4"));
            }));

            allGame.loadMap(1);

            for(int x = 0; x < 4; x++)
                for(int y = 0; y < 3; y++)
                    if(allGame.getMap().getCell(x, y) != null)
                        allGame.getMap().getCell(x, y).refill(allGame);

            //it's just for test
            for(Player p:players){
                int x, y;
                do {
                    x = new Random().nextInt(4);
                    y = new Random().nextInt(3);
                }while(allGame.getMap().getCell(x, y) == null);

                allGame.getMap().getCell(x, y).addPawn(p);
            }

            return new MatchView(new GameView(allGame.getMap(), players, null), me, me, 3, GamePhase.REGULAR, true, me);
        }catch (Exception ex){
            ;
        }
        return null;
    }

    /**
     * Update the actual gameView to the client
     *
     * @param matchView current game view
     */
    @Override
    public void updateGame(MatchView matchView) {
        exchanger.setRequest(Interaction.UPDATEVIEW, "Updating view...", matchView, false);
        goToWait();
    }

    /**
     * Asks the user for the nickname
     *
     * @return user's nickname
     */
    @Override
    public String getNickname() {
        exchanger.setRequest(Interaction.GETNICKNAME, "Inserisci il tuo nickname", null, true);
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
            goToWait();
            return (Action) exchanger.getAnswer();
        }

        return null;
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
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
        goToWait();
        return (boolean)exchanger.getAnswer();
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
        goToWait();
        return (boolean)exchanger.getAnswer();
    }

    /**
     * Asks the user for the IP address of the server
     * @return Server's IP address
     */
    public String getIPAddress(){
        exchanger.setRequest(Interaction.SERVERIP, "Inserisci l'IP del server.", null, true);
        goToWait();
        return (String)exchanger.getAnswer();
    }

    /**
     * Asks the user for the IP address of the local machine
     * @return Server's IP address
     */
    public String getLocalAddress(List<String> possibleIP){
        return possibleIP.get(0);//todo add the gui method for this
    }

    private void goToWait(){
        try {
            while (!exchanger.isFreeToUse())
                Thread.sleep(150);
        }catch (InterruptedException e){
        }
    }
}