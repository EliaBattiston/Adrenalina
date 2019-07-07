package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.clientmodel.GameView;
import it.polimi.ingsw.clientmodel.MatchView;
import it.polimi.ingsw.clientmodel.MyPlayerView;
import it.polimi.ingsw.clientmodel.PlayerView;
import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class containing every information needed for the execution of a single match
 */
public class Match implements Runnable, Serializable
{
    /**
     * Model of the match's game, containing all relevant information
     */
    private Game game;

    /**
     * Currently active player
     */
    private Player active;

    /**
     * Number of remaining actions for the currently active player
     */
    private int actionsNumber;

    /**
     * Phase of the game
     */
    private GamePhase phase;

    /**
     * True if there will be a frenzy turn at the end of the game
     */
    private boolean useFrenzy;

    /**
     * The first player to play his turn in frenzy mode
     */
    private Player firstFrenzy;

    /**
     * Tracks the number of kills done in frenzy mode, because there is no space left on the skull track
     */
    private List<Player> frenzyKills;

    /**
     * Reference to the definition of all the actions that can be used in the game
     */
    private transient Activities activities;

    /**
     * Number of the current turn
     */
    private int turnNumber;

    /**
     * Gson instance used to save persistance files
     */
    private transient Gson gson;

    //Block of defines
    private static final transient int NUMBER_OF_SKULLS_ON_KILLS_BOARD = 8;
    private static final transient int REGULAR_ACTIONS_NUMBER = 2;
    private static final transient int FRENZY_ACTIONS_NUMBER = 1;
    private static final transient int NUMBER_OF_MAPS = 4;
    private static final transient int DAMAGES_FOR_KILL = 10;
    private static final transient int MAPWIDTH = 4;
    private static final transient int MAPHEIGHT = 3;
    private static final transient int REGULAR_MAX_KILL_POINTS = 8;
    private static final transient int FRENZY_MAX_KILL_POINTS = 2;
    private static final transient int NUMBER_OF_DAMAGES = 12;
    private static final transient int INDEX_OF_KILLER_IN_DAMAGES = 10;
    private static final transient int INDEX_OF_OVER_KILLER_IN_DAMAGES = 11;
    private static final transient String BASE_GAME_FILE = "baseGame.json";

    /**
     * Strings used inside this class
     */
    private static final String STA_CONFIGURANDO_LA_PARTITA = " sta configurando la partita";
    private static final String HA_SCELTO_DI_USARE_LA_MAPPA = " ha scelto di usare la mappa ";
    private static final String HA_SCELTO_DI = " ha scelto di";
    private static final String USARE_LA_MODALITA_FRENESIA = " usare la modalità Frenesia";
    private static final String PARTITA_AVVIATA_E_IL_TURNO_DI = "Partita avviata, è il turno di ";
    private static final String IL_SERVER_HA_SCELTO_DI_USARE_LA_MAPPA = "Il server ha scelto di usare la mappa ";
    private static final String IL_SERVER_HA_SCELTO_DI_USARE_LA_MODALITA_FRENESIA = "Il server ha scelto di usare la modalità Frenesia";
    private static final String NON_ESEGUE_MOSSE_POICHE_NON_E_CONNESSO = " non esegue mosse poichè non è connesso";
    private static final String E_IL_TURNO_DI = "È il turno di ";
    private static final String FINE_TURNO = "\u001B[31mFine turno ";
    private static final String ERROR_IN_WRITING_PERSISTANCE_FILE = "Error in writing persistance file";
    private static final String COULDN_T_DELETE_FILE = "Couldn't delete file";
    private static final String ADR_CORRECTLY_DELETED = ".adr correctly deleted";
    private static final String PER_PESCARE_UN_NUOVO_POTENZIAMENTO = " per pescare un nuovo potenziamento";
    private static final String SCARTA = " scarta ";
    private static final String SCEGLI_UN_POTENZIAMENTO = "Scegli un potenziamento da scartare, il colore del potenziamento scartato determinerà la cella di spawn";
    private static final String E_SPAWNA_NELLA_CELLA = " e spawna nella cella ";
    private static final String GLI_OGGETTI_MANCANTI_DALLA_MAPPA_SONO_STATI_POSIZIONATI = "Gli oggetti mancanti dalla mappa sono stati posizionati";
    private static final String E_STATO_UCCISO_DA = " è stato ucciso da ";
    private static final String IL_GIOCO_E_TERMINATO = "\u001b[34mIl gioco è terminato\u001B[0m";
    private static final String SI_E_DISCONNESSO = " si è disconnesso";

