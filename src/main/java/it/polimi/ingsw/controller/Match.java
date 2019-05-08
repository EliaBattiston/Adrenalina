package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.WrongPointException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.MatchView;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

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
     * Creates a new empty match
     * @param skullsNum Number of skulls to be used in the game
     * @throws FileNotFoundException If the file is not found in the filesystem
     */
    public Match(int skullsNum) throws FileNotFoundException
    {
        this.activities = Activities.getInstance();
        this.active = null;
        this.actionsNumber = 0;
        this.phase = GamePhase.REGULAR;
        this.firstFrenzy = null;
        this.frenzyKills = new ArrayList<>();
        this.skullsNum = skullsNum;

        game = Game.jsonDeserialize("resources/baseGame.json");
        game.getPowersDeck().shuffle();
        game.getWeaponsDeck().shuffle();
        game.getAmmoDeck().shuffle();
        game.initializeSkullsBoard(skullsNum);
    }

    /**
     *  Executes the operations needed before the start of the game
     * @param skullsNum Number of skulls to be used in the game
     * @throws FileNotFoundException If the file is not found in the filesystem
     */
    private void initialize(int skullsNum) throws FileNotFoundException
    {
        //Ask the user which maps he wants to use and if he wants to use frenzy mode
        game.loadMap(game.getPlayers().get(0).getConn().chooseMap());
        useFrenzy = game.getPlayers().get(0).getConn().chooseFrenzy();

        refillMap();
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
        int turnNum = 0;

        try
        {
            initialize(skullsNum);
        }
        catch(FileNotFoundException e)
        {
            ;
        }

        //Defining needed variables
        List<Action> availableActions; //Actions the user can currently do
        List<Action> feasible = new ArrayList<>();

        //The first turn is played by the player in the first position of the list
        active = game.getPlayers().get(0);
        actionsNumber = 2;

        while(phase != GamePhase.ENDED)
        {
            //Check if spawning is needed
            if(active.getPosition() == null)
            {
                spawnPlayer(active);
            }

            //Let players use their actions
            for( ; actionsNumber>0 ; actionsNumber--)
            {
                //Check what the player can do right now
                availableActions = activities.getAvailable(
                        (int) Arrays.stream(active.getReceivedDamage()).filter(Objects::nonNull).count(),
                        phase == GamePhase.FRENZY,
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

                //FIXME NullPointerException here @startup sometimes
                active.getConn().chooseAction(feasible, true).execute(active, game.getMap(), game);
            }

            //Reload weapons
            if(FeasibleLambdaMap.possibleReload(active))
                ActionLambdaMap.reload(active);

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

            if(phase == GamePhase.FRENZY)
            {
                //Set the firstFrenzy player
                if(firstFrenzy == null)
                {
                    firstFrenzy = active;
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

            System.out.println("Fine turno " + turnNum);
            turnNum++;
        }
    }

    /**
     * Does every step needed to spawn a player
     * @param pl Player who needs to be spawned
     */
    private void spawnPlayer(Player pl) throws WrongPointException
    {
        //Draw powers if not enough to choose
        switch(pl.getPowers().size())
        {
            case 0:
            {
                pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                    powers[0] = game.getPowersDeck().draw();
                    powers[1] = game.getPowersDeck().draw();
                }));
                break;
            }
            case 1:
            case 2:
            {
                pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                    int i;
                    for(i = 0; i < 3 && powers[i] == null; i++)
                        ;
                    powers[i] = game.getPowersDeck().draw();
                }));
                break;
            }
            default:
                break;
        }


        //Choose power
        Power chosen = pl.getConn().discardPower(pl.getPowers(), true);
        Color spawnColor = chosen.getColor();
        //Discard the power
        pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
            for(int i = 0; i < 3; i++)
            {
                if(powers[i] == chosen)
                {
                    game.getPowersDeck().scrapCard(powers[i]);
                    powers[i] = null;
                }
            }
        }));

        boolean found = false;
        int spawnX = 0;
        int spawnY = 0;

        //Move in the correct position
        for(int x = 0; x < 4 && !found; x++)
        {
            for(int y = 0; y < 3 && !found; y++)
            {
                if(game.getMap().getCell(x, y) != null && game.getMap().getCell(x, y).hasSpawn(spawnColor))
                {
                    spawnX = x;
                    spawnY = y;
                    found = true;
                }
            }
        }

        final Point spawnPoint = new Point(spawnX, spawnY);

        if(found)
        {
            pl.applyEffects(EffectsLambda.move(pl, spawnPoint, game.getMap()));
        }
        //If not found the map is incorrect

        System.out.println(pl + " è respawnato in " + spawnX + "," + spawnY);
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

        System.out.println("Riempita la mappa con gli oggetti mancanti");
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
            if(killed.getReceivedDamage()[11] != null);
                frenzyKills.add( game.getPlayer( killed.getReceivedDamage()[10] ) );
        }

        System.out.println(killed + " è stato ucciso");
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

            for(int k = 8; k >= 0; k--)
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
                for(int k = 8; k >= 0; k--)
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
            if(phase == GamePhase.FRENZY)
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
            if(phase != GamePhase.FRENZY)
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

        System.out.println("Il gioco è terminato");
    }

    public MatchView getMatchView(Player viewer){
        return new MatchView(game.getGameView(viewer), active, viewer, actionsNumber, phase, useFrenzy, firstFrenzy);
    }
}
