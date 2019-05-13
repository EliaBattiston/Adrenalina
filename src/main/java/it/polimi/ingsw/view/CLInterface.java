package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.controller.GamePhase;
import it.polimi.ingsw.model.Map;
import it.polimi.ingsw.model.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * CLI implementation of the user interface
 */
public class CLInterface implements UserInterface {

    /**
     * Input scanner
     */
    private Scanner in;
    /**
     * Output writer
     */
    private PrintWriter stdout;
    /**
     * Instance of the match view (from which taking data)
     */
    private MatchView view;

    /**
     *Color and symbol constants
     */
    private static final int CELLDIM = 20;
    private static final int DOORDIM = 4;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    private static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    private static final String[] ANSI_COLORS = {ANSI_RED, ANSI_BLUE, ANSI_YELLOW, ANSI_WHITE, ANSI_PURPLE, ANSI_GREEN, ANSI_CYAN, ANSI_BLACK};
    private static final String[] ANSI_BACKGROUNDS = {ANSI_RED_BACKGROUND, ANSI_BLUE_BACKGROUND, ANSI_YELLOW_BACKGROUND, ANSI_WHITE_BACKGROUND, ANSI_PURPLE_BACKGROUND, ANSI_GREEN_BACKGROUND, ANSI_CYAN_BACKGROUND, ANSI_BLACK_BACKGROUND};

    private static final String BOX = "◼";//"\u25FC";

    private static final String SPACE = "\u0020";

    private static final String TL_CORNER = "╔";
    private static final String TR_CORNER = "╗";
    private static final String BL_CORNER = "╚";
    private static final String BR_CORNER = "╝";
    private static final String VERT = "║";
    private static final String HOR = "═";

    private static final String L_HOR = "-";
    private static final String L_VERT = "|";


    /**
     * Initialization of the interface, in particular instantiation of scanne and writer over System in and out
     */
    public CLInterface() {
        in = new Scanner(System.in);
        stdout = new PrintWriter(System.out);
    }

    /**
     *Inner cell text formatting (spacing insertion on the right of the text)
     * @param toPrint Text to be formatted
     * @return Formatted text
     */
    private String innerCellFormat(String toPrint) {
        String copy = toPrint.replace(ANSI_RESET, "");
        for(String s: ANSI_COLORS)
            copy = copy.replace(s, "");
        for(String s: ANSI_BACKGROUNDS)
            copy = copy.replace(s, "");

        String ret = toPrint;
        for(int i = 0; i < CELLDIM - 2 - copy.length(); i++) {
            ret += " ";
        }
        return ret;
    }

    /**
     * Inner cell text formatting (spacing insertion on the left of the text)
     * @param toPrint Text to be formatted
     * @return Formatted text
     */
    private String innerCellFormatRight(String toPrint) {
        String copy = toPrint.replace(ANSI_RESET, "");
        for(String s: ANSI_COLORS)
            copy = copy.replace(s, "");
        for(String s: ANSI_BACKGROUNDS)
            copy = copy.replace(s, "");

        String ret = "";
        for(int i = 0; i < CELLDIM - 2 - copy.length(); i++) {
            ret += " ";
        }
        ret += toPrint;
        return ret;
    }

    private String scan() {
        try {
            String line = in.nextLine();
            while(line.equals(""))
                line = in.nextLine();
            return line;
        }
        catch(NoSuchElementException e) {
            return "";
        }
    }

