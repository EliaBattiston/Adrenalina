package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLInterface implements UserInterface {

    private Scanner in;
    private PrintWriter stdout;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public CLInterface() {
        in = new Scanner(System.in);
        stdout = new PrintWriter(System.out);
    }

    /**
     * Print of string line (with terminator) method
     * @param toBePrinted String to be printed
     */
    private void println(String toBePrinted) {
        stdout.println(toBePrinted);
        stdout.flush();
    }

    /**
     * Print of string method
     * @param toBePrinted String to be printed
     */
    private void print(String toBePrinted) {
        stdout.print(toBePrinted);
        stdout.flush();
    }


    public static void main(String[] args) {
        List<Action> list = new ArrayList<>();
        List<Color> cost = new ArrayList<>();
        List<Weapon> weaplist = new ArrayList<>();
        List<Power> powlist = new ArrayList<>();
        List<Point> pointlist = new ArrayList<>();
        cost.add(Color.BLUE);
        cost.add(Color.YELLOW);
        cost.add(Color.BLUE);
        cost.add(Color.BLUE);
        cost.add(Color.RED);
        cost.add(Color.RED);
        list.add(new Action("Prova1", "Desc1", cost, null));
        list.add(new Action("Prova2", "Desc2", cost, null));
        list.add(new Action("Prova3", "Desc3", cost, null));
        weaplist.add(new Weapon(0, "Weapon1", "Notes1",new Action("WeapAct", "", cost, null), null, null, Color.BLUE));
        weaplist.add(new Weapon(1, "Weapon2", "Notes2",new Action("WeapAct", "", cost, null), null, null, Color.RED));
        weaplist.add(new Weapon(2, "Weapon3", "Notes3",new Action("WeapAct", "", cost, null), null, null, Color.YELLOW));
        powlist.add(new Power(0, "Power1", new Action("PowAct", "", cost, null), Color.BLUE));
        powlist.add(new Power(1, "Power2", new Action("PowAct", "", cost, null), Color.YELLOW));
        powlist.add(new Power(2, "Power3", new Action("PowAct", "", cost, null), Color.RED));
        pointlist.add(new Point(0,0));
        pointlist.add(new Point(1,2));
        pointlist.add(new Point(0,2));
        pointlist.add(new Point(3,0));
        pointlist.add(new Point(3,2));

        CLInterface inter = new CLInterface();

        /*
        System.out.println(ANSI_GREEN_BACKGROUND + inter.chooseAction(list, true).getName() + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.chooseAction(list, false).getName() + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.chooseDirection(false).toString() + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.chooseFrenzy().toString() + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.chooseWeapon(weaplist, false) + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.getSkullNum().toString() + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.discardWeapon(weaplist, false) + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.grabWeapon(weaplist, false) + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.choosePower(powlist, false) + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.reload(weaplist, false) + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.choosePosition(pointlist, false) + ANSI_RESET);

        System.out.println(ANSI_GREEN_BACKGROUND + inter.discardPower(powlist, false) + ANSI_RESET);
        
         */
    }



    /**
     * Update the actual gameView to the client
     * @param gameView current game view
     */
    public void updateGame(GameView gameView) {
        ;
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available, boolean mustChoose) {
        println("Azioni disponibili:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            println("[0] Non fare nulla");
        }
        for(Action disp: available) {
            i++;
            List<Color> cost = disp.getCost();
            print("[" + i + "] " + disp.getName() + " (" + disp.getDescription() + "), costo: ");
            for(Color c: cost) {
                //TODO check how to change the IDE encoding
                switch (c) {
                    case BLUE:
                        print(ANSI_BLUE + (char) 254 + ANSI_RESET + " ");
                        break;
                    case RED:
                        print(ANSI_RED + (char) 254 + ANSI_RESET + " ");
                        break;
                    case YELLOW:
                        print(ANSI_YELLOW + (char) 254 + ANSI_RESET + " ");
                        break;
                }
            }
            println("");
        }
        do {
            print("Qual è la tua scelta? [" + starting + "-" + i + "]: ");
            choose = in.nextInt();
        }while (choose < starting || choose > i);

        if(choose == 0)
            return null;
        else
            return available.get(choose - 1);
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     *
     */
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose) {
        println("Armi disponibili:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            println("[0] Non scegliere nulla");
        }
        for(Weapon disp: available) {
            i++;
            println("[" + i + "] " + disp.getName());
        }
        do {
            print("Qual è la tua scelta? [" + starting + "-" + i + "]: ");
            choose = in.nextInt();
        }while (choose < starting || choose > i);

        if(choose == 0)
            return null;
        else
            return available.get(choose - 1);
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose) {
        println("Armi disponibili nella cella:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            println("[0] Non scegliere nulla");
        }
        for(Weapon disp: grabbable) {
            i++;
            println("[" + i + "] " + disp.getName());
        }
        do {
            print("Qual è la tua scelta? [" + starting + "-" + i + "]: ");
            choose = in.nextInt();
        }while (choose < starting || choose > i);

        if(choose == 0)
            return null;
        else
            return grabbable.get(choose - 1);
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Weapon to be reloaded
     */
    public Weapon reload(List<Weapon> reloadable, boolean mustChoose) {
        println("Armi ricaricabili:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            println("[0] Non scegliere nulla");
        }
        for(Weapon disp: reloadable) {
            i++;
            List<Color> cost = disp.getBase().getCost();
            print("[" + i + "] " + disp.getName() + ", costo: ");
            for(Color c: cost) {
                //TODO check how to change the IDE encoding
                switch (c) {
                    case BLUE:
                        print(ANSI_BLUE + (char) 254 + ANSI_RESET + " ");
                        break;
                    case RED:
                        print(ANSI_RED + (char) 254 + ANSI_RESET + " ");
                        break;
                    case YELLOW:
                        print(ANSI_YELLOW + (char) 254 + ANSI_RESET + " ");
                        break;
                }
            }
            println("");
        }
        do {
            print("Qual è la tua scelta? [" + starting + "-" + i + "]: ");
            choose = in.nextInt();
        }while (choose < starting || choose > i);

        if(choose == 0)
            return null;
        else
            return reloadable.get(choose - 1);
    }

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the player will be when he's done moving
     */
    public Point movePlayer(List<Point> destinations, boolean mustChoose) {
        println("Movimenti possibili:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            println("[0] Non muoverti");
        }
        for(Point disp: destinations) {
            i++;
            println("[" + i + "] " + (disp.getY() * 4 + disp.getX()));
            //TODO Implements Map highlighting of possible moving points
        }
        do {
            print("Qual è la tua scelta? [" + starting + "-" + i + "]: ");
            choose = in.nextInt();
        }while (choose < starting || choose > i);

        if(choose == 0)
            return null;
        else
            return destinations.get(choose - 1);
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets, boolean mustChoose) {
        return targets.get(0);
    }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the enemy will be after being moved
     */
    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose) {
        //TODO come per movePlayer
        return destinations.get(0);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) {
        println("Scegli quale carta potenziamento scartare:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            println("[0] Non scartare nessuna carta");
        }
        for(Power pow: powers) {
            i++;
            print("[" + i + "] " + pow.getName() + ", costo: ");
            switch (pow.getColor()) {
                case BLUE:
                    println(ANSI_BLUE + (char) 254 + ANSI_RESET + " ");
                    break;
                case RED:
                    println(ANSI_RED + (char) 254 + ANSI_RESET + " ");
                    break;
                case YELLOW:
                    println(ANSI_YELLOW + (char) 254 + ANSI_RESET + " ");
                    break;
            }
        }
        do {
            print("Qual è la tua scelta? [" + starting + "-" + i + "]: ");
            choose = in.nextInt();
        }while (choose < starting || choose > i);

        if(choose == 0)
            return null;
        else
            return powers.get(choose - 1);
    }

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen room
     */
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) {
        println("Scegli una stanza:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            println("[-1] Nessuna scelta");
        }
        for(Integer room: rooms) {
            i++;
            println("Stanza " + room);
        }
        do {
            print("Qual è la tua scelta? [" + starting + "-" + i + "]: ");
            choose = in.nextInt();
        }while (!rooms.contains(choose) || choose == -1);

        if(choose == 0)
            return null;
        else
            return choose;
    }

    /**
     * Asks the player to choose a direction
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     */
    public Direction chooseDirection(boolean mustChoose) {
        println("Scegli una direzione:");
        int i = 0;
        int starting = 1;
        String choose;
        if(!mustChoose) {
            starting = 0;
            println("0 - Nessuna scelta");
        }

        println("N - Nord");
        println("E - Est");
        println("S - Sud");
        println("W - Ovest");

        do {
            print("La tua scelta: ");
            choose = in.nextLine();
        }while (!choose.equalsIgnoreCase("N") && !choose.equalsIgnoreCase("W") && !choose.equalsIgnoreCase("S") && !choose.equalsIgnoreCase("E") && !choose.equalsIgnoreCase("0"));

        switch (choose.toUpperCase()) {
            case "N":
                return Direction.NORTH;
            case "E":
                return Direction.EAST;
            case "S":
                return Direction.SOUTH;
            case "W":
                return Direction.WEST;
            default:
                return null;
        }
    }

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen position
     */
    public Point choosePosition(List<Point> positions, boolean mustChoose) {
        println("Scegli una cella della mappa:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            println("[0] Non scegliere");
        }
        for(Point disp: positions) {
            i++;
            println("[" + i + "] " + (disp.getY() * 4 + disp.getX()));
            //TODO Implements Map highlighting of possible moving points
        }
        do {
            print("Qual è la tua scelta? [" + starting + "-" + i + "]: ");
            choose = in.nextInt();
        }while (choose < starting || choose > i);

        if(choose == 0)
            return null;
        else
            return positions.get(choose - 1);
    }

    /**
     * Asks the user for the nickname
     * @return user's nickname
     */
    public String getNickname() {
        String nick;
        print("Il tuo nickname: ");
        nick = in.nextLine();
        return nick;
    }

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     */
    public String getPhrase() {
        print("La tua esclamazione: ");
        return in.nextLine();
    }

    /**
     * Asks the user fot the fighter
     * @return user's fighter
     */
    public Fighter getFighter() {
        println("[1] Dstruttor3");
        println("[2] Banshee");
        println("[3] Dozer");
        println("[4] Violetta");
        println("[5] Sprog");

        int chosen = 0;
        while(chosen < 1 || chosen > 5)
        {
            print("Scegli il tuo personaggio [1-5]: ");
            chosen = in.nextInt();
        }

        return Fighter.values()[chosen-1];
    }

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     */
    public Integer getSkullNum() {
        int num;
        do {
            print("Scegli con quanti teschi vuoi giocare [5-8]: ");
            num = in.nextInt();
        }
        while(num < 5 || num > 8);
        return num;
    }

    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose) {
        println("Scegli un'arma da scartare:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            println("[0] Non scegliere nulla");
        }
        for(Weapon weapon: inHand) {
            i++;
            print("[" + i + "] " + weapon.getName() + " ");
            switch (weapon.getColor()) {
                case BLUE:
                    println(ANSI_BLUE + (char) 254 + ANSI_RESET + " ");
                    break;
                case RED:
                    println(ANSI_RED + (char) 254 + ANSI_RESET + " ");
                    break;
                case YELLOW:
                    println(ANSI_YELLOW + (char) 254 + ANSI_RESET + " ");
                    break;
            }
        }
        do {
            print("Qual è la tua scelta? [" + starting + "-" + i + "]: ");
            choose = in.nextInt();
        }while (choose < starting || choose > i);

        if(choose == 0)
            return null;
        else
            return inHand.get(choose - 1);
    }

    /**
     * Asks the user to choose which map he wants to use
     * @return Number of the chosen map
     */
    public Integer chooseMap() {
        return 0;
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     */
    public Boolean chooseFrenzy() {
        String ans;
        do {
            print("Vuoi la modalità Frenesia a fine partita? [S/N]: ");
            ans = in.nextLine();
        }
        while(!ans.equalsIgnoreCase("s") && !ans.equalsIgnoreCase("n"));
        return ans.toLowerCase() == "s";
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose) {
        println("Scegli un potenziamento da usare:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            println("[0] Non usare nessun potenziamento");
        }
        for(Power pow: inHand) {
            i++;
            print("[" + i + "] " + pow.getName() + ", costo: ");
            switch (pow.getColor()) {
                case BLUE:
                    println(ANSI_BLUE + (char) 254 + ANSI_RESET + " ");
                    break;
                case RED:
                    println(ANSI_RED + (char) 254 + ANSI_RESET + " ");
                    break;
                case YELLOW:
                    println(ANSI_YELLOW + (char) 254 + ANSI_RESET + " ");
                    break;
            }
        }
        do {
            print("Qual è la tua scelta? [" + starting + "-" + i + "]: ");
            choose = in.nextInt();
        }while (choose < starting || choose > i);

        if(choose == 0)
            return null;
        else
            return inHand.get(choose - 1);
    }
}
