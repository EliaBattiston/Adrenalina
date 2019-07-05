package it.polimi.ingsw.view;

import it.polimi.ingsw.clientmodel.MatchView;
import it.polimi.ingsw.clientmodel.PlayerView;
import it.polimi.ingsw.controller.GamePhase;
import it.polimi.ingsw.controller.Interaction;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GuiInterface implements UserInterface and it's the Class that handles the connection between the server and the GUI
 */
public class GuiInterface implements UserInterface{
    /**
     * The exchanger of the information between Gui and GuiInterface
     */
    private GuiExchanger exchanger;

    /**
     * Strings used in this class
     */
    private static final String UPDATING_VIEW = "Updating view...";
    private static final String INSERISCI_IL_TUO_NICKNAME = "Inserisci il tuo nickname";
    private static final String INSERISCI_LA_TUA_FRASE_DI_BATTAGLIA = "Inserisci la tua frase di battaglia";
    private static final String SCEGLI_IL_TUO_COMBATTENTE = "Scegli il tuo combattente";
    private static final String SCEGLI_IL_NUMERO_DI_TESCHI_DA_USARE = "Scegli il numero di teschi da usare";
    private static final String ACTIVITY_PREFIX = "a-";
    private static final String SCEGLI_UN_AZIONE_DI_BASE = "Scegli un'azione di base";
    private static final String WEAPON_PREFIX = "w";
    private static final String SCEGLI_L_AZIONE_DA_USARE = "Scegli l'azione da usare";
    private static final String ACTION_REQUESTED_CAN_T_BE_HANDLED = "Action requested can't be handled!!!";
    private static final String SCEGLI_UN_ARMA_CON_CUI_SPARARE = "Scegli un'arma con cui sparare";
    private static final String SCEGLI_UN_ARMA_DA_RACCOGLIERE = "Scegli un'arma da raccogliere";
    private static final String SCEGLI_UN_ARMA_DA_RICARICARE = "Scegli un'arma da ricaricare";
    private static final String SCEGLI_UN_ARMA_DA_SCARTARE = "Scegli un'arma da scartare";
    private static final String SCEGLI_UNA_POWER_DA_USARE = "Scegli una power da usare";
    private static final String SCEGLI_UNA_CARTA_POWER = "Scegli una carta power";
    private static final String SCEGLI_DOVE_MUOVERTI = "Scegli dove muoverti";
    private static final String SCEGLI_UNA_POSIZIONE = "Scegli una posizione";
    private static final String SCEGLI_UNA_MUNIZIONE_DA_USARE = "Scegli una munizione da usare";
    private static final String SCEGLI_DOVE_MUOVERE_IL_NEMICO = "Scegli dove muovere il nemico";
    private static final String SCEGLI_UN_NEMICO = "Scegli un nemico";
    private static final String SCEGLI_UNA_STANZA = "Scegli una stanza";
    private static final String SCEGLI_UNA_DIREZIONE_CARDINALE = "Scegli una direzione cardinale";
    private static final String CON_QUALE_MAPPA_VUOI_GIOCARE = "Con quale mappa vuoi giocare?";
    private static final String VUOI_USARE_LA_MODALITÀ_FRENESIA = "Vuoi usare la modalità frenesia?";
    private static final String ENDGAME = "ENDGAME\n";
    private static final String VUOI_USARE_RMI_O_SOCKET = "Vuoi usare RMI o Socket?";
    private static final String INSERISCI_L_IP_DEL_SERVER = "Inserisci l'IP del server.";
    private static final String INDIRIZZO_IP_NON_CORRETTAMENTE_FORMATTATO = "Indirizzo IP non correttamente formattato\nInserisci l'IP del server.";
    private static final String SELEZIONA_L_INDIRIZZO_SU_CUI_VUOI_GIOCARE = "Seleziona l'indirizzo su cui vuoi giocare";

    //TODO remove main and test pieces
    public static void main(String[] args){
        new GuiInterface().tester();
    }

    /**
     * Build the GuiInterface and launch the GUI thread
     */
    public GuiInterface(){
        String[] args = new String[2];
        exchanger = GuiExchanger.getInstance();

        new Thread(()-> javafx.application.Application.launch(Gui.class, args)).start();
    }