    private int scanInt() {
        int num = 0;
        try {
            num = in.nextInt();
        }
        catch (InputMismatchException e) {
            String s = scan();
            if(s.equalsIgnoreCase("?"))
                num = -1;
            else
                num = -10;
        }
        return num;
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

    /**
     * Formats colored boxes with the right weapon/ammo color
     * @param color Box color
     * @return Formatted and colored box string
     */
    private String formatColorBox(Color color) {
        switch (color) {
            case BLUE:
                return ANSI_BLUE + BOX + ANSI_RESET;
            case RED:
                return ANSI_RED + BOX + ANSI_RESET;
            case YELLOW:
                return ANSI_YELLOW + BOX + ANSI_RESET;
            default:
                return "";
        }
    }

    private String fighterToString(Fighter fighter) {
        String res;
        switch (fighter) {
            case BANSHEE:
                res = ANSI_BLUE + "Banshee" + ANSI_RESET;
                break;
            case SPROG:
                res = ANSI_GREEN + "Sprog" + ANSI_RESET;
                break;
            case DOZER:
                res = ANSI_BLACK + "Dozer" + ANSI_RESET;
                break;
            case VIOLETTA:
                res = ANSI_PURPLE + "Violetta" + ANSI_RESET;
                break;
            case DSTRUTTOR3:
                res = ANSI_YELLOW + "Dstruttor3" + ANSI_RESET;
                break;
            default:
                res = ANSI_RESET;
        }
        return res;
    }

    /**
     * Prints weapon action informations in a well-presented format
     * @param weapon Weapon to print out info
     */
    private void printWeapon(Weapon weapon) {
        print(ANSI_BOLD + weapon.getName() + ANSI_RESET + " " + formatColorBox(weapon.getColor()) + " ");
        for(Color c: weapon.getBase().getCost()) {
            print(formatColorBox(c) + " ");
        }
        println("");
        println("Effetti:");
        println(String.format("%13s: %s", "Base", weapon.getBase().getDescription()));
        if(weapon.getAlternative() != null) {
            String cost = "";
            for(Color c: weapon.getAlternative().getCost()) {
                cost += " " + formatColorBox(c);
            }
            println(String.format("%13s: %s", "Alternativo", "(" + weapon.getAlternative().getName() + ", costo" + cost + ") " + weapon.getAlternative().getDescription()));
        }
        if(weapon.getAdditional() != null) {
            String cost = "";
            for(Color c: weapon.getAdditional().get(0).getCost()) {
                cost += " " + formatColorBox(c);
            }
            println(String.format("%13s: %s", "Addizionale", "(" + weapon.getAdditional().get(0).getName() + ", costo" + cost + ") " + weapon.getAdditional().get(0).getDescription()));
            if(weapon.getAdditional().size() == 2) {
                for(Color c: weapon.getAdditional().get(1).getCost()) {
                    cost += " " + formatColorBox(c);
                }
                println(String.format("%13s: %s", "Addizionale", "(" + weapon.getAdditional().get(1).getName() + ", costo" + cost + ") " + weapon.getAdditional().get(1).getDescription()));
            }
        }
    }

    /**
     * Update the actual gameView to the client
     * @param matchView current game view
     */
    public void updateGame(MatchView matchView) {
        view = matchView;
        map(null, null);
        frenzyInfo();
        playerInfo();
    }

    /**
     * Prints out a general info menu
     */
    private void generalInfo() {
        int sel1;
        do {
            println("");
            println("Seleziona un'opzione:");
            println("[1] Oggetti presenti in una cella");
            println("[2] Informazioni su arma");
            println("[3] Informazioni su potenziamento");
            println("[4] Informazioni su armi/potenziamenti in mano");
            println("[0] Esci");
            print("Selezione: ");
            sel1 = scanInt();
            switch (sel1) {
                case 0:
                    break;
                case 1:
                    int cellN;
                    do {
                        print("Inserisci il numero della cella: ");
                        cellN = scanInt();
                    }while (cellN < 1 || cellN > 12);
                    cellN--;
                    int y = cellN / 4;
                    int x = cellN - (y * 4);
                    if(view.getGame().getMap().getCell(x,y) != null) {
                        Cell cell = view.getGame().getMap().getCell(x,y);
                        if(cell.getRoomNumber() < 3 && cell.hasSpawn(Color.values()[cell.getRoomNumber()])) {
                            print("ARMI: ");
                            SpawnCell sc = (SpawnCell) cell;
                            for(Weapon w: sc.getWeapons())
                                print(w.getName() + " ");
                        }
                        else {
                            RegularCell rc = (RegularCell) cell;
                            for (Color color : rc.getLoot().getContent()) {
                                switch (color) {
                                    case RED:
                                        print("MUNIZIONE " + ANSI_RED + "rossa " + ANSI_RESET);
                                        break;
                                    case YELLOW:
                                        print("MUNIZIONE " + ANSI_YELLOW + "gialla " + ANSI_RESET);
                                        break;
                                    case BLUE:
                                        print("MUNIZIONE " + ANSI_BLUE + "blu " + ANSI_RESET);
                                        break;
                                    case POWER:
                                        print("POTENZIAMENTO (da pescare) ");
                                        break;
                                }
                            }
                            println("");
                        }
                    }
                    else
                        println("Cella non presente nella mappa");
                    break;
                case 2:
                    try{
                        JsonReader reader = new JsonReader(new FileReader("resources/baseGame.json"));
                        Gson gson = new Gson();
                        Game baseGame = gson.fromJson(reader, Game.class);
                        List<Weapon> weaplist = new ArrayList<>();
                        while(weaplist.size() < 21) {
                            weaplist.add(baseGame.getWeaponsDeck().draw());
                        }
                        for(int i = 0; i < weaplist.size() - (weaplist.size() % 2); i += 2) {
                            println(String.format("[%2d] %-23s | [%2d] %-23s", i+1, weaplist.get(i).getName(), i+2, weaplist.get(i+1).getName()));
                        }
                        if(weaplist.size() % 2 == 1) {
                            println(String.format("[%2d] %-23s", weaplist.size(), weaplist.get(weaplist.size()-1).getName()));
                        }
                        int sel2;
                        do {
                            print("Selezione [1-" + weaplist.size() + "]: ");
                            sel2 = scanInt();
                        }while (sel2 < 1 || sel2 > weaplist.size());
                        sel2--;
                        printWeapon(weaplist.get(sel2));
                    }
                    catch(FileNotFoundException ex){
                        println("Error! Error!");
                    }
                    break;
                case 3:
                    try{
                        JsonReader reader = new JsonReader(new FileReader("resources/baseGame.json"));
                        Gson gson = new Gson();
                        Game baseGame = gson.fromJson(reader, Game.class);
                        List<Power> powlist = new ArrayList<>();
                        while(powlist.size() < 4) {
                            powlist.add(baseGame.getPowersDeck().draw());
                        }
                        for(int i = 0; i < powlist.size(); i++) {
                            println("[" + (i + 1) + "] " + powlist.get(i).getName());
                        }
                        int sel2;
                        do {
                            print("Selezione [1-" + powlist.size() + "]: ");
                            sel2 = scanInt();
                        }while (sel2 < 1 || sel2 > powlist.size());
                        sel2--;
                        println(ANSI_BOLD + powlist.get(sel2).getName() + ANSI_RESET + ": " + powlist.get(sel2).getBase().getDescription());
                    }
                    catch(FileNotFoundException ex){
                        println("Error! Error!");
                    }
                    break;
                case 4:
                    if(view.getMyPlayer().getWeapons() != null && !view.getMyPlayer().getWeapons().isEmpty()) {
                        println("Armi in mano: ");
                        for (Weapon w : view.getMyPlayer().getWeapons()) {
                            print(w.getName() + " (");
                            if (w.isLoaded())
                                print("carica");
                            else
                                print("scarica");
                            println(")");
                        }
                    }
                    else
                        println("Nessuna arma in mano");

                    if(view.getMyPlayer().getPowers() != null && !view.getMyPlayer().getPowers().isEmpty()) {
                        println("Potenziamenti in mano: ");
                        for (Power p : view.getMyPlayer().getPowers()) {
                            println(p.getName() + formatColorBox(p.getColor()));
                        }
                    }
                    else
                        println("Nessun potenziamento in mano");
                    break;
                default:
                    println("Scelta non presente in elenco, ripetere");
            }
            println("");
        }
        while (sel1 != 0);
    }

    /**
     * Prints out the Frenzy mode status
     */
    private void frenzyInfo() {
        print("Modalità Frenesia: ");
        if(view.getPhase() == GamePhase.FRENZY)
            print(ANSI_GREEN);
        else
            print(ANSI_RED);
        println(BOX + ANSI_RESET);
    }

    /**
     * Prints out players' general informations (connection, playing player, ammos)
     */
    private void playerInfo() {
        println("Giocatori: ");
        for(Player p: view.getGame().getPlayers()) {
            String print = "";
            String background = "";
            if(p.equals(view.getMyPlayer())) {
                print += ANSI_BOLD;
            }
            if(p.equals(view.getActive())) {
                background = ANSI_CYAN_BACKGROUND;
            }

            print += background + String.format("%-25s", p.getNick() + SPACE + background + fighterToString(p.getCharacter())) + ANSI_RESET;
            print += " Ammo: ";
            print += p.getAmmo(Color.BLUE) + "x" + formatColorBox(Color.BLUE) + " ";
            print += p.getAmmo(Color.YELLOW) + "x" + formatColorBox(Color.YELLOW) + " ";
            print += p.getAmmo(Color.RED) + "x" + formatColorBox(Color.RED) + " ";
            println(print);
        }
    }

    private int generalMenu(List<String> options, int starting) {
        return generalMenu(options, starting, true);
    }

    private int generalMenu(List<String> options, int starting, boolean gameInfo) {
        int choose;
        for(String opt: options)
            println(opt);

        if(gameInfo)
            println("[?] Informazioni sul gioco");

        do {
            print("Qual è la tua scelta? [" + starting + "-" + (options.size() - 1) + "]: ");
            choose = scanInt();
            if(choose == -1 && gameInfo)
                generalInfo();
        }
        while (choose < starting || choose > options.size() - 1);
        return choose;
    }

    /**
     * Prints out a map of the match, with the possibility to highlight cells and/or players
     * @param marked Players to be highlighted
     * @param highlighted Cells to be highlighted
     */
    private void map(List<Player> marked, List<Point> highlighted) {

        println("\n\n");

        for(int y = 0; y < 3; y++) {
            for(int rowN = 0; rowN < 9; rowN++) {
                for(int x = 0; x < 4; x++) {
                    if(view.getGame().getMap().getCell(x,y) != null)
                        print(printCell(x,y,rowN, marked, highlighted));
                    else {
                        print(" " + innerCellFormat("") + " ");
                    }
                }
                println("");
            }
        }
        println("");
    }

    /**
     * Cell display formatter to print out a map cell
     * @param x Cell X coordinate
     * @param y Cell Y coordinate
     * @param row Row number of the cell (between 0 and 8) depending on the row the map is printing out
     * @param marked Players to be highlighted
     * @param highlighted Cells to be highlighted
     * @return Cell part formatted string
     */
    private String printCell(int x, int y, int row, List<Player> marked, List<Point> highlighted) {
        Cell c = view.getGame().getMap().getCell(x,y);
        String ret = "";
        String highlight = "";
        if(highlighted != null) {
            for (Point p : highlighted) {
                if (x == p.getX() && y == p.getY())
                    highlight = ANSI_CYAN_BACKGROUND;
            }
        }
        switch (row) {
            case 0:
                ret = ANSI_COLORS[c.getRoomNumber()] + corner(x,y,true,true);
                switch (c.getSides()[0]) {
                    case DOOR:
                        for(int i = 1; i < (CELLDIM - DOORDIM)/2; i++)
                            ret += HOR;
                        for(int i = 0; i < DOORDIM; i++)
                            ret += SPACE;
                        for(int i = 1; i < (CELLDIM - DOORDIM)/2; i++)
                            ret += HOR;

                        break;
                    case WALL:
                        for(int i = 0; i < CELLDIM - 2; i++)
                            ret += HOR;
                        break;
                    case NOTHING:
                        for(int i = 0; i < CELLDIM - 2; i++)
                            ret += L_HOR;
                        break;
                    default:
                        break;
                }
                ret += corner(x,y,true, false) + ANSI_RESET;
                break;
            case 1:
                if(c.getSides()[3] != Side.NOTHING)
                    ret = ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret = ANSI_COLORS[c.getRoomNumber()] + L_VERT + ANSI_RESET;

                ret += highlight + innerCellFormat(String.format("CELLA %-2d", (x + 4* y + 1))) + ANSI_RESET;

                if(c.getSides()[1] != Side.NOTHING)
                    ret += ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret += SPACE;
                break;
            case 2:
                if(c.getSides()[3] != Side.NOTHING)
                    ret = ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret = ANSI_COLORS[c.getRoomNumber()] + L_VERT + ANSI_RESET;

                String loot = "";
                if(c.getRoomNumber() < 3 && c.hasSpawn(Color.values()[c.getRoomNumber()])) {
                    loot += highlight + ANSI_COLORS[c.getRoomNumber()] + "S" + SPACE;
                }
                else {
                    RegularCell rc = (RegularCell) c;
                    if(rc.getLoot() != null)
                    {
                        for(Color color : rc.getLoot().getContent()) {
                            switch (color) {
                                case BLUE:
                                    loot += highlight + ANSI_BLUE + BOX;
                                    break;
                                case YELLOW:
                                    loot += highlight + ANSI_YELLOW + BOX;
                                    break;
                                case RED:
                                    loot += highlight + ANSI_RED + BOX;
                                    break;
                                case POWER:
                                    loot += highlight + ANSI_BLACK + BOX;
                                    break;
                            }
                            loot += SPACE;
                        }
                    }
                }
                ret += highlight + innerCellFormatRight(loot) + ANSI_RESET;
                if(c.getSides()[1] != Side.NOTHING)
                    ret += ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret += SPACE;
                break;
            case 3:
                switch (c.getSides()[3]) {
                    case WALL:
                        ret = ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                        break;
                    case NOTHING:
                        ret = ANSI_COLORS[c.getRoomNumber()] + L_VERT + ANSI_RESET;
                        break;
                    case DOOR:
                        ret = SPACE;
                        break;

                }
                if(c.getPawns().size() >= 1) {
                    Player p = c.getPawns().get(0);
                    String bgd = "";
                    if(marked != null) {
                        for(Player mark: marked) {
                            if (p.equals(mark)) {
                                bgd += ANSI_RED_BACKGROUND + ANSI_BLACK;
                            }
                        }
                    }
                    ret += highlight + innerCellFormat(bgd + p.getNick() + ANSI_RESET + highlight) + ANSI_RESET;
                }
                else
                    ret += highlight + innerCellFormat("") + ANSI_RESET;

                if(c.getSides()[1] == Side.WALL)
                    ret += ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret += SPACE;
                break;
            case 4:
                switch (c.getSides()[3]) {
                    case WALL:
                        ret = ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                        break;
                    case NOTHING:
                        ret = ANSI_COLORS[c.getRoomNumber()] + L_VERT + ANSI_RESET;
                        break;
                    case DOOR:
                        ret = SPACE;
                        break;

                }
                if(c.getPawns().size() >= 2) {
                    Player p = c.getPawns().get(1);
                    String bgd = "";
                    if(marked != null) {
                        for(Player mark: marked) {
                            if (p.equals(mark)) {
                                bgd += ANSI_RED_BACKGROUND + ANSI_BLACK;
                            }
                        }
                    }
                    ret += highlight + innerCellFormat(bgd + p.getNick() + ANSI_RESET + highlight) + ANSI_RESET;
                }
                else
                    ret += highlight + innerCellFormat("") + ANSI_RESET;

                if(c.getSides()[1] == Side.WALL)
                    ret += ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret += SPACE;
                break;
            case 5:
                switch (c.getSides()[3]) {
                    case WALL:
                        ret = ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                        break;
                    case NOTHING:
                        ret = ANSI_COLORS[c.getRoomNumber()] + L_VERT + ANSI_RESET;
                        break;
                    case DOOR:
                        ret = SPACE;
                        break;

                }
                if(c.getPawns().size() >= 3) {
                    Player p = c.getPawns().get(2);
                    String bgd = "";
                    if(marked != null) {
                        for(Player mark: marked) {
                            if (p.equals(mark)) {
                                bgd += ANSI_RED_BACKGROUND + ANSI_BLACK;
                            }
                        }
                    }
                    ret += highlight + innerCellFormat(bgd + p.getNick() + ANSI_RESET + highlight) + ANSI_RESET;
                }
                else
                    ret += highlight + innerCellFormat("") + ANSI_RESET;

                if(c.getSides()[1] == Side.WALL)
                    ret += ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret += SPACE;
                break;
            case 6:
                if(c.getSides()[3] != Side.NOTHING)
                    ret = ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret = ANSI_COLORS[c.getRoomNumber()] + L_VERT + ANSI_RESET;

                if(c.getPawns().size() >= 4) {
                    Player p = c.getPawns().get(3);
                    String bgd = "";
                    if(marked != null) {
                        for(Player mark: marked) {
                            if (p.equals(mark)) {
                                bgd += ANSI_RED_BACKGROUND + ANSI_BLACK;
                            }
                        }
                    }
                    ret += highlight + innerCellFormat(bgd + p.getNick() + ANSI_RESET + highlight) + ANSI_RESET;
                }
                else
                    ret += highlight + innerCellFormat("") + ANSI_RESET;

                if(c.getSides()[1] != Side.NOTHING)
                    ret += ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret += SPACE;
                break;
            case 7:
                if(c.getSides()[3] != Side.NOTHING)
                    ret = ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret = ANSI_COLORS[c.getRoomNumber()] + L_VERT + ANSI_RESET;

                if(c.getPawns().size() >= 5) {
                    Player p = c.getPawns().get(4);
                    String bgd = "";
                    if(marked != null) {
                        for(Player mark: marked) {
                            if (p.equals(mark)) {
                                bgd += ANSI_RED_BACKGROUND + ANSI_BLACK;
                            }
                        }
                    }
                    ret += highlight + innerCellFormat(bgd + p.getNick() + ANSI_RESET + highlight) + ANSI_RESET;
                }
                else
                    ret += highlight + innerCellFormat("") + ANSI_RESET;

                if(c.getSides()[1] != Side.NOTHING)
                    ret += ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET;
                else
                    ret += SPACE;
                break;
            case 8:
                ret = ANSI_COLORS[c.getRoomNumber()] + corner(x,y,false,true);
                switch (c.getSides()[2]) {
                    case DOOR:
                        for(int i = 1; i < (CELLDIM - DOORDIM)/2; i++)
                            ret += HOR;
                        for(int i = 0; i < DOORDIM; i++)
                            ret += SPACE;
                        for(int i = 1; i < (CELLDIM - DOORDIM)/2; i++)
                            ret += HOR;

                        break;
                    case WALL:
                        for(int i = 0; i < CELLDIM - 2; i++)
                            ret += HOR;
                        break;
                    case NOTHING:
                        for(int i = 0; i < CELLDIM - 2; i++)
                            ret += SPACE;
                        break;
                }
                ret += corner(x,y,false,false) + ANSI_RESET;
                break;

        }
        return ret;
    }

    /**
     * Cell corner formatter
     * @param x Cell x coordinate
     * @param y Cell y coordinate
     * @param north If true considers the northern part of the cell, the south one otherwise
     * @param west If true considers the west part of the cell, the east one otherwise
     * @return Formatted corner string
     */
    private String corner(int x, int y, boolean north, boolean west) {
        Cell center = view.getGame().getMap().getCell(x,y);
        String ret = null;
        if(north) {
            if(west) {
                switch (center.getSides()[0]) {
                    case WALL:
                    case DOOR:
                        if(center.getSides()[3] != Side.NOTHING)
                            ret =  TL_CORNER;
                        else
                            ret =  HOR;
                        break;
                    case NOTHING:
                        if(center.getSides()[3] != Side.NOTHING)
                            ret =  VERT;
                        else
                            ret =  BR_CORNER;
                        break;
                }
            }
            else {
                switch (center.getSides()[0]) {
                    case WALL:
                    case DOOR:
                        if(center.getSides()[1] != Side.NOTHING)
                            ret =  TR_CORNER;
                        else
                            ret =  HOR;
                        break;
                    case NOTHING:
                        if(center.getSides()[1] != Side.NOTHING)
                            ret =  VERT;
                        else
                            ret =  BL_CORNER;
                        break;
                }
            }
        }
        else {
            if(west) {
                switch (center.getSides()[2]) {
                    case WALL:
                    case DOOR:
                        if(center.getSides()[3] != Side.NOTHING)
                            ret =  BL_CORNER;
                        else
                            ret =  HOR;
                        break;
                    case NOTHING:
                        if(center.getSides()[3] != Side.NOTHING)
                            ret =  VERT;
                        else
                            ret =  TR_CORNER;
                        break;
                }
            }

            else {
                switch (center.getSides()[2]) {
                    case WALL:
                    case DOOR:
                        if(center.getSides()[1] != Side.NOTHING)
                            ret =  BR_CORNER;
                        else
                            ret =  HOR;
                        break;
                    case NOTHING:
                        if(center.getSides()[1] != Side.NOTHING)
                            ret =  VERT;
                        else
                            ret =  TL_CORNER;
                        break;
                }
            }
        }
        return ret;
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available, boolean mustChoose) {
        List<String> options = new ArrayList<>();
        options.add("Azioni disponibili:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non fare nulla");
        }
        for(Action disp: available) {
            i++;
            List<Color> cost = disp.getCost();
            String s = "[" + i + "] " + disp.getName() + " (" + disp.getDescription() + "), costo: ";
            for(Color c: cost) {
                s += formatColorBox(c) + " ";
            }
            options.add(s);
        }

        choose = generalMenu(options, starting);

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
        List<String> options = new ArrayList<>();
        options.add("Armi disponibili:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non scegliere nulla");
        }
        for(Weapon disp: available) {
            i++;
            options.add("[" + i + "] " + disp.getName());
        }