    /**
     * Creates a new empty match
     * @param skullsNum Number of skulls to be used in the game
     */
    public Match(int skullsNum)
    {
        this.activities = Activities.getInstance();
        this.active = null;
        this.actionsNumber = 0;
        this.phase = GamePhase.INITIALIZING;
        this.firstFrenzy = null;
        this.frenzyKills = new ArrayList<>();
        this.turnNumber = 0;
        this.gson = new GsonBuilder().registerTypeAdapter(Cell.class, new CellAdapter()).create();

        game = Game.jsonDeserialize(BASE_GAME_FILE);
        game.getPowersDeck().shuffle();
        game.getWeaponsDeck().shuffle();
        game.getAmmoDeck().shuffle();
        game.initializeSkullsBoard(skullsNum);
    }

    /**
     *  Executes the operations needed before the start of the game
     * @throws ClientDisconnectedException If the file is not found in the filesystem
     */
    private void initialize() throws ClientDisconnectedException
    {
        broadcastMessage(game.getPlayers().get(0).getNick() + STA_CONFIGURANDO_LA_PARTITA, game.getPlayers());

        //Ask the user which maps he wants to use and if he wants to use frenzy mode
        int mapNum = game.getPlayers().get(0).getConn().chooseMap();
        try
        {
            game.loadMap(mapNum);
        }
        catch(FileNotFoundException f)
        {
            Logger.getGlobal().log(Level.SEVERE, "Map file not found");
        }
        broadcastMessage(game.getPlayers().get(0).getNick() + HA_SCELTO_DI_USARE_LA_MAPPA + mapNum, game.getPlayers());

        useFrenzy = game.getPlayers().get(0).getConn().chooseFrenzy();
        broadcastMessage(game.getPlayers().get(0).getNick() + HA_SCELTO_DI +  ( useFrenzy ? "" : " non" ) + USARE_LA_MODALITA_FRENESIA, game.getPlayers());

        //Make folder for persistance files
        new File("matches").mkdirs();

        refillMap();

        updateViews();

        //The first turn is played by the player in the first position of the list
        active = game.getPlayers().get(0);
        actionsNumber = REGULAR_ACTIONS_NUMBER;

        broadcastMessage(PARTITA_AVVIATA_E_IL_TURNO_DI + active.getNick(), game.getPlayers());

        this.phase = GamePhase.REGULAR;
    }

    /**
     * Gets the game model of the match
     * @return The match's game model
     */
    public Game getGame()
    {
        return game;
    }

