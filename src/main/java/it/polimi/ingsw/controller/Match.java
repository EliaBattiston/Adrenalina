package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.WrongPointException;
import it.polimi.ingsw.model.*;

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
     * True if the game is in frenzy mode
     */
    private GamePhase phase;

    /**
     * The first player to play his turn in frenzy mode
     */
    private Player firstFrenzy;

    /**
     * Reference to the definition of all the actions that can be used in the game
     */
    private Activities activities;

    /**
     * Creates a new empty match
     * @param skullsNum Number of skulls to be used in the game
     */
    public Match(int skullsNum) throws FileNotFoundException
    {
        initialize(skullsNum);

        this.activities = Activities.getInstance();
        this.active = null;
        this.actionsNumber = 0;
        this.phase = GamePhase.REGULAR;
        this.firstFrenzy = null;
    }

    /**
     *  Executes the operations needed before the start of the game
     * @param skullsNum Number of skulls to be used in the game
     */
    private void initialize(int skullsNum) throws FileNotFoundException
    {
        game = Game.jsonDeserialize("resources/baseGame.json");
        game.getPowersDeck().shuffle();
        game.getWeaponsDeck().shuffle();
        game.getAmmoDeck().shuffle();
        game.initializeSkullsBoard(skullsNum);

        //TODO let the user choose
        game.loadMap("resources/map1.json");
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
        //Defining needed variables
        List<Action> availableActions; //Actions the user can currently do

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

                active.getConn().chooseAction(availableActions, true).execute(active, game.getMap(), game.getPlayers());
            }

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
                //TODO avoid instanceof statements
                if(game.getMap().getCell(x, y) instanceof  SpawnCell && ((SpawnCell) game.getMap().getCell(x, y)).getSpawn() == spawnColor)
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
                {
                    //TODO avoid instanceof statements
                    if (selectedCell instanceof RegularCell && ((RegularCell) selectedCell).getLoot() == null)
                    {
                        ((RegularCell) selectedCell).refillLoot(game.getAmmoDeck().draw());
                    } else if (selectedCell instanceof SpawnCell)
                    {
                        while (((SpawnCell) selectedCell).getWeapons().size() < 3)
                        {
                            ((SpawnCell) selectedCell).refillWeapon(game.getWeaponsDeck().draw());
                        }
                    }
                }
            }
        }
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

        //The player was killed
        //Forget about damage givers of the last turn
        inflictedDamages.clear();

        //Calculate max points
        maxPoints = 8 - (killed.getSkulls() * 2);
        //Count damages
        for(Player enemy : game.getPlayers())
        {
            if(enemy != killed)
            {
                damageNum = (int)Arrays.stream(killed.getReceivedDamage()).filter(player -> player == enemy ).count();
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
                for(Player p : killed.getReceivedDamage())
                {
                    if(p == e1.getKey())
                        return -1;

                    if(p == e2.getKey())
                        return 1;
                }
            }

            //If implemented correctly, it's impossible to land here
            return 0;
        });

        //First blood
        killed.getReceivedDamage()[0].addPoints(1);
        //Give points
        for(Entry<Player, Integer> entry : inflictedDamages)
        {
            entry.getKey().addPoints(maxPoints);

            if(maxPoints > 2)
                maxPoints -= 2;
            else
                maxPoints = 1;
        }

        //Register the kill on the board
        for(nextSkull = 7; nextSkull >= 0 && (!game.getSkulls()[nextSkull].isUsed() || game.getSkulls()[nextSkull].getKiller() != null); nextSkull--)
            ;
        if(nextSkull > -1)
        {
            game.getSkulls()[nextSkull].setKiller(killed.getReceivedDamage()[10], killed.getReceivedDamage()[11] != null);
            killed.addSkull();
        }

        //Give a mark to the overkiller
        if(killed.getReceivedDamage()[11] != null)
        {
            killed.getReceivedDamage()[11].applyEffects(EffectsLambda.marks(1, killed));
        }

        //Reset damages
        for(int i = 0; i < 12; i++)
        {
            killed.getReceivedDamage()[i] = null;
        }

        //Check if it's time for frenzy mode
        if(nextSkull == 0)
        {
            phase = GamePhase.FRENZY;
        }
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
    }
}
