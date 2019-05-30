package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.MatchView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class containing every information needed for the execution of a single match
 */
public class Match implements Runnable
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
    private Activities activities;

    /**
     * Number of skulls
     */
    private int skullsNum;

    /**
     * Number of the current turn
     */
    private int turnNumber;

    /**
     * Gson instance used to save persistance files
     */
    private transient Gson gson;

    /**
     * Creates a new empty match
     * @param skullsNum Number of skulls to be used in the game
     * @throws FileNotFoundException If the file is not found in the filesystem
     */
    public Match(int skullsNum) throws FileNotFoundException
    {
        this.activities = Activities.getInstance();
        this.active = null;
        this.actionsNumber = 0;
        this.phase = GamePhase.INITIALIZING;
        this.firstFrenzy = null;
        this.frenzyKills = new ArrayList<>();
        this.skullsNum = skullsNum;
        this.turnNumber = 0;
        this.gson = new GsonBuilder().registerTypeAdapter(Cell.class, new CellAdapter()).create();

        game = Game.jsonDeserialize("baseGame.json");
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
        broadcastMessage(game.getPlayers().get(0).getNick() + " ha scelto di usare la mappa " + mapNum, game.getPlayers());

        useFrenzy = game.getPlayers().get(0).getConn().chooseFrenzy();
        broadcastMessage(game.getPlayers().get(0).getNick() + " ha scelto di" +  ( useFrenzy ? "" : " non" ) + " usare la modalità Frenesia", game.getPlayers());

        //Make folder for persistance files
        new File("matches").mkdirs();

        refillMap();


        updateViews();

        //The first turn is played by the player in the first position of the list
        active = game.getPlayers().get(0);
        actionsNumber = 2;

        broadcastMessage("Partita avviata, è il turno di " + active.getNick(), game.getPlayers());

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
                    int mapNum = new Random().nextInt(4) + 1;
                    try
                    {
                        game.loadMap(mapNum);
                    } catch (FileNotFoundException f)
                    {
                        Logger.getGlobal().log(Level.SEVERE, "Map file not found");
                    }
                    broadcastMessage("Il server ha scelto di usare la mappa " + mapNum, game.getPlayers());
                }

                useFrenzy = true;
                broadcastMessage("Il server ha scelto di" + (useFrenzy ? "" : " non") + " usare la modalità Frenesia", game.getPlayers());
            }
        }

        if(gson == null)
        {
            this.gson = new GsonBuilder().registerTypeAdapter(Cell.class, new CellAdapter()).create();
        }

        while(phase != GamePhase.ENDED)
        {
            if(game.getPlayers().stream().map(p->p.getConn()).filter(Objects::nonNull).count() < Configuration.getInstance().getMinPlayers())
                phase = GamePhase.ENDED;

            if(active.getConn() != null)
            {
                //Run the player's turn until the timeout runs out
                new Timeout(Configuration.getInstance().getPlayerTurnSeconds(), TimeUnit.SECONDS, this);
                //playerTurn(); //FIXME
            }
            else
                broadcastMessage(active.getNick() + " non esegue mosse poichè non è connesso", game.getPlayers());

            //Check if some cell's loot or weapons need to be refilled
            refillMap();

            //Check if there is a kill
            for(Player current : game.getPlayers())
            {
                if( Arrays.stream(current.getReceivedDamage()).filter(Objects::nonNull).count() > 10 )
                {
                    registerKill(current);
                    spawnPlayer(current);
                }
            }

            //When the active player's turn finishes, we pick the next active player
            active = game.getNextPlayer(active);

            broadcastMessage("È il turno di " + active.getNick(), game.getPlayers());

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
                actionsNumber = 1;
            }
            else
            {
                actionsNumber = 2;
            }

            //If the game ended make the last points calculation
            if(phase == GamePhase.ENDED)
            {
                endGame();
            }
            else {
                updateViews();

                System.out.println("\u001B[31mFine turno " + turnNumber + "\u001B[0m");
                turnNumber++;
            }

            try (PrintWriter out = new PrintWriter("matches/" + this.hashCode() + ".adr")) {
                out.println(gson.toJson(this));
            }
            catch(FileNotFoundException e)
            {
                Logger.getGlobal().log(Level.SEVERE, "Error in writing persistance file", e);
            }
        }

        if(phase == GamePhase.ENDED)
        {
            File saved = new File("matches/" + this.hashCode() + ".adr");
            if(saved.delete())
                System.out.println("File" + this.hashCode() + ".adr correctly deleted");
            else
                System.out.println("Couldn't delete file" + this.hashCode() + ".adr");

        }
    }

    public void playerTurn()
    {
        //Defining needed variables
        List<Action> availableActions; //Actions the user can currently do
        List<Action> feasible = new ArrayList<>();
        Action chosen;

        //Check if spawning is needed
        if(!active.isSpawned())
        {
            if(!spawnPlayer(active))
                return;
        }

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

        //Reload weapons
        if(FeasibleLambdaMap.possibleReload(active)) {
            try {
                ActionLambdaMap.reload(active, game.getPlayers());
            }
            catch(ClientDisconnectedException e) {
                disconnectPlayer(active, game.getPlayers());
            }
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
                    p.getConn().updateGame(getMatchView(p));
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

                    Match.broadcastMessage(pl.getNick() + " scarta " + discarded.getName() + " per pescare un nuovo potenziamento", game.getPlayers());
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
                pl.getConn().sendMessage("Scegli un potenziamento da scartare, il colore del potenziamento scartato determinerà la cella di spawn");
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
        for (int x = 0; x < 4 && !found; x++) {
            for (int y = 0; y < 3 && !found; y++) {
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

        broadcastMessage(pl.getNick() + " scarta " + chosen.getName() + " e spawna nella cella " + ((spawnY*4)+spawnX+1), game.getPlayers() );

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

        for(int x = 0; x < 4; x++)
        {
            for(int y = 0; y < 3; y++)
            {
                //Don't check if the cell is unused
                selectedCell = game.getMap().getCell(x, y);
                if(selectedCell != null)
                    selectedCell.refill(game);
            }
        }

        broadcastMessage("Gli oggetti mancanti dalla mappa sono stati posizionati", game.getPlayers());
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
            maxPoints = 2 - (killed.getSkulls() * 2);
        else
            maxPoints = 8 - (killed.getSkulls() * 2);

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
            for (nextSkull = 7; nextSkull >= 0 && (!game.getSkulls()[nextSkull].isUsed() || game.getSkulls()[nextSkull].getKiller() != null); nextSkull--)
                ;
            if (nextSkull > -1)
            {
                game.getSkulls()[nextSkull].setKiller(game.getPlayer(killed.getReceivedDamage()[10]), killed.getReceivedDamage()[11] != null);
                killed.addSkull();
            }

            //Give a mark to the overkiller
            if (killed.getReceivedDamage()[11] != null)
            {
                game.getPlayer(killed.getReceivedDamage()[11]).applyEffects(EffectsLambda.marks(1, killed));
            }

            broadcastMessage(killed.getNick() + " è stato ucciso da " + game.getPlayer(killed.getReceivedDamage()[10]).getNick() + "! " + game.getPlayer(killed.getReceivedDamage()[10]).getActionPhrase(), game.getPlayers());

            //Reset damages
            for (int i = 0; i < 12; i++)
            {
                killed.getReceivedDamage()[i] = null;
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
            frenzyKills.add( game.getPlayer(killed.getReceivedDamage()[10]) );
            if(killed.getReceivedDamage()[11] != null)
                frenzyKills.add( game.getPlayer( killed.getReceivedDamage()[10] ) );
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
        maxPoints = 8;

        for(Player p : game.getPlayers())
        {
            damageNum = 0;

            for(int k = 7; k >= 0; k--)
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
                for(int k = 7; k >= 0; k--)
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
                maxPoints = 2 - (damaged.getSkulls() * 2);
            else
                maxPoints = 8 - (damaged.getSkulls() * 2);

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
            catch (ClientDisconnectedException e) {
            }
        }
        System.out.println("\u001b[34mIl gioco è terminato\u001B[0m");
    }

    /**
     * Gives a representation of the match which is suitable to be sent to a client
     * @param viewer Player whose perspective will be reflected in the MatchView
     * @return View of the match, ready to be serialized and sent
     */
    public MatchView getMatchView(Player viewer){
        return new MatchView(game.getGameView(viewer), active, viewer, actionsNumber, phase, useFrenzy, firstFrenzy, Configuration.getInstance().getPlayerTurnSeconds());
    }

    /**
     * Send a message to every player and show it in stdout
     * @param message Message to be displayed
     * @param players List of players to receive the message
     */
    public static void broadcastMessage(String message, List<Player> players)
    {
        System.out.println(message);
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
        Logger.getGlobal().log( Level.INFO, pl.getNick()+" si è disconnesso" );
        if(pl.getConn() != null)
            pl.getConn().cancelConnection();
        pl.setConn(null);

        broadcastMessage(pl.getNick() + " si è disconnesso", players);
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
}