    /**
     * Run a test of the implementation
     */
    private void tester(){
        try {
            println("Start test gui");



            //String ip = getIPAddress();
            //boolean isRMI = this.useRMI();
            //String nick = getNickname();
            //String phrase = getPhrase();
            //Fighter f= getFighter();
            //int s = getSkullNum();
/*
            chooseFrenzy();


            println("IP choosen: " + ip + ", RMI: " + isRMI + "  " + nick + "   " + phrase);

            println(f.toString() + "  " + s);
*/
            MatchView testView = initForTest();
            this.updateGame( testView );

            List<Direction> possible = new ArrayList<>();
            possible.add(Direction.NORTH);
            possible.add(Direction.SOUTH);
            this.chooseDirection(possible, true);

            Activities ac = Activities.getInstance();

            List<Action> available = ac.getAvailable(9, true, true);

            Action chosen = chooseAction(available, false);

            println(chosen==null?"null":chosen.getName() + " scelta");

            available = ac.getAvailable(9, true, false);

            chosen = chooseAction(available, false);

            println(chosen==null?"null":chosen.getName() + " scelta");

            /*List<Power> p = new ArrayList<>();
            p.add(new Power(1, "sdb", null, Color.RED));
            p.add(new Power(4, "sdb", null, Color.RED));
            p.add(new Power(5, "sdb", null, Color.BLUE));
            p.add(new Power(18, "sdb", null, Color.RED));

            discardPower(p, true);*/
/*
            List<Player> players = new ArrayList<>();

            players.add(new Player("p4", "!", Fighter.BANSHEE));
            players.add(new Player("p5", "!", Fighter.DOZER));*/

            /*List<Integer> rooms = new ArrayList<>();
            rooms.add(1);
            rooms.add(2);
            rooms.add(3);

            Integer choosen = chooseRoom(rooms, true);
            println(choosen!=null?choosen:"null");
*/

            /*List<Color> color = new ArrayList<>();
            color.add(Color.RED);
            color.add(Color.BLUE);
            color.add(Color.YELLOW);

            Color chosen = chooseAmmo(color, false);
            println(chosen==null?"null":chosen);*/

            /*List<Action> actions = new ArrayList<>();
            actions.add(new Action("trdsbdnbnbfy", "asvdsbsd", null, "w2-a"));
            actions.add(new Action("try", "asvdsjnzfjbn jdzfnbkjdzfnbjknzfjbjzfdnbjzfndbjzdhjfjzfnvbjdf<nvijzdnfijbnbsd", null, "w2-a"));

            this.chooseAction(actions, false);*/

            /*Player chosen = chooseTarget(players, false);

            if(chosen != null)
                println(chosen.toString());
            else
                println("NULL");*/


            /*List<Action> a = new ArrayList<>();
            a.addAll(Activities.getInstance().getAvailable(4, false, false));

            Action cho = chooseAction(a, true);

            println(cho.getName());
*/
            /*List<Point> dest = Map.possibleMovements(testView.getMyPlayer().getPosition(), 1, testView.getGame().getMap());

            Point chosenP = movePlayer(dest,false);
            if(chosenP == null)
                println("null");
            else
                println(chosenP.getX() + " y: "+ chosenP.getY());

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

            if(w!=null)
                println("Done, chosen weapon: " + w.getName());

            Thread.sleep(500);
            //getFighter();

            //getNickname();*/



            /*List<Player> players = new ArrayList<>();
            players.add(new Player("aaa", "yay", Fighter.SPROG));
            players.add(new Player("aaa", "yay", Fighter.DOZER));
            chooseTarget(players, true);*/

           /* List<Direction> possible = new ArrayList<>();
            possible.add(Direction.NORTH);
            possible.add(Direction.SOUTH);
            chooseDirection(possible, true);*/

        }catch (Exception e){
            //Check for errors not needed
        }
    }

    /**
     * Initialize a test MatchView for a fast debugging
     * @return a simple MatchView
     */
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

            for(Player p: players)
                allGame.addPlayer(p);

            Player me = players.get(0);

            me.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                allGame.getWeaponsDeck().shuffle();
                weapons[0] = allGame.getWeaponsDeck().draw();
                weapons[1] = allGame.getWeaponsDeck().draw();
                weapons[1].setLoaded(false);
                weapons[2] = allGame.getWeaponsDeck().draw();
                weapons[2].setLoaded(false);

                allGame.getPowersDeck().shuffle();
                powers[0] = allGame.getPowersDeck().draw();
                powers[1] = allGame.getPowersDeck().draw();

                ammo.add(Color.YELLOW, 2);
                ammo.add(Color.BLUE, 1);

                damage[0] = "p5";
                damage[1] = "p5";
                damage[2] = "p5";