    /**
     * Runs the main logic of the game in a thread
     */
    public void run()
    {
        if(phase == GamePhase.INITIALIZING)
        {
            try
            {
                initialize();
            } catch (ClientDisconnectedException e)
            {
                disconnectPlayer(game.getPlayers().get(0), game.getPlayers());

                //If the first player disconnects while trying to choose, the server choses randomly
                if (game.getMap() == null)
                {
                    int mapNum = new Random().nextInt(NUMBER_OF_MAPS) + 1;
                    try
                    {
                        game.loadMap(mapNum);
                    } catch (FileNotFoundException f)
                    {
                        Logger.getGlobal().log(Level.SEVERE, "Map file not found");
                    }
                    broadcastMessage(IL_SERVER_HA_SCELTO_DI_USARE_LA_MAPPA + mapNum, game.getPlayers());
                }

                useFrenzy = true;
                broadcastMessage(IL_SERVER_HA_SCELTO_DI_USARE_LA_MODALITA_FRENESIA, game.getPlayers());
            }
        }

        if(gson == null)
        {
            this.gson = new GsonBuilder().registerTypeAdapter(Cell.class, new CellAdapter()).create();
        }

        if(activities == null)
            activities = Activities.getInstance();

        updateViews();

        while(phase != GamePhase.ENDED)
        {
            if(active.getConn() != null)
            {
                //Run the player's turn until the timeout runs out
                new Timeout(Configuration.getInstance().getPlayerTurnSeconds(), TimeUnit.SECONDS, this);
            }
            else
                broadcastMessage(active.getNick() + NON_ESEGUE_MOSSE_POICHE_NON_E_CONNESSO, game.getPlayers());

            //Check if some cell's loot or weapons need to be refilled
            refillMap();

            //Check if there is a kill
            for(Player current : game.getPlayers())
            {
                if( Arrays.stream(current.getReceivedDamage()).filter(Objects::nonNull).count() > DAMAGES_FOR_KILL)
                {
                    registerKill(current);
                    spawnPlayer(current);
                }
            }

            //When the active player's turn finishes, we pick the next active player
            active = game.getNextPlayer(active);

            broadcastMessage(E_IL_TURNO_DI + active.getNick(), game.getPlayers());

            if(phase == GamePhase.FRENZY)
            {
                //Set the firstFrenzy player
                if(firstFrenzy == null)
                {
                    firstFrenzy = active;

                    for(Player p : game.getPlayers())
                    {
                        if(Arrays.stream(p.getReceivedDamage()).noneMatch(Objects::nonNull))
                        {
                            p.setFrenzyBoard(true);
                        }
                    }
                }
                else if(game.getPlayers().indexOf(active) == 0)
                {
                    phase = GamePhase.ENDED;
                }
            }

            //Set how many actions the player can make in his turn
            if (phase == GamePhase.FRENZY && game.getPlayers().indexOf(active) < game.getPlayers().indexOf(firstFrenzy) )
            {
                actionsNumber = FRENZY_ACTIONS_NUMBER;
            }
            else
            {
                actionsNumber = REGULAR_ACTIONS_NUMBER;
            }

            if(game.getPlayers().stream().map(Player::getConn).filter(Objects::nonNull).count() < Configuration.getInstance().getMinPlayers())
                phase = GamePhase.ENDED;

            //If the game ended make the last points calculation
            if(phase == GamePhase.ENDED)
            {
                endGame();
            }
            else {
                updateViews();

                println(FINE_TURNO + turnNumber + "\u001B[0m");
                turnNumber++;
            }

            try (PrintWriter out = new PrintWriter("matches/" + this.hashCode() + ".adr")) {
                out.println(gson.toJson(this));
            }
            catch(FileNotFoundException e)
            {
                Logger.getGlobal().log(Level.SEVERE, ERROR_IN_WRITING_PERSISTANCE_FILE, e);
            }
        }

        if(phase == GamePhase.ENDED)
        {
            File saved = new File("matches/" + this.hashCode() + ".adr");
            if(saved.delete())
                println("File" + this.hashCode() + ADR_CORRECTLY_DELETED);
            else
                println(COULDN_T_DELETE_FILE + this.hashCode() + ".adr");

        }
    }

