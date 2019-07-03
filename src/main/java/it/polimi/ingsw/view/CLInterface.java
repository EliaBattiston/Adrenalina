package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.clientmodel.*;
import it.polimi.ingsw.controller.GamePhase;
import it.polimi.ingsw.model.*;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     * Log messages
     */
    private List<String> log;

    /**
     *Color and symbol constants
     */
    private static final int CELLDIM = 20;
    private static final int DOORDIM = 6;
    private static final int TOPSPACE = 50;
    private static final int WEAPONSNUM = 21;
    private static final int POWERSNUM = 4;

    private static final int CELLROWNUM = 8;
    private static final int DOORHEIGHT = 2;

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

    private static final String BOX = "◼";
    private static final String SKULL = "☠";

    private static final String SPACE = "\u0020";
    private static final String SQUARE_OPEN = "[";
    private static final String SQUARE_CLOSE = "]";

    private static final String TL_CORNER = "╔";
    private static final String TR_CORNER = "╗";
    private static final String BL_CORNER = "╚";
    private static final String BR_CORNER = "╝";
    private static final String VERT = "║";
    private static final String HOR = "═";

    private static final String L_HOR = "-";
    private static final String L_VERT = "|";

    //Other defines
    private static final int DAMAGES_NUMBER = 12;
    private static final String AMMUNITION = "MUNIZIONE ";
    private static final String SELECTION = "Selezione [1-";
    private static final String GAMEINFO = "[?] Informazioni sul gioco";
    private static final String NOCHOOSE = "[0] Non scegliere nulla";
    private static final String COST = ", costo: ";
    private static final String CHOOSE = "Scelta: ";

    /**
     * Strings used inside the CLI
     */
    private static final String EFFETTI = "Effetti:";
    private static final String BASE = "Base";
    private static final String ALTERNATIVO = "Alternativo";
    private static final String ADDIZIONALE = "Addizionale";
    private static final String SELEZIONA_UN_OPZIONE = "Seleziona un'opzione:";
    private static final String OGGETTI_PRESENTI_IN_UNA_CELLA = "[1] Oggetti presenti in una cella";
    private static final String INFORMAZIONI_SU_ARMA = "[2] Informazioni su arma";
    private static final String INFORMAZIONI_SU_POTENZIAMENTO = "[3] Informazioni su potenziamento";
    private static final String INFORMAZIONI_SU_ARMI_POTENZIAMENTI_IN_MANO = "[4] Informazioni su armi/potenziamenti in mano";
    private static final String LOG_COMPLETO_DELLA_PARTITA = "[5] Log completo della partita";
    private static final String ESCI = "[0] Esci";
    private static final String SELEZIONE = "Selezione: ";
    private static final String INSERISCI_IL_NUMERO_DELLA_CELLA = "Inserisci il numero della cella: ";
    private static final String ARMI = "ARMI: ";
    private static final String ROSSA = "rossa ";
    private static final String GIALLA = "gialla ";
    private static final String BLU = "blu ";
    private static final String POTENZIAMENTO_DA_PESCARE = "POTENZIAMENTO (da pescare) ";
    private static final String CELLA_NON_PRESENTE_NELLA_MAPPA = "Cella non presente nella mappa";
    private static final String ARMI_IN_MANO = "Armi in mano: ";
    private static final String CARICA = "carica";
    private static final String SCARICA = "scarica";
    private static final String NESSUNA_ARMA_IN_MANO = "\tNessuna arma in mano";
    private static final String POTENZIAMENTI_IN_MANO = "Potenziamenti in mano: ";
    private static final String NESSUN_POTENZIAMENTO_IN_MANO = "\tNessun potenziamento in mano";
    private static final String SCELTA_NON_PRESENTE_IN_ELENCO_RIPETERE = "Scelta non presente in elenco, ripetere";
    private static final String MODALITÀ_FRENESIA = "\nModalità Frenesia: ";
    private static final String SI = "SI";
    private static final String NO = "NO";
    private static final String GIOCATORI = "\nGiocatori: ";
    private static final String AMMO = " Ammo: ";
    private static final String MORTI = "Morti: ";
    private static final String X = "x";
    private static final String DANNI = "Danni: ";
    private static final String MARCHI = " Marchi: ";
    private static final String TESCHI = "\nTeschi: ";
    private static final String QUAL_È_LA_TUA_SCELTA = "Qual è la tua scelta? [";
    private static final String AZIONI_DISPONIBILI = "\nAzioni disponibili:";
    private static final String NON_FARE_NULLA = "[0] Non fare nulla";
    private static final String ARMI_DISPONIBILI = "\nArmi disponibili:";
    private static final String ARMI_DISPONIBILI_NELLA_CELLA = "\nArmi disponibili nella cella:";
    private static final String ARMI_RICARICABILI = "\nArmi ricaricabili:";
    private static final String MOVIMENTI_POSSIBILI = "\nMovimenti possibili: ";
    private static final String SCEGLI_UN_BERSAGLIO = "\nScegli un bersaglio:";
    private static final String NON_SCEGLIERE_NESSUNO = "[0] Non scegliere nessuno";
    private static final String SCEGLI_DOVE_MUOVERE_IL_GIOCATORE = "\nScegli dove muovere il giocatore:";
    private static final String POSIZIONI_CELLE_POSSIBILI = "Posizioni (celle) possibili: ";
    private static final String SCEGLI_QUALE_CARTA_POTENZIAMENTO_SCARTARE = "\nScegli quale carta potenziamento scartare:";
    private static final String NON_SCARTARE_NESSUNA_CARTA = "[0] Non scartare nessuna carta";
    private static final String SCEGLI_UNA_STANZA = "\nScegli una stanza:";
    private static final String NESSUNA_SCELTA = "[0] Nessuna scelta";
    private static final String BIANCA = "BIANCA";
    private static final String VIOLA = "VIOLA";
    private static final String VERDE = "VERDE";
    private static final String SCEGLI_UNA_DIREZIONE = "\nScegli una direzione:";
    private static final String NESSUNA_SCELTA1 = "0 - Nessuna scelta";
    private static final String N_NORD = "N - Nord";
    private static final String E_EST = "E - Est";
    private static final String S_SUD = "S - Sud";
    private static final String W_OVEST = "W - Ovest";
    private static final String LA_TUA_SCELTA = "La tua scelta: ";
    private static final String SCEGLI_UNA_CELLA_DELLA_MAPPA = "\nScegli una cella della mappa:";
    private static final String CELLE_DISPONIBILI = "\nCelle disponibili:";
    private static final String IL_TUO_NICKNAME = "\nIl tuo nickname: ";
    private static final String LA_TUA_ESCLAMAZIONE = "\nLa tua esclamazione: ";
    private static final String SCEGLI_IL_TUO_FIGHTER = "\nScegli il tuo Fighter";
    private static final String SCEGLI_CON_QUANTI_TESCHI_VUOI_GIOCARE_5_8 = "\nScegli con quanti teschi vuoi giocare [5-8]: ";
    private static final String SCEGLI_UN_ARMA_DA_SCARTARE = "\nScegli un'arma da scartare:";
    private static final String SCEGLI_LA_MAPPA_DA_UTILIZZARE = "\nScegli la mappa da utilizzare:";
    private static final String OTTIMA_PER_INIZIARE = "[1] Ottima per iniziare";
    private static final String OTTIMA_PER_3_O_4_GIOCATORI = "[2] Ottima per 3 o 4 giocatori";
    private static final String OTTIMA_PER_QUALSIASI_NUMERO_DI_GIOCATORI = "[3] Ottima per qualsiasi numero di giocatori";
    private static final String OTTIMA_PER_4_O_5_GIOCATORI = "[4] Ottima per 4 o 5 giocatori";
    private static final String VUOI_LA_MODALITÀ_FRENESIA_A_FINE_PARTITA_S_N = "\nVuoi la modalità Frenesia a fine partita? [S/N]: ";
    private static final String SCEGLI_UN_POTENZIAMENTO_DA_USARE = "\nScegli un potenziamento da usare:";
    private static final String NON_USARE_NESSUN_POTENZIAMENTO = "[0] Non usare nessun potenziamento";
    private static final String SCEGLI_QUALE_MUNIZIONE_USARE = "\nScegli quale munizione usare:";
    private static final String NESSUNO = "[0] Nessuno";
    private static final String CONNESSIONE_CON_S_OCKET_O_CON_R_MI = "Connessione con [S]ocket o con [R]mi? ";
    private static final String INDIRIZZO_IP_DEL_SERVER = "Indirizzo IP del server: ";
    private static final String IP_IN_FORMATO_NON_CORRETTO_REINSERISCILO = "IP in formato non corretto, reinseriscilo";
    private static final String LOG_ULTIMI_MESSAGGI = "--------------Log (ultimi messaggi)--------------";
    private static final String SELEZIONA_L_IP_DELLA_MACCHINA = "Seleziona l'IP della macchina";
    private static final String LA_PARTITÀ_È_TERMINATA = "----------------- La partità è terminata --------------------\n";
    private static final String CLASSIFICA = "                         Classifica";
    private static final String ALLA_PROSSIMA = "\n\nAlla prossima!";

    /**
     * Path to the base game file
     */
    private static final String BASE_GAME_FILE_PATH = "baseGame.json";

    /**
     * Initialization of the interface, in particular instantiation of scanne and writer over System in and out
     */
    public CLInterface() {
        in = new Scanner(System.in);
        stdout = new PrintWriter(System.out);
        log = new ArrayList<>();

        println("    ___       __                      ___                ___    __  _______  _____");
        println("   /   | ____/ /_______  ____  ____ _/ (_)___  ____ _   /   |  /  |/  / __ \\/ ___/");
        println("  / /| |/ __  / ___/ _ \\/ __ \\/ __ `/ / / __ \\/ __ `/  / /| | / /|_/ / / / / __ \\ ");
        println(" / ___ / /_/ / /  /  __/ / / / /_/ / / / / / / /_/ /  / ___ |/ /  / / /_/ / /_/ / ");
        println("/_/  |_\\__,_/_/   \\___/_/ /_/\\__,_/_/_/_/ /_/\\__,_/  /_/  |_/_/  /_/\\____/\\____/  ");
        println("");
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

        StringBuilder ret = new StringBuilder(toPrint);
        ret.append(SPACE.repeat(CELLDIM - 2 - copy.length()));
        return ret.toString();
    }

    /**
     *General text formatting (spacing insertion on the right of the text)
     * @param toPrint Text to be formatted
     * @param totChar Total number of characters to display
     * @return Formatted text
     */
    private String stringFormat(String toPrint, int totChar) {
        String copy = toPrint.replace(ANSI_RESET, "");
        for(String s: ANSI_COLORS)
            copy = copy.replace(s, "");
        for(String s: ANSI_BACKGROUNDS)
            copy = copy.replace(s, "");

        StringBuilder ret = new StringBuilder(toPrint);
        ret.append(SPACE.repeat(totChar - copy.length()));
        return ret.toString();
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

        StringBuilder ret = new StringBuilder();
        ret.append(SPACE.repeat(CELLDIM - 2 - copy.length()));
        ret.append(toPrint);
        return ret.toString();
    }

    /**
     * Reads a string from System.in
     * @return Read string
     */
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

    /**
     * Reads an integer from System.in
     * @return Read integer
     */
    private int scanInt() {
        int num = 0;
        try {
            num = in.nextInt();
            if(num == -1)
                num = -10;
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
    protected void println(String toBePrinted) {
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

    /**
     * Formats a string to represent the name and colour of a Fighter
     * @param fighter Fighter to be formatted
     * @return Formatted Fighter string
     */
    private String fighterToString(Fighter fighter) {
        String res;
        switch (fighter) {
            case BANSHEE:
                res = ANSI_BLUE;
                break;
            case SPROG:
                res = ANSI_GREEN;
                break;
            case DOZER:
                res = ANSI_BLACK;
                break;
            case VIOLETTA:
                res = ANSI_PURPLE;
                break;
            case DSTRUTTOR3:
                res = ANSI_YELLOW;
                break;
            default:
                res = ANSI_RESET;
        }
        res += fighter.name().substring(0, 1) + fighter.name().substring(1).toLowerCase() + ANSI_RESET;

        return res;
    }

    /**
     * Formats a letter to represent the name and colour of a Fighter
     * @param fighter Fighter to be formatted
     * @return Formatted Fighter letter
     */
    private String fighterToLetter(Fighter fighter) {
        String res;
        switch (fighter) {
            case BANSHEE:
                res = ANSI_BLUE + fighter.name().substring(0, 1);
                break;
            case SPROG:
                res = ANSI_GREEN + fighter.name().substring(0, 1);
                break;
            case DOZER:
                res = ANSI_BLACK + fighter.name().substring(0, 1);
                break;
            case VIOLETTA:
                res = ANSI_PURPLE + fighter.name().substring(0, 1);
                break;
            case DSTRUTTOR3:
                res = ANSI_YELLOW + "3";
                break;
            default:
                res = ANSI_RESET;
        }
        res +=  ANSI_RESET;
        return res;
    }

    /**
     * Prints weapon action informations in a well-presented format
     * @param weapon Weapon to print out info
     */
    private void printWeapon(Weapon weapon) {
        println("");
        print(ANSI_BOLD + weapon.getName() + ANSI_RESET + SPACE + formatColorBox(weapon.getColor()) + SPACE);
        for(Color c: weapon.getBase().getCost()) {
            print(formatColorBox(c) + SPACE);
        }
        println("");
        println(EFFETTI);

        String formatter = "%13s: %s";
        String costString = ", costo";

        println(String.format(formatter, BASE, weapon.getBase().getDescription()));
        if(weapon.getAlternative() != null) {
            StringBuilder cost = new StringBuilder();
            for(Color c: weapon.getAlternative().getCost()) {
                cost.append(SPACE + formatColorBox(c));
            }
            println(String.format(formatter, ALTERNATIVO, "(" + weapon.getAlternative().getName() + costString + cost.toString() + ") " + weapon.getAlternative().getDescription()));
        }
        if(weapon.getAdditional() != null) {
            StringBuilder cost = new StringBuilder();
            for(Color c: weapon.getAdditional().get(0).getCost()) {
                cost.append(SPACE + formatColorBox(c));
            }
            println(String.format(formatter, ADDIZIONALE, "(" + weapon.getAdditional().get(0).getName() + costString + cost.toString() + ") " + weapon.getAdditional().get(0).getDescription()));
            if(weapon.getAdditional().size() == 2) {
                for(Color c: weapon.getAdditional().get(1).getCost()) {
                    cost.append(SPACE + formatColorBox(c));
                }
                println(String.format(formatter, ADDIZIONALE, "(" + weapon.getAdditional().get(1).getName() + costString + cost.toString() + ") " + weapon.getAdditional().get(1).getDescription()));
            }
        }
    }

    /**
     * Update the actual gameView to the client
     * @param matchView current game view
     */
    public void updateGame(MatchView matchView) {
        view = matchView;
        for(int i = 0; i < TOPSPACE; i++)
            println("");
        map(null, null);
        logInfo();
        skullsInfo();
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
            println(SELEZIONA_UN_OPZIONE);
            println(OGGETTI_PRESENTI_IN_UNA_CELLA);
            println(INFORMAZIONI_SU_ARMA);
            println(INFORMAZIONI_SU_POTENZIAMENTO);
            println(INFORMAZIONI_SU_ARMI_POTENZIAMENTI_IN_MANO);
            println(LOG_COMPLETO_DELLA_PARTITA);
            println(ESCI);
            print(SELEZIONE);
            sel1 = scanInt();
            switch (sel1) {
                case 0:
                    break;
                case 1:
                    int cellN;
                    do {
                        print(INSERISCI_IL_NUMERO_DELLA_CELLA);
                        cellN = scanInt();
                    }while (cellN < 1 || cellN > 12);
                    cellN--;
                    int y = cellN / 4;
                    int x = cellN - (y * 4);
                    if(view.getGame().getMap().getCell(x,y) != null) {
                        CellView cell = view.getGame().getMap().getCell(x,y);
                        if(cell.getRoomNumber() < 3 && cell.hasSpawn(Color.values()[cell.getRoomNumber()])) {
                            print(ARMI);
                            SpawnCellView sc = (SpawnCellView) cell;
                            for(Weapon w: sc.getWeapons())
                                print(w.getName() + SPACE);
                        }
                        else {
                            RegularCellView rc = (RegularCellView) cell;
                            for (Color color : rc.getLoot().getContent()) {
                                switch (color) {
                                    case RED:
                                        print(AMMUNITION + ANSI_RED + ROSSA + ANSI_RESET);
                                        break;
                                    case YELLOW:
                                        print(AMMUNITION + ANSI_YELLOW + GIALLA + ANSI_RESET);
                                        break;
                                    case BLUE:
                                        print(AMMUNITION + ANSI_BLUE + BLU + ANSI_RESET);
                                        break;
                                    case POWER:
                                        print(POTENZIAMENTO_DA_PESCARE);
                                        break;
                                }
                            }
                            println("");
                        }
                    }
                    else
                        println(CELLA_NON_PRESENTE_NELLA_MAPPA);
                    break;
                case 2:
                    JsonReader reader = new JsonReader(new InputStreamReader(Game.class.getClassLoader().getResourceAsStream(BASE_GAME_FILE_PATH)));
                    Gson gson = new Gson();
                    Game baseGame = gson.fromJson(reader, Game.class);
                    List<Weapon> weaplist = new ArrayList<>();
                    while(weaplist.size() < WEAPONSNUM) {
                        weaplist.add(baseGame.getWeaponsDeck().draw());
                    }

                    Collections.sort(weaplist, (Weapon o1, Weapon o2)->o1.getName().compareToIgnoreCase(o2.getName()));

                    for(int i = 0; i < weaplist.size() - (weaplist.size() % 2); i += 2) {
                        println(String.format("[%2d] %-23s [%2d] %-23s", i+1, weaplist.get(i).getName(), i+2, weaplist.get(i+1).getName()));
                    }
                    if(weaplist.size() % 2 == 1) {
                        println(String.format("[%2d] %-23s", weaplist.size(), weaplist.get(weaplist.size()-1).getName()));
                    }
                    int sel2;
                    do {
                        print(SELECTION + weaplist.size() + "]: ");
                        sel2 = scanInt();
                    }while (sel2 < 1 || sel2 > weaplist.size());
                    sel2--;
                    printWeapon(weaplist.get(sel2));
                    break;
                case 3:
                    JsonReader readerb = new JsonReader(new InputStreamReader(Game.class.getClassLoader().getResourceAsStream(BASE_GAME_FILE_PATH)));
                    Gson gsonb = new Gson();
                    Game baseGameb = gsonb.fromJson(readerb, Game.class);
                    List<Power> powlist = new ArrayList<>();
                    while(powlist.size() < POWERSNUM) {
                        powlist.add(baseGameb.getPowersDeck().draw());
                    }
                    for(int i = 0; i < powlist.size(); i++) {
                        println(SQUARE_OPEN + (i + 1) + SQUARE_CLOSE + powlist.get(i).getName());
                    }
                    int sel3;
                    do {
                        print(SELECTION + powlist.size() + "]: ");
                        sel3 = scanInt();
                    }while (sel3 < 1 || sel3 > powlist.size());
                    sel3--;
                    println(ANSI_BOLD + powlist.get(sel3).getName() + ANSI_RESET + ": " + powlist.get(sel3).getBase().getDescription());
                    break;
                case 4:
                    if(view.getMyPlayer().getWeapons() != null && !view.getMyPlayer().getWeapons().isEmpty()) {
                        println(ARMI_IN_MANO);
                        for (Weapon w : view.getMyPlayer().getWeapons()) {
                            print("\t" + w.getName() + " (");
                            if (w.isLoaded())
                                print(CARICA);
                            else
                                print(SCARICA);
                            println(")");
                        }
                    }
                    else
                        println(NESSUNA_ARMA_IN_MANO);

                    if(view.getMyPlayer().getPowers() != null && !view.getMyPlayer().getPowers().isEmpty()) {
                        println(POTENZIAMENTI_IN_MANO);
                        for (Power p : view.getMyPlayer().getPowers()) {
                            println("\t" + p.getName() + SPACE + formatColorBox(p.getColor()));
                        }
                    }
                    else
                        println(NESSUN_POTENZIAMENTO_IN_MANO);
                    break;
                case 5:
                    for(String s: log)
                        println(s);
                    break;
                default:
                    println(SCELTA_NON_PRESENTE_IN_ELENCO_RIPETERE);
            }
            println("");
        }
        while (sel1 != 0);
    }

    /**
     * Prints out the Frenzy mode status
     */
    private void frenzyInfo() {
        print(MODALITÀ_FRENESIA);
        if(view.getPhase() == GamePhase.FRENZY)
            print(ANSI_GREEN + SI);
        else
            print(ANSI_RED + NO);
        println(ANSI_RESET);
    }

    /**
     * Prints out players' general informations (connection, playing player, ammos)
     */
    private void playerInfo() {
        println(GIOCATORI);
        for(PlayerView p: view.getGame().getPlayers()) {
            String print = "";
            String background = "";
            if(p.getNick().equals(view.getMyPlayer().getNick())) {
                print += ANSI_BOLD;
            }
            if(view.getActive() != null && p.getNick().equals(view.getActive().getNick())) {
                background = ANSI_CYAN_BACKGROUND;
            }


            print += background + stringFormat(p.getNick() + " - " + background + fighterToString(p.getCharacter()), 32) + ANSI_RESET;
            print += MORTI + p.getSkulls() + X + SKULL + SPACE;
            print += AMMO;
            print += p.getAmmo(Color.BLUE) + X + formatColorBox(Color.BLUE) + SPACE;
            print += p.getAmmo(Color.YELLOW) + X + formatColorBox(Color.YELLOW) + SPACE;
            print += p.getAmmo(Color.RED) + X + formatColorBox(Color.RED) + SPACE;
            print += DANNI;
            for(int i = 0; i < DAMAGES_NUMBER; i++) {
                boolean found = false;
                for(PlayerView pl: view.getGame().getPlayers()) {
                    if (p.getDamage(i) != null && p.getDamage(i).equals(pl.getNick())) {
                        print += fighterToLetter(pl.getCharacter());
                        found = true;
                    }
                }
                if(!found)
                    print += "-";
            }
            print += MARCHI;
            if(p.getReceivedMarks().isEmpty())
                print += "-";
            else {
                for (String mark : p.getReceivedMarks()) {
                    for (PlayerView pl : view.getGame().getPlayers())
                        if (mark.equals(pl.getNick()))
                            print += fighterToLetter(pl.getCharacter());
                }
            }

            println(print);
        }
    }

    /**
     * Prints out infos about the Skulls board
     */
    public void skullsInfo() {
        print(TESCHI);
        for(KillView k: view.getGame().getSkullsBoard()) {
            if(k.isUsed()) {
                if(k.getSkull())
                    print(SKULL + SPACE);
                else {
                    print(fighterToLetter(k.getKiller().getCharacter()));
                    if(k.getOverkill())
                        print("(" + fighterToLetter(k.getKiller().getCharacter()) + ")");
                    print(SPACE);
                }
            }
            else
                print("- ");
        }
    }

    /**
     * Overload of the generalMenu method, with gameInfo parameter fixed to true
     * @param options List of options and strings to be printed
     * @param starting Starting value of the menu
     * @return Menu index choose
     */
    private int generalMenu(List<String> options, int starting) {
        return generalMenu(options, starting, true);
    }

    /**
     * General method to print out a choosing menu and to check out the user's answer before ending
     * @param options List of options and strings to be printed
     * @param starting Starting value of the menu
     * @param gameInfo If true enables the Game info submenu
     * @return Menu index choose
     */
    private int generalMenu(List<String> options, int starting, boolean gameInfo) {
        int choose;

        do {
            println("");
            for (String opt : options)
                println(opt);

            if (gameInfo)
                println(GAMEINFO);

            int end;
            if(starting == 0)
                end = options.size() - 2;
            else
                end = options.size() - 1;

            do {
                print(QUAL_È_LA_TUA_SCELTA + starting + "-" + end + "]: ");
                choose = scanInt();
                if (choose == -1 && gameInfo)
                    generalInfo();
            }
            while (choose != -1 && (choose < starting || choose > end));
        }
        while(choose == -1);

        return choose;
    }

    /**
     * Prints out a map of the match, with the possibility to highlight cells and/or players
     * @param marked Players to be highlighted
     * @param highlighted Cells to be highlighted
     */
    private void map(List<PlayerView> marked, List<Point> highlighted) {

        println("\n\n");

        for(int y = 0; y < 3; y++) {
            for(int rowN = 0; rowN < 9; rowN++) {
                for(int x = 0; x < 4; x++) {
                    if(view.getGame().getMap().getCell(x,y) != null)
                        print(printCell(x,y,rowN, marked, highlighted));
                    else {
                        print(SPACE + innerCellFormat("") + SPACE);
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
    private String printCell(int x, int y, int row, List<PlayerView> marked, List<Point> highlighted) {
        CellView c = view.getGame().getMap().getCell(x,y);
        StringBuilder ret = new StringBuilder();
        String highlight = "";
        if(highlighted != null) {
            for (Point p : highlighted) {
                if (x == p.getX() && y == p.getY())
                    highlight = ANSI_CYAN_BACKGROUND;
            }
        }

        if(row == 0) {
            ret.append(ANSI_COLORS[c.getRoomNumber()] + corner(x,y,true,true));
            switch (c.getSide(Direction.NORTH)) {
                case DOOR:
                    ret.append(HOR.repeat((CELLDIM - DOORDIM)/2 - 1));
                    ret.append(SPACE.repeat(DOORDIM));
                    ret.append(HOR.repeat((CELLDIM - DOORDIM)/2 - 1));
                    break;
                case WALL:
                    ret.append(HOR.repeat(CELLDIM - 2));
                    break;
                case NOTHING:
                    ret.append(L_HOR.repeat(CELLDIM - 2));
                    break;
                default:
                    break;
            }
            ret.append(corner(x,y,true, false) + ANSI_RESET);
        }
        else if(row == 1) {
            if(c.getSide(Direction.WEST) != Side.NOTHING)
                ret.append(ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET);
            else
                ret.append(ANSI_COLORS[c.getRoomNumber()] + L_VERT + ANSI_RESET);

            ret.append(highlight + innerCellFormat(String.format("CELLA %-2d", (x + 4* y + 1))) + ANSI_RESET);

            if(c.getSide(Direction.EAST) != Side.NOTHING)
                ret.append(ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET);
            else
                ret.append(SPACE);
        }
        else if(row >= 2 && row < CELLROWNUM) {
            if (c.getSide(Direction.WEST) == Side.NOTHING)
                ret.append(ANSI_COLORS[c.getRoomNumber()] + L_VERT + ANSI_RESET);
            else {
                if (c.getSide(Direction.WEST) == Side.DOOR && row > DOORHEIGHT && row < CELLROWNUM - DOORHEIGHT) {
                    ret.append(SPACE);
                } else
                    ret.append(ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET);
            }

            if (row == 2) {
                String loot = "";
                if(c.getRoomNumber() < 3 && c.hasSpawn(Color.values()[c.getRoomNumber()])) {
                    loot += highlight + ANSI_COLORS[c.getRoomNumber()] + "S" + SPACE;
                }
                else {
                    RegularCellView rc = (RegularCellView) c;
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
                ret.append(highlight + innerCellFormatRight(loot) + ANSI_RESET);
            }
            else {
                if (c.getPawns().size() >= row - 2) {
                    PlayerView p = c.getPawns().get(row - 3);
                    String bgd = "";
                    if (marked != null) {
                        for (PlayerView mark : marked) {
                            if (p.getNick().equals(mark.getNick())) {
                                bgd += ANSI_RED_BACKGROUND + ANSI_BLACK;
                            }
                        }
                    }
                    ret.append(highlight + innerCellFormat(bgd + p.getNick().substring(0, Math.min(p.getNick().length(), CELLDIM - 2)) + ANSI_RESET + highlight) + ANSI_RESET);
                }
                else
                    ret.append(highlight + innerCellFormat("") + ANSI_RESET);
            }

            switch (c.getSide(Direction.EAST)) {
                case WALL:
                    ret.append(ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET);
                    break;
                case NOTHING:
                    ret.append(SPACE);
                    break;
                case DOOR:
                    if (row > DOORHEIGHT && row < CELLROWNUM - DOORHEIGHT) {
                        ret.append(SPACE);
                    } else
                        ret.append(ANSI_COLORS[c.getRoomNumber()] + VERT + ANSI_RESET);
                    break;
            }
        }
        else if(row == CELLROWNUM) {
            ret.append(ANSI_COLORS[c.getRoomNumber()] + corner(x,y,false,true));
            switch (c.getSide(Direction.SOUTH)) {
                case DOOR:
                    ret.append(HOR.repeat((CELLDIM - DOORDIM)/2 - 1));
                    ret.append(SPACE.repeat(DOORDIM));
                    ret.append(HOR.repeat((CELLDIM - DOORDIM)/2 - 1));
                    break;
                case WALL:
                    ret.append(HOR.repeat(CELLDIM - 2));
                    break;
                case NOTHING:
                    ret.append(SPACE.repeat(CELLDIM - 2));
                    break;
            }
            ret.append(corner(x,y,false,false) + ANSI_RESET);
        }

        return ret.toString();
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
        CellView center = view.getGame().getMap().getCell(x,y);
        String ret = null;
        if(north) {
            if(west) {
                switch (center.getSide(Direction.NORTH)) {
                    case WALL:
                    case DOOR:
                        if(center.getSide(Direction.WEST) != Side.NOTHING)
                            ret =  TL_CORNER;
                        else
                            ret =  HOR;
                        break;
                    case NOTHING:
                        if(center.getSide(Direction.WEST) != Side.NOTHING)
                            ret =  VERT;
                        else
                            ret =  BR_CORNER;
                        break;
                }
            }
            else {
                switch (center.getSide(Direction.NORTH)) {
                    case WALL:
                    case DOOR:
                        if(center.getSide(Direction.EAST) != Side.NOTHING)
                            ret =  TR_CORNER;
                        else
                            ret =  HOR;
                        break;
                    case NOTHING:
                        if(center.getSide(Direction.EAST) != Side.NOTHING)
                            ret =  VERT;
                        else
                            ret =  BL_CORNER;
                        break;
                }
            }
        }
        else {
            if(west) {
                switch (center.getSide(Direction.SOUTH)) {
                    case WALL:
                    case DOOR:
                        if(center.getSide(Direction.WEST) != Side.NOTHING)
                            ret =  BL_CORNER;
                        else
                            ret =  HOR;
                        break;
                    case NOTHING:
                        if(center.getSide(Direction.WEST) != Side.NOTHING)
                            ret =  VERT;
                        else
                            ret =  TR_CORNER;
                        break;
                }
            }

            else {
                switch (center.getSide(Direction.SOUTH)) {
                    case WALL:
                    case DOOR:
                        if(center.getSide(Direction.EAST) != Side.NOTHING)
                            ret =  BR_CORNER;
                        else
                            ret =  HOR;
                        break;
                    case NOTHING:
                        if(center.getSide(Direction.EAST) != Side.NOTHING)
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
        options.add(AZIONI_DISPONIBILI);
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add(NON_FARE_NULLA);
        }
        for(Action disp: available) {
            i++;
            List<Color> cost = disp.getCost();
            String s = SQUARE_OPEN + i + SQUARE_CLOSE + disp.getName() + " (" + disp.getDescription() + ")" + ( disp.getLambdaID().contains("a-")? "" : COST ) ;
            for(Color c: cost) {
                s += formatColorBox(c) + SPACE;
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
        options.add(ARMI_DISPONIBILI);
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add(NOCHOOSE);
        }
        for(Weapon disp: available) {
            i++;
            options.add(SQUARE_OPEN + i + SQUARE_CLOSE + disp.getName());
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
        options.add(ARMI_DISPONIBILI_NELLA_CELLA);
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add(NOCHOOSE);
        }
        for(Weapon disp: grabbable) {
            i++;
            options.add(SQUARE_OPEN + i + SQUARE_CLOSE + disp.getName());
        }
        println(GAMEINFO);

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
        options.add(ARMI_RICARICABILI);
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add(NOCHOOSE);
        }
        for(Weapon disp: reloadable) {
            String s = "";
            i++;
            List<Color> cost = disp.getBase().getCost();
            s = SQUARE_OPEN + i + SQUARE_CLOSE + disp.getName() + COST;
            for(Color c: cost) {
                s += formatColorBox(c) + SPACE;
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
        List<Integer> options = new ArrayList<>();
        int choose;
        int actPos = -1;
        if(!mustChoose)
            destinations.add(view.getActive().getPosition());
        for(Point disp: destinations) {
            int point = disp.getY() * 4 + disp.getX() + 1;
            options.add(point);
        }
        Collections.sort(options);

        map(null, destinations);

        print(MOVIMENTI_POSSIBILI);
        for(Integer point: options) {
            print(point + SPACE);
        }
        println("");
        do {
            print(CHOOSE);
            choose = scanInt();
        }
        while (!options.contains(choose));

        if(choose == actPos)
            return null;
        else {
            choose--;
            Point eqP = new Point(choose % 4, choose / 4);
            Point retP = null;
            for(Point p: destinations) {
                if(p.getX() == eqP.getX() && p.getY() == eqP.getY())
                    retP = p;
            }
            return retP;
        }
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen target
     */
    public PlayerView chooseTarget(List<PlayerView> targets, boolean mustChoose) {
        map(targets, null);
        List<String> options = new ArrayList<>();
        options.add(SCEGLI_UN_BERSAGLIO);
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add(NON_SCEGLIERE_NESSUNO);
        }
        for(PlayerView p: targets) {
            i++;
            options.add(SQUARE_OPEN + i + SQUARE_CLOSE + p.getNick());
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
    public Point moveEnemy(PlayerView enemy, List<Point> destinations, boolean mustChoose) {
        List<PlayerView> plist = new ArrayList<>();
        List<Integer> options = new ArrayList<>();
        plist.add(enemy);

        int choose;
        int actPos = -1;
        if(!mustChoose)
            destinations.add(view.getActive().getPosition());
        for(Point disp: destinations) {
            int point = disp.getY() * 4 + disp.getX() + 1;
            options.add(point);
        }
        Collections.sort(options);

        println(SCEGLI_DOVE_MUOVERE_IL_GIOCATORE);

        map(plist, destinations);

        print(POSIZIONI_CELLE_POSSIBILI);
        for(Integer point: options) {
            print(point + SPACE);
        }



        println("");
        do {
            print(CHOOSE);
            choose = scanInt();
        }
        while (!options.contains(choose));

        if(choose == actPos)
            return null;
        else {
            choose--;
            Point eqP = new Point(choose % 4, choose / 4);
            Point retP = null;
            for(Point p: destinations) {
                if(p.getX() == eqP.getX() && p.getY() == eqP.getY())
                    retP = p;
            }
            return retP;
        }
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) {
        List<String> options = new ArrayList<>();
        options.add(SCEGLI_QUALE_CARTA_POTENZIAMENTO_SCARTARE);
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add(NON_SCARTARE_NESSUNA_CARTA);
        }
        for(Power pow: powers) {
            i++;
            options.add(SQUARE_OPEN + i + SQUARE_CLOSE + pow.getName() + ", colore: " + formatColorBox(pow.getColor()) + SPACE);
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
        options.add(SCEGLI_UNA_STANZA);
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add(NESSUNA_SCELTA);
        }
        for(Integer room: rooms) {
            i++;
            String s = "";
            s = SQUARE_OPEN + i + "] Stanza ";
            switch (room) {
                case 0:
                    s += ANSI_RED + ROSSA.toUpperCase();
                    break;
                case 1:
                    s += ANSI_BLUE + BLU.toUpperCase();
                    break;
                case 2:
                    s += ANSI_YELLOW + GIALLA.toUpperCase();
                    break;
                case 3:
                    s += ANSI_WHITE + BIANCA;
                    break;
                case 4:
                    s += ANSI_PURPLE + VIOLA;
                    break;
                case 5:
                    s += ANSI_GREEN + VERDE;
                    break;
                default:
                    break;
            }
            s += ANSI_RESET;
            options.add(s);
        }

        choose = generalMenu(options, starting);

        if(choose == 0)
            return null;
        else
            return rooms.get(choose - 1);
    }

    /**
     * Asks the player to choose a direction
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     */
    public Direction chooseDirection(List<Direction> possible, boolean mustChoose) {
        println(SCEGLI_UNA_DIREZIONE);

        String choose;
        if(!mustChoose) {
            println(NESSUNA_SCELTA1);
        }

        List<String> possibleString = new ArrayList<>();
        for(Direction d: possible) {
            switch (d) {
                case NORTH:
                    println(N_NORD);
                    possibleString.add("n");
                    break;
                case EAST:
                    println(E_EST);
                    possibleString.add("e");
                    break;
                case SOUTH:
                    println(S_SUD);
                    possibleString.add("s");
                    break;
                case WEST:
                    println(W_OVEST);
                    possibleString.add("w");
                    break;
                default:
                    return null;
            }
        }

        println(GAMEINFO);

        do {
            print(LA_TUA_SCELTA);
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
        List<Integer> options = new ArrayList<>();
        int choose;
        int actPos = -1;
        if(!mustChoose)
            positions.add(view.getActive().getPosition());
        for(Point disp: positions) {
            int point = disp.getY() * 4 + disp.getX() + 1;
            options.add(point);
        }
        Collections.sort(options);

        println(SCEGLI_UNA_CELLA_DELLA_MAPPA);

        map(null, positions);

        print(CELLE_DISPONIBILI);
        for(Integer point: options) {
            print(point + SPACE);
        }
        println("");
        do {
            print(CHOOSE);
            choose = scanInt();
        }
        while (!options.contains(choose));

        if(choose == actPos)
            return null;
        else {
            choose--;
            Point eqP = new Point(choose % 4, choose / 4);
            Point retP = null;
            for(Point p: positions) {
                if(p.getX() == eqP.getX() && p.getY() == eqP.getY())
                    retP = p;
            }
            return retP;
        }
    }

    /**
     * Asks the user for the nickname
     * @return user's nickname
     */
    public String getNickname() {
        String nick;
        print(IL_TUO_NICKNAME);
        nick = scan();
        return nick;
    }

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     */
    public String getPhrase() {
        print(LA_TUA_ESCLAMAZIONE);
        return scan();
    }

    /**
     * Asks the user fot the fighter
     * @param available List of available fighters
     * @return user's fighter
     */
    public Fighter getFighter(List<Fighter> available) {

        List<String> options = new ArrayList<>();
        options.add(SCEGLI_IL_TUO_FIGHTER);
        int i = 1;
        for(Fighter f: available) {
            switch (f) {
                case DSTRUTTOR3:
                    options.add(SQUARE_OPEN + i + "] Dstruttor3");
                    break;
                case VIOLETTA:
                    options.add(SQUARE_OPEN + i + "] Violetta");
                    break;
                case DOZER:
                    options.add(SQUARE_OPEN + i + "] Dozer");
                    break;
                case SPROG:
                    options.add(SQUARE_OPEN + i + "] Sprog");
                    break;
                case BANSHEE:
                    options.add(SQUARE_OPEN + i + "] Banshee");
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
            print(SCEGLI_CON_QUANTI_TESCHI_VUOI_GIOCARE_5_8);
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
        options.add(SCEGLI_UN_ARMA_DA_SCARTARE);
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add(NOCHOOSE);
        }
        for(Weapon weapon: inHand) {
            i++;
            String s = SQUARE_OPEN + i + SQUARE_CLOSE + weapon.getName() + SPACE;
            switch (weapon.getColor()) {
                case BLUE:
                    s += ANSI_BLUE + BOX + ANSI_RESET + SPACE;
                    break;
                case RED:
                    s += ANSI_RED + BOX + ANSI_RESET + SPACE;
                    break;
                case YELLOW:
                    s += ANSI_YELLOW + BOX + ANSI_RESET + SPACE;
                    break;
                default:
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
        List<String> options = new ArrayList<>();
        options.add(SCEGLI_LA_MAPPA_DA_UTILIZZARE);
        options.add(OTTIMA_PER_INIZIARE);
        options.add(OTTIMA_PER_3_O_4_GIOCATORI);
        options.add(OTTIMA_PER_QUALSIASI_NUMERO_DI_GIOCATORI);
        options.add(OTTIMA_PER_4_O_5_GIOCATORI);

        return generalMenu(options, 1, false);
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     */
    public Boolean chooseFrenzy() {
        String ans;
        do {
            print(VUOI_LA_MODALITÀ_FRENESIA_A_FINE_PARTITA_S_N);
            ans = scan();
        }
        while(!ans.equalsIgnoreCase("s") && !ans.equalsIgnoreCase("n"));
        return ans.equalsIgnoreCase("s");
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose) {
        List<String> options = new ArrayList<>();
        options.add(SCEGLI_UN_POTENZIAMENTO_DA_USARE);
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add(NON_USARE_NESSUN_POTENZIAMENTO);
        }
        for(Power pow: inHand) {
            i++;
            options.add(SQUARE_OPEN + i + SQUARE_CLOSE + pow.getName() + COST + formatColorBox(pow.getColor()) + SPACE);
        }

        choose = generalMenu(options, starting);

        if(choose == 0)
            return null;
        else
            return inHand.get(choose - 1);
    }

    /**
     * Asks the user which ammo he wants to use
     * @param available List of powers on the player's board which can be used
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Color of the chosen ammo
     */
    public Color chooseAmmo(List<Color> available, boolean mustChoose){
        List<String> options = new ArrayList<>();
        options.add(SCEGLI_QUALE_MUNIZIONE_USARE);
        int i = 0;
        int starting = 1;
        int choose;
        if(!mustChoose) {
            starting = 0;
            options.add(NESSUNO);
        }
        for(Color ammo: available) {
            i++;
            options.add(SQUARE_OPEN + i + SQUARE_CLOSE + ammo.name());
        }

        choose = generalMenu(options, starting);

        if(choose == 0)
            return null;
        else
            return available.get(choose - 1);
    }

    /**
     * Prints out a general message to the client interface
     * @param message Message to be printed
     */
    public void generalMessage(String message) {
        println("\n" + message);
        String logMessage = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        logMessage = dtf.format(now) + SPACE + message;
        log.add(logMessage);
    }

    /**
     * Asks the user to choose between TCP and RMI connection
     * @return true in case of RMI connection, false elsewhere
     */
    public boolean useRMI() {
        String buffer;
        do
        {
            print(CONNESSIONE_CON_S_OCKET_O_CON_R_MI);
            buffer = scan();
        }
        while (!buffer.equalsIgnoreCase("r") && !buffer.equalsIgnoreCase("s"));

        return buffer.equalsIgnoreCase("r");
    }

    /**
     * Asks the user for the IP address of the server
     * @return Server's IP address
     */
    public String getIPAddress() {
        String buffer;
        //Ask for IP address
        print(INDIRIZZO_IP_DEL_SERVER);
        buffer = scan();
        while(!checkIP(buffer)) {
            println(IP_IN_FORMATO_NON_CORRETTO_REINSERISCILO);
            print(INDIRIZZO_IP_DEL_SERVER);
            buffer = scan();
        }
        return buffer;
    }

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

    private void logInfo() {
        println(LOG_ULTIMI_MESSAGGI);

        for(int i = Math.max(0, log.size() - 10); i < log.size(); i++) {
            println(log.get(i));
        }

        println("-------------------------------------------------");
    }

    /**
     * Asks the user for the IP address of the local machine
     * @return Server's IP address
     */
    public String getLocalAddress(List<String> possibleIP) {
        int pos;

        println(SELEZIONA_L_IP_DELLA_MACCHINA);
        for(int i = 0; i < possibleIP.size(); i++) {
            println(SQUARE_OPEN + (i + 1) + SQUARE_CLOSE + possibleIP.get(i));
        }
        do {
            print(SELECTION + possibleIP.size() + "]: ");
            pos = scanInt() - 1;
        }
        while (pos < 0 || pos >= possibleIP.size());
        return possibleIP.get(pos);
    }

    /**
     * Sends to the client the list of players in winning order and notifies the end of the game
     * @param winnerList Ordered players' list
     */
    public void endGame(List<PlayerView> winnerList) {
        for(int i = 0; i < TOPSPACE; i++)
            println("");
        println(LA_PARTITÀ_È_TERMINATA);
        println(ANSI_BOLD + CLASSIFICA + ANSI_RESET);
        println(String.format("Pos.    %-40s Punti", "Giocatore"));
        for(int i = 0; i < winnerList.size(); i++) {
            String bold = "";
            if(winnerList.get(i).getNick().equals(view.getMyPlayer().getNick()))
                bold = ANSI_BOLD;
            println(bold + String.format("%4d° - %-40s %5d", (i + 1), winnerList.get(i).getNick(), winnerList.get(i).getPoints()) + ANSI_RESET);
        }
        println(ALLA_PROSSIMA);
        System.exit(0);
    }
}