                marks.addAll(Arrays.asList("p2", "p3", "p3"));
                marks.addAll(Arrays.asList("p4", "p5", "p4"));
            }));

            me.addSkull();
            me.addSkull();

            me.setFrenzyBoard(true);

            players.get(1).applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                allGame.getWeaponsDeck().shuffle();
                weapons[0] = allGame.getWeaponsDeck().draw();
                weapons[1] = allGame.getWeaponsDeck().draw();
                weapons[1].setLoaded(false);
                weapons[2] = allGame.getWeaponsDeck().draw();
                weapons[2].setLoaded(false);

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

            players.get(1).addSkull();
            players.get(1).addSkull();
            players.get(1).addSkull();
            players.get(1).addSkull();
            players.get(1).addSkull();
            players.get(1).addSkull();
            players.get(1).addSkull();
            players.get(1).addSkull();

            players.get(2).addSkull();
            players.get(2).addSkull();

            me.addPoints(5);

            allGame.loadMap(1);

            for(int x = 0; x < 4; x++)
                for(int y = 0; y < 3; y++)
                    if(allGame.getMap().getCell(x, y) != null)
                        allGame.getMap().getCell(x, y).refill(allGame);

            //it's just for test
            for(Player p:players){
                int x;
                int y;
                do {
                    x = new Random().nextInt(4);
                    y = new Random().nextInt(3);
                }while(allGame.getMap().getCell(x, y) == null);
                if(p == me)
                    allGame.getMap().getCell(me.getPosition()).addPawn(me);
                else
                    allGame.getMap().getCell(x, y).addPawn(p);
            }

            allGame.initializeSkullsBoard(6);
            allGame.getSkulls()[3].setKiller(me,true);
            allGame.getSkulls()[4].setKiller(me,false);

            return new MatchView(me.getFullView(), me.getView(), allGame.getView(), GamePhase.REGULAR, 99999);
        }catch (Exception ex){
            //No error check needed
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
        exchanger.setRequest(Interaction.UPDATEVIEW, UPDATING_VIEW, matchView, true);
        exchanger.waitFreeToUse();
    }

    /**
     * Asks the user for the nickname
     *
     * @return user's nickname
     */
    @Override
    public String getNickname() {
        exchanger.setRequest(Interaction.GETNICKNAME, INSERISCI_IL_TUO_NICKNAME, null, true);
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
        exchanger.setRequest(Interaction.GETPHRASE, INSERISCI_LA_TUA_FRASE_DI_BATTAGLIA, null, true);
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
        exchanger.setRequest(Interaction.GETFIGHTER, SCEGLI_IL_TUO_COMBATTENTE, available, true);
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
        exchanger.setRequest(Interaction.GETSKULLSNUM, SCEGLI_IL_NUMERO_DI_TESCHI_DA_USARE, null, true);
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
        if(available.get(0).getLambdaID().contains(ACTIVITY_PREFIX)) {
            exchanger.setRequest(Interaction.CHOOSEBASEACTION, SCEGLI_UN_AZIONE_DI_BASE, available, mustChoose);
            exchanger.waitFreeToUse();
            return (Action) exchanger.getAnswer();
        }

        //weapons
        if(available.get(0).getLambdaID().contains(WEAPON_PREFIX)) {
            exchanger.setRequest(Interaction.CHOOSEWEAPONACTION, SCEGLI_L_AZIONE_DA_USARE, available, mustChoose);
            exchanger.waitFreeToUse();
            return (Action) exchanger.getAnswer();
        }

        //Not sure 100% there's no other type of action
        Logger.getGlobal().log(Level.SEVERE, ACTION_REQUESTED_CAN_T_BE_HANDLED);
        return null;
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
        exchanger.setRequest(Interaction.CHOOSEWEAPON, SCEGLI_UN_ARMA_CON_CUI_SPARARE, available, mustChoose);
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
        exchanger.setRequest(Interaction.CHOOSEWEAPON, SCEGLI_UN_ARMA_DA_RACCOGLIERE, grabbable, mustChoose);
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
        exchanger.setRequest(Interaction.CHOOSEWEAPON, SCEGLI_UN_ARMA_DA_RICARICARE, reloadable, mustChoose);
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
        exchanger.setRequest(Interaction.CHOOSEWEAPON, SCEGLI_UN_ARMA_DA_SCARTARE, inHand, mustChoose);
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
        exchanger.setRequest(Interaction.CHOOSEPOWER, SCEGLI_UNA_POWER_DA_USARE, inHand, mustChoose);
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
        exchanger.setRequest(Interaction.DISCARDPOWER, SCEGLI_UNA_CARTA_POWER, powers, mustChoose);
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
        exchanger.setRequest(Interaction.MOVEPLAYER, SCEGLI_DOVE_MUOVERTI, destinations, mustChoose);
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
        exchanger.setRequest(Interaction.CHOOSEPOSITION, SCEGLI_UNA_POSIZIONE, positions, mustChoose);
        exchanger.waitFreeToUse();
        return (Point) exchanger.getAnswer();
    }

    /**
     * Asks the user which ammo he wants to use
     * @param available List of powers on the player's board which can be used
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Color of the chosen ammo
     */
    @Override
    public Color chooseAmmo(List<Color> available, boolean mustChoose){
        exchanger.setRequest(Interaction.CHOOSEAMMO, SCEGLI_UNA_MUNIZIONE_DA_USARE, available, mustChoose);
        exchanger.waitFreeToUse();
        return (Color) exchanger.getAnswer();
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
    public Point moveEnemy(PlayerView enemy, List<Point> destinations, boolean mustChoose) {
        exchanger.setRequest(Interaction.MOVEENEMY, SCEGLI_DOVE_MUOVERE_IL_NEMICO, destinations, mustChoose);
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
    public PlayerView chooseTarget(List<PlayerView> targets, boolean mustChoose) {
        exchanger.setRequest(Interaction.CHOOSETARGET, SCEGLI_UN_NEMICO, targets, mustChoose);
        exchanger.waitFreeToUse();
        return (PlayerView) exchanger.getAnswer();
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
        exchanger.setRequest(Interaction.CHOOSEROOM, SCEGLI_UNA_STANZA, rooms, mustChoose);
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
        exchanger.setRequest(Interaction.CHOOSEDIRECTION, SCEGLI_UNA_DIREZIONE_CARDINALE, possible, mustChoose);
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
        exchanger.setRequest(Interaction.CHOOSEMAP, CON_QUALE_MAPPA_VUOI_GIOCARE, null, true);
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
        exchanger.setRequest(Interaction.CHOOSEFRENZY, VUOI_USARE_LA_MODALITÀ_FRENESIA, null, true);
        exchanger.waitFreeToUse();
        return (boolean)exchanger.getAnswer();
    }

    /**
     * Sends to the client the list of players in winning order and notifies the end of the game
     * @param winnerList Ordered players' list
     */
    public void endGame(List<PlayerView> winnerList) {
        String message = ENDGAME;

        for(int i = winnerList.size() - 1; i >= 0; i--) {
            message += (i + 1) +"° con " + winnerList.get(i).getPoints() + " punti è " + winnerList.get(i).getNick() + "\n";
        }

        exchanger.setRequest(Interaction.ENDGAME, message, null, true);
    }

    /**
     * Prints out a general message to the client interface
     * @param message Message to be printed
     */
    public void generalMessage(String message){
        exchanger.waitFreeToUse();
        println(message);
        exchanger.setRequest(Interaction.LOG, message, null, true);
        exchanger.waitFreeToUse();
    }

    /**
     * Asks the user to choose between TCP and RMI connection
     * @return true in case of RMI connection, false elsewhere
     */
    public boolean useRMI(){
        exchanger.setRequest(Interaction.RMIORSOCKET, VUOI_USARE_RMI_O_SOCKET, null, true);
        exchanger.waitFreeToUse();
        return (boolean)exchanger.getAnswer();
    }

    /**
     * Asks the user for the IP address of the server
     * @return Server's IP address
     */
    public String getIPAddress(){
        exchanger.setRequest(Interaction.SERVERIP, INSERISCI_L_IP_DEL_SERVER, null, true);
        exchanger.waitFreeToUse();
        String ip = (String)exchanger.getAnswer();

        while(!checkIP(ip)) {
            exchanger.setRequest(Interaction.SERVERIP, INDIRIZZO_IP_NON_CORRETTAMENTE_FORMATTATO, null, true);
            exchanger.waitFreeToUse();
            ip = (String)exchanger.getAnswer();
        }

        return ip;
    }

    /**
     * check it's a valid ip address
     * @param ip the string to be checked
     * @return true if it's an IP address, false otherwise
     */
    private boolean checkIP(String ip) {
        String[] pieces;
        pieces = ip.split("\\.");
        if(ip.equals("localhost"))
            return true;
        else {
            if(pieces.length != 4)
                return false;
            for(String piece: pieces) {
                int n = Integer.parseInt(piece);
                if(n > 255)
                    return false;
            }
            return true;
        }
    }

    /**
     * Asks the user for the IP address of the local machine
     * @return Server's IP address
     */
    public String getLocalAddress(List<String> possibleIP){
        exchanger.setRequest(Interaction.ASKLOCALADDRESS, SELEZIONA_L_INDIRIZZO_SU_CUI_VUOI_GIOCARE, possibleIP, true);
        exchanger.waitFreeToUse();
        return (String)exchanger.getAnswer();
    }

    protected static void println(String s) { System.out.println(s);}
}