    public void playerTurn()
    {
        //Defining needed variables
        List<Action> availableActions; //Actions the user can currently do
        List<Action> feasible = new ArrayList<>();
        Action chosen;

        //Check if spawning is needed
        if(!active.isSpawned() && !spawnPlayer(active))
            return;

        //Let players use their actions
        for( ; actionsNumber>0 ; actionsNumber--)
        {
            //Check what the player can do right now
            availableActions = activities.getAvailable(
                    (int) Arrays.stream(active.getReceivedDamage()).filter(Objects::nonNull).count(),
                    phase == GamePhase.FRENZY && active.getFrenzyBoard(),
                    firstFrenzy != null && game.getPlayers().indexOf(active) < game.getPlayers().indexOf(firstFrenzy)
            );

            //Determine which are feasible
            feasible.clear();
            for(Action a : availableActions)
            {
                if(a.isFeasible(active, game.getMap(), game))
                {
                    feasible.add(a);
                }
            }

            try {
                chosen = active.getConn().chooseAction(feasible, true);
                chosen.execute(active, game.getMap(), game);

                if(chosen.getLambdaID().equals("a-p"))
                    actionsNumber++;
            }
            catch(ClientDisconnectedException e) {
                disconnectPlayer(active, game.getPlayers());
                return;
            }

            updateViews();
        }

        //The player can use a power before the turn ends
        if(activities.getPlayPower().isFeasible(active, game.getMap(), game))
        {
            availableActions = new ArrayList<>();
            availableActions.add(activities.getPlayPower());
            try {
                chosen = active.getConn().chooseAction(availableActions, false);
                if(chosen != null)
                    chosen.execute(active, game.getMap(), game);
            }
            catch(ClientDisconnectedException e) {
                disconnectPlayer(active, game.getPlayers());
                return;
            }

        }

        //Reload weapons
        if(FeasibleLambdaMap.possibleReload(active)) {
            try {
                ActionLambdaMap.reload(active, game.getPlayers());
            }
            catch(ClientDisconnectedException e) {
                disconnectPlayer(active, game.getPlayers());
            }

            updateViews();
        }

        //Clean used power cards
        active.cleanUsedPowers(game.getPowersDeck());
    }

    /**
     * Updates game data on every client
     */
    private void updateViews()
    {
        for(Player p: game.getPlayers())
        {
            if(p.getConn() != null)
            {
                try{
                    p.getConn().updateGame(this.getView(p));
                }
                catch(ClientDisconnectedException e)
                {
                    disconnectPlayer(p, game.getPlayers());
                }
            }
        }
    }