        choose = generalMenu(options, starting);

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
        List<String> options = new ArrayList<>();
        options.add("Armi disponibili nella cella:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non scegliere nulla");
        }
        for(Weapon disp: grabbable) {
            i++;
            options.add("[" + i + "] " + disp.getName());
        }
        println("[?] Informazioni sul gioco");

        choose = generalMenu(options, starting);

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
        List<String> options = new ArrayList<>();
        options.add("Armi ricaricabili:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non scegliere nulla");
        }
        for(Weapon disp: reloadable) {
            String s = "";
            i++;
            List<Color> cost = disp.getBase().getCost();
            s = "[" + i + "] " + disp.getName() + ", costo: ";
            for(Color c: cost) {
                s += formatColorBox(c) + " ";
            }
            options.add(s);
        }

        choose = generalMenu(options, starting);

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
        map(null, destinations);
        List<String> options = new ArrayList<>();
        options.add("Movimenti possibili:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non muoverti");
        }
        for(Point disp: destinations) {
            i++;
            options.add("[" + i + "] " + (disp.getY() * 4 + disp.getX() + 1));
        }

        choose = generalMenu(options, starting);

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
        map(targets, null);
        List<String> options = new ArrayList<>();
        options.add("Scegli un bersaglio:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non scegliere nessuno");
        }
        for(Player p: targets) {
            i++;
            options.add("[" + i + "] " + p.getNick());
        }

        choose = generalMenu(options, starting);

        if(choose == 0)
            return null;
        else
            return targets.get(choose - 1);
    }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the enemy will be after being moved
     */
    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose) {
        List<Player> plist = new ArrayList<>();
        List<String> options = new ArrayList<>();
        plist.add(enemy);
        map(plist, destinations);
        options.add("Scegli dove muovere il giocatore:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non muoverti");
        }
        for(Point disp: destinations) {
            i++;
            options.add("[" + i + "] " + (disp.getY() * 4 + disp.getX() + 1));
        }

        choose = generalMenu(options, starting);

        if(choose == 0)
            return null;
        else
            return destinations.get(choose - 1);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) {
        List<String> options = new ArrayList<>();
        options.add("Scegli quale carta potenziamento scartare:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non scartare nessuna carta");
        }
        for(Power pow: powers) {
            i++;
            options.add("[" + i + "] " + pow.getName() + ", costo: " + formatColorBox(pow.getColor()) + " ");
        }

        choose = generalMenu(options, starting);

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
        List<String> options = new ArrayList<>();
        options.add("Scegli una stanza:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Nessuna scelta");
        }
        for(Integer room: rooms) {
            i++;
            String s = "";
            s = "[" + i + "] Stanza ";
            switch (room) {
                case 0:
                    s += ANSI_RED + "ROSSA";
                    break;
                case 1:
                    s += ANSI_BLUE + "BLU";
                    break;
                case 2:
                    s += ANSI_YELLOW + "GIALLA";
                    break;
                case 3:
                    s += ANSI_WHITE + "BIANCA";
                    break;
                case 4:
                    s += ANSI_PURPLE + "VIOLA";
                    break;
                case 5:
                    s += ANSI_GREEN + "VERDE";
            }
            s += ANSI_RESET;
            options.add(s);
        }

        choose = generalMenu(options, starting);

        if(choose == 0)
            return null;
        else
            return choose - 1;
    }

    /**
     * Asks the player to choose a direction
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     */
    public Direction chooseDirection(List<Direction> possible, boolean mustChoose) {
        println("Scegli una direzione:");
        int i = 0;
        int starting = 1;
        String choose;
        if(!mustChoose) {
            starting = 0;
            println("0 - Nessuna scelta");
        }

        List<String> possibleString = new ArrayList<>();
        for(Direction d: possible) {
            switch (d) {
                case NORTH:
                    println("N - Nord");
                    possibleString.add("n");
                    break;
                case EAST:
                    println("E - Est");
                    possibleString.add("e");
                    break;
                case SOUTH:
                    println("S - Sud");
                    possibleString.add("s");
                    break;
                case WEST:
                    println("W - Ovest");
                    possibleString.add("w");
                    break;
                default:
                    return null;
            }
        }

        println("[?] Informazioni sul gioco");

        do {
            print("La tua scelta: ");
            choose = scan();
        }while (!choose.equalsIgnoreCase("0") && !choose.equalsIgnoreCase("?") && !possibleString.contains(choose));

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
        map(null, positions);
        List<String> options = new ArrayList<>();
        options.add("Scegli una cella della mappa:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non scegliere");
        }
        for(Point disp: positions) {
            i++;
            options.add("[" + i + "] " + (disp.getY() * 4 + disp.getX()));
        }

        choose = generalMenu(options, starting);

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
        nick = scan();
        return nick;
    }

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     */
    public String getPhrase() {
        print("La tua esclamazione: ");
        return scan();
    }

    /**
     * Asks the user fot the fighter
     * @param available List of available fighters
     * @return user's fighter
     */
    public Fighter getFighter(List<Fighter> available) {

        List<String> options = new ArrayList<>();
        options.add("Scegli il tuo Fighter");
        int i = 1;
        for(Fighter f: available) {
            switch (f) {
                case DSTRUTTOR3:
                    options.add("[" + i + "] Dstruttor3");
                    break;
                case VIOLETTA:
                    options.add("[" + i + "] Violetta");
                    break;
                case DOZER:
                    options.add("[" + i + "] Dozer");
                    break;
                case SPROG:
                    options.add("[" + i + "] Sprog");
                    break;
                case BANSHEE:
                    options.add("[" + i + "] Banshee");
                    break;
            }
            i++;
        }

        int chosen = generalMenu(options, 1, false);

        return available.get(chosen - 1);
    }

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     */
    public Integer getSkullNum() {
        int num;
        do {
            print("Scegli con quanti teschi vuoi giocare [5-8]: ");
            num = scanInt();
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
        List<String> options = new ArrayList<>();
        options.add("Scegli un'arma da scartare:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non scegliere nulla");
        }
        for(Weapon weapon: inHand) {
            i++;
            String s = "[" + i + "] " + weapon.getName() + " ";
            switch (weapon.getColor()) {
                case BLUE:
                    s += ANSI_BLUE + BOX + ANSI_RESET + " ";
                    break;
                case RED:
                    s += ANSI_RED + BOX + ANSI_RESET + " ";
                    break;
                case YELLOW:
                    s += ANSI_YELLOW + BOX + ANSI_RESET + " ";
                    break;
            }
            options.add(s);
        }

        choose = generalMenu(options, starting);

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
        int map;
        List<String> options = new ArrayList<>();
        options.add("Scegli la mappa da utilizzare:");
        options.add("[1] Ottima per iniziare");
        options.add("[2] Ottima per 3 o 4 giocatori");
        options.add("[3] Ottima per qualsiasi numero di giocatori");
        options.add("[4] Ottima per 4 o 5 giocatori");

        return generalMenu(options, 1, false);
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     */
    public Boolean chooseFrenzy() {
        String ans;
        do {
            print("Vuoi la modalità Frenesia a fine partita? [S/N]: ");
            ans = scan();
        }
        while(!ans.equalsIgnoreCase("s") && !ans.equalsIgnoreCase("n"));
        return ans.toLowerCase().equals("s");
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose) {
        List<String> options = new ArrayList<>();
        options.add("Scegli un potenziamento da usare:");
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add("[0] Non usare nessun potenziamento");
        }
        for(Power pow: inHand) {
            i++;
            options.add("[" + i + "] " + pow.getName() + ", costo: " + formatColorBox(pow.getColor()) + " ");
        }

        choose = generalMenu(options, starting);

        if(choose == 0)
            return null;
        else
            return inHand.get(choose - 1);
    }

    /**
     * Prints out a general message to the client interface
     * @param message Message to be printed
     */
    public void generalMessage(String message) {
        println(message);
    }
}