    /**
     * Does every step needed to spawn a player
     * @param pl Player who needs to be spawned
     * @return True if the player didn't disconnect while spawning
     */
    private boolean spawnPlayer(Player pl)
    {
        boolean spawned = false;

        if(pl.getSkulls() == 0) //First spawn
        {
            pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                powers[0] = game.getPowersDeck().draw();
                powers[1] = game.getPowersDeck().draw();
            }));
        }
        else //Draw a power
        {
            pl.applyEffects((damage, marks, position, weapons, powers, ammo) -> {
                Power newPower = game.getPowersDeck().draw();
                Power discarded = null;

                if (Arrays.stream(powers).noneMatch(Objects::isNull))
                {
                    List<Power> inHand = new ArrayList<>(Arrays.asList(powers));
                    inHand.add(newPower);
                    try
                    {
                        discarded = pl.getConn().discardPower(inHand, true);
                    } catch (ClientDisconnectedException e)
                    {
                        Match.disconnectPlayer(pl, game.getPlayers());
                        discarded = inHand.get(new Random().nextInt(inHand.size()));
                    }

                    game.getPowersDeck().scrapCard(discarded);

                    Match.broadcastMessage(pl.getNick() + SCARTA + discarded.getName() + PER_PESCARE_UN_NUOVO_POTENZIAMENTO, game.getPlayers());
                }

                int empty = Arrays.asList(powers).indexOf(discarded);

                if (empty != -1) //if it was null put in the first null, if it was a specific card discarded, put where the card was
                    powers[empty] = newPower;
            });
        }



        updateViews();

        //Choose power
        Power chosen;

        if(pl.getConn() != null)
        {
            try
            {
                pl.getConn().sendMessage(SCEGLI_UN_POTENZIAMENTO);
                chosen = pl.getConn().discardPower(pl.getPowers(), true);
                spawned = true;
            }
            catch(ClientDisconnectedException e)
            {
                chosen = pl.getPowers().get(new Random().nextInt(pl.getPowers().size()));
                disconnectPlayer(pl, game.getPlayers());
            }
        }
        else //If the player is disconnected we have to choose a random card to make him respawn
        {
            chosen = pl.getPowers().get(new Random().nextInt(pl.getPowers().size()));
        }

        Color spawnColor = chosen.getColor();
        //Discard the power
        pl.applyEffects(EffectsLambda.removePower(chosen, game.getPowersDeck()));

        boolean found = false;
        int spawnX = 0;
        int spawnY = 0;

        //Move in the correct position
        for (int x = 0; x < MAPWIDTH && !found; x++) {
            for (int y = 0; y < MAPHEIGHT && !found; y++) {
                if (game.getMap().getCell(x, y) != null && game.getMap().getCell(x, y).hasSpawn(spawnColor)) {
                    spawnX = x;
                    spawnY = y;
                    found = true;
                }
            }
        }

        final Point spawnPoint = new Point(spawnX, spawnY);

        if (found) {
            pl.applyEffects(EffectsLambda.move(pl, spawnPoint, game.getMap()));
        }
        //If not found the map is incorrect

        broadcastMessage(pl.getNick() + SCARTA + chosen.getName() + E_SPAWNA_NELLA_CELLA + ((spawnY* MAPWIDTH)+spawnX+1), game.getPlayers() );

        pl.setSpawned(true);

        updateViews();
        return spawned;
    }

    /**
     * Refill loot or weapons in every cell of the map that needs it
     */
    private void refillMap()
    {
        Cell selectedCell = null;

        for(int x = 0; x < MAPWIDTH; x++)
        {
            for(int y = 0; y < MAPHEIGHT; y++)
            {
                //Don't check if the cell is unused
                selectedCell = game.getMap().getCell(x, y);
                if(selectedCell != null)
                    selectedCell.refill(game);
            }
        }

        broadcastMessage(GLI_OGGETTI_MANCANTI_DALLA_MAPPA_SONO_STATI_POSIZIONATI, game.getPlayers());
    }

    /**
     * Do the necessary operations when a player is killed
     * @param killed The player who was killed
     */
    protected void registerKill(Player killed)
    {
        int nextSkull; //Index of the first usable skull on the board
        int maxPoints; //Number of points the player that inflicted the most damage gets when killing
        int damageNum; //Number of damages inflicted by a user
        List<Entry<Player, Integer>> inflictedDamages = new ArrayList<>(); //Used to count how many damages every player inflicted

        //Calculate max points
        if(phase == GamePhase.FRENZY)
            maxPoints = FRENZY_MAX_KILL_POINTS - (killed.getSkulls() * 2);
        else
            maxPoints = REGULAR_MAX_KILL_POINTS - (killed.getSkulls() * 2);

        if(maxPoints < 1)
            maxPoints = 1;

        //Count damages
        for(Player enemy : game.getPlayers())
        {
            if(enemy != killed)
            {
                damageNum = (int)Arrays.stream(killed.getReceivedDamage()).filter(player -> game.getPlayer(player) == enemy ).count();
                if(damageNum > 0)
                    inflictedDamages.add( new AbstractMap.SimpleEntry<Player, Integer>(enemy, damageNum));
            }
        }

        //Sorting the inflicted damages in descending order, by damage number
        inflictedDamages.sort( (e1, e2) -> {
            if (e1.getValue() > e2.getValue())
                return -1;
            else if(e1.getValue() < e2.getValue())
                return 1;
            else
            {
                //Check who inflicted damage first
                for(String p : killed.getReceivedDamage())
                {
                    if(game.getPlayer(p) == e1.getKey())
                        return -1;

                    if(game.getPlayer(p) == e2.getKey())
                        return 1;
                }
            }

            //If implemented correctly, it's impossible to land here
            return 0;
        });

        //First blood
        if(phase != GamePhase.FRENZY)
            game.getPlayer(killed.getReceivedDamage()[0]).addPoints(1);
        //Give points
        for(Entry<Player, Integer> entry : inflictedDamages)
        {
            entry.getKey().addPoints(maxPoints);

            if(maxPoints > 2)
                maxPoints -= 2;
            else
                maxPoints = 1;
        }

        if(phase != GamePhase.FRENZY)
        {
            //Register the kill on the board
            for (nextSkull = NUMBER_OF_SKULLS_ON_KILLS_BOARD - 1; nextSkull >= 0 && (!game.getSkulls()[nextSkull].isUsed() || game.getSkulls()[nextSkull].getKiller() != null); nextSkull--)
                ;
            if (nextSkull > -1)
            {
                game.getSkulls()[nextSkull].setKiller(game.getPlayer(killed.getReceivedDamage()[INDEX_OF_KILLER_IN_DAMAGES]), killed.getReceivedDamage()[INDEX_OF_OVER_KILLER_IN_DAMAGES] != null);
                killed.addSkull();
            }

            //Give a mark to the overkiller
            if (killed.getReceivedDamage()[INDEX_OF_OVER_KILLER_IN_DAMAGES] != null)
            {
                game.getPlayer(killed.getReceivedDamage()[INDEX_OF_OVER_KILLER_IN_DAMAGES]).applyEffects(EffectsLambda.marks(1, killed));
            }

            //Check if it's time for frenzy mode
            if(nextSkull == 0)
            {
                if(useFrenzy)
                    phase = GamePhase.FRENZY;
                else
                    phase = GamePhase.ENDED;
            }
        }
        else
        {
            frenzyKills.add( game.getPlayer(killed.getReceivedDamage()[INDEX_OF_KILLER_IN_DAMAGES]) );
            if(killed.getReceivedDamage()[INDEX_OF_OVER_KILLER_IN_DAMAGES] != null)
                frenzyKills.add( game.getPlayer( killed.getReceivedDamage()[INDEX_OF_KILLER_IN_DAMAGES] ) );
        }

        broadcastMessage(killed.getNick() + E_STATO_UCCISO_DA + game.getPlayer(killed.getReceivedDamage()[INDEX_OF_KILLER_IN_DAMAGES]).getNick() + "! " + game.getPlayer(killed.getReceivedDamage()[INDEX_OF_KILLER_IN_DAMAGES]).getActionPhrase(), game.getPlayers());

        //Reset damages
        for (int i = 0; i < NUMBER_OF_DAMAGES; i++)
        {
            killed.getReceivedDamage()[i] = null;
        }

        killed.setSpawned(false);

        if(phase == GamePhase.FRENZY)
            killed.setFrenzyBoard(true);
    }

    /**
     * Makes necessary operations at the end of the game
     */
    protected void endGame()
    {
        int maxPoints; //Number of points the player that inflicted the most damage gets when killing
        int damageNum; //Number of damages inflicted by a user
        List<Entry<Player, Integer>> inflictedDamages = new ArrayList<>(); //Used to count how many damages every player inflicted

        inflictedDamages.clear();
        maxPoints = REGULAR_MAX_KILL_POINTS;

        for(Player p : game.getPlayers())
        {
            damageNum = 0;

            for(int k = NUMBER_OF_SKULLS_ON_KILLS_BOARD - 1; k >= 0; k--)
            {
                if(game.getSkulls()[k].isUsed() && game.getSkulls()[k].getKiller() == p)
                {
                    damageNum++;

                    if(game.getSkulls()[k].getOverkill())
                        damageNum++;
                }

                for(Player f : frenzyKills)
                {
                    if(f == p)
                        damageNum++;
                }
            }

            if(damageNum > 0)
                inflictedDamages.add( new AbstractMap.SimpleEntry<Player, Integer>(p, damageNum) );
        }

        //Sort the damages
        inflictedDamages.sort( (e1, e2) -> {
            if (e1.getValue() > e2.getValue())
                return -1;
            else if(e1.getValue() < e2.getValue())
                return 1;
            else
            {
                //Check who inflicted damage first
                for(int k = NUMBER_OF_SKULLS_ON_KILLS_BOARD - 1; k >= 0; k--)
                {
                    if(game.getSkulls()[k].isUsed())
                    {
                        if (game.getSkulls()[k].getKiller() == e1.getKey())
                            return -1;

                        if (game.getSkulls()[k].getKiller() == e2.getKey())
                            return 1;
                    }
                }
            }

            //If implemented correctly, it's impossible to land here
            return 0;
        });

        //Give points
        for(Entry<Player, Integer> entry : inflictedDamages)
        {
            entry.getKey().addPoints(maxPoints);

            if(maxPoints > 2)
                maxPoints -= 2;
            else
                maxPoints = 1;
        }

        //Give points for damages to every player
        for(Player damaged : game.getPlayers())
        {
            inflictedDamages.clear();

            //Calculate max points
            if(!useFrenzy)
                maxPoints = FRENZY_MAX_KILL_POINTS - (damaged.getSkulls() * 2);
            else
                maxPoints = REGULAR_MAX_KILL_POINTS - (damaged.getSkulls() * 2);

            if(maxPoints < 1)
                maxPoints = 1;

            //Count damages
            for(Player enemy : game.getPlayers())
            {
                if(enemy != damaged)
                {
                    damageNum = (int)Arrays.stream(damaged.getReceivedDamage()).filter(player -> game.getPlayer(player) == enemy ).count();
                    if(damageNum > 0)
                        inflictedDamages.add( new AbstractMap.SimpleEntry<Player, Integer>(enemy, damageNum));
                }
            }

            //Sorting the inflicted damages in descending order, by damage number
            inflictedDamages.sort( (e1, e2) -> {
                if (e1.getValue() > e2.getValue())
                    return -1;
                else if(e1.getValue() < e2.getValue())
                    return 1;
                else
                {
                    //Check who inflicted damage first
                    for(String p : damaged.getReceivedDamage())
                    {
                        if(game.getPlayer(p) == e1.getKey())
                            return -1;

                        if(game.getPlayer(p) == e2.getKey())
                            return 1;
                    }
                }

                //If implemented correctly, it's impossible to land here
                return 0;
            });

            //First blood
            if(!useFrenzy && damaged.getReceivedDamage()[0] != null)
                game.getPlayer(damaged.getReceivedDamage()[0]).addPoints(1);
            //Give points
            for(Entry<Player, Integer> entry : inflictedDamages)
            {
                entry.getKey().addPoints(maxPoints);

                if(maxPoints > 2)
                    maxPoints -= 2;
                else
                    maxPoints = 1;
            }
        }

        List<Player> winners = new ArrayList<>(game.getPlayers());

        Collections.sort(winners, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o2.getPoints() - o1.getPoints();
            }
        });

        for(Player p: game.getPlayers()) {
            try {
                if(p.getConn() != null)
                    p.getConn().endGame(winners);
            }
            catch (ClientDisconnectedException ignore) {
                ;
            }
        }
        println(IL_GIOCO_E_TERMINATO);
    }

    /**
     * Gives a representation of the match which is suitable to be sent to a client
     * @param viewer Player whose perspective will be reflected in the MatchView
     * @return View of the match, ready to be serialized and sent
     */
    public MatchView getView(Player viewer){
        MyPlayerView v = viewer.getFullView();
        PlayerView act = active==null?null:active.getView();
        GameView gv = game.getView();
        return new MatchView(v, act, gv, phase, Configuration.getInstance().getPlayerTurnSeconds());
    }

    /**
     * Send a message to every player and show it in stdout
     * @param message Message to be displayed
     * @param players List of players to receive the message
     */
    public static void broadcastMessage(String message, List<Player> players)
    {
        println(message);
        for(Player p: players)
        {
            if(p.getConn() != null)
            {
                try{
                    p.getConn().sendMessage(message);
                }
                catch(ClientDisconnectedException e)
                {
                    disconnectPlayer(p, players);
                }
            }
        }
    }

    /**
     * Manage the disconnection of a player
     * @param pl Disconnected player
     * @param players Players who'll receive an announcement of the disconnection
     */
    public static void disconnectPlayer(Player pl, List<Player> players)
    {
        Logger.getGlobal().log( Level.INFO, pl.getNick()+ SI_E_DISCONNESSO);
        if(pl.getConn() != null)
            pl.getConn().cancelConnection();
        pl.setConn(null);

        broadcastMessage(pl.getNick() + SI_E_DISCONNESSO, players);
    }

    /**
     * Gets the active player of the match
     * @return Currently active player
     */
    public Player getActive()
    {
        return active;
    }

    /**
     * Fixes the active player reference after a persistent game is loaded
     */
    public void fixActive()
    {
        active = game.getPlayer(active.getNick());
    }

    /**
     * Call to System.out.println function
     * @param s String to be printed
     */
    private static void println(String s) { System.out.println(s);}
}
