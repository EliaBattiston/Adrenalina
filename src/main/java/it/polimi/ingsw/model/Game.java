package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.exceptions.UsedNameException;

import java.util.ArrayList;
import java.util.List;

public class Game {
    /**
     * Map used in this match
     */
    private Map map;
    /**
     * Players participating the match
     */
    private List<Player> players;
    /**
     * Deck of weapon cards
     */
    private Deck<Weapon> weaponsDeck;
    /**
     * Deck of power cards
     */
    private EndlessDeck<Power> powersDeck;
    /**
     * Deck of loot cards
     */
    private EndlessDeck<Loot> ammoDeck;

    /**
     * Killshot track, made of 8 Kills
     */
    private Kill[] skullsBoard;

    /**
     * Creates a new game
     * @param skullsNum Number of skulls to use in the killshot track
     * @param gameMap Map to be used in the match
     * @param powersDeck Power cards deck
     * @param ammoDeck Loot cards deck
     * @param weaponsDeck Weapon cards deck
     */
    Game(int skullsNum, Map gameMap, EndlessDeck<Power> powersDeck, EndlessDeck<Loot> ammoDeck, Deck<Weapon> weaponsDeck)
    {
        skullsBoard = new Kill[8];
        for(int i=0; i<8; i++)
        {
            skullsBoard[i] = new Kill(i<skullsNum);
        }

        this.map = gameMap;
        this.powersDeck = powersDeck;
        this.ammoDeck = ammoDeck;
        this.weaponsDeck = weaponsDeck;
        this.players = new ArrayList<>();
    }

    //TODO make this useful
    public Player getNextPlayer()
    {
        return players.get(0);
    }

    //TODO change return type in UML and exception in UML
    //TODO avoid double character?
    /**
     * Add a player to the game
     * @param pl Added player
     * @return  If false there are too many players already or there is already a player with the same name
     * @throws UsedNameException When the new player has the same nickname as another one already in the game, but is not the same player
     */
    public boolean addPlayer(Player pl) throws UsedNameException
    {
        if(players.size() < 5 )
        {
            if(!players.contains(pl))
            {
                if(players.stream().map(player -> player.getNick()).noneMatch(nick -> nick == pl.getNick() ))
                {
                    throw new UsedNameException();
                }
                else
                {
                    players.add(pl);
                    return true;
                }
            }
        }

        return false;
    }

    //TODO add to UML
    public List<Player> getPlayers()
    {
        return new ArrayList<Player>(players);
    }

    public Map getMap()
    {
        return map;
    }

    public Deck<Weapon> getWeaponsDeck()
    {
        return weaponsDeck;
    }

    public EndlessDeck<Power> getPowersDeck()
    {
        return powersDeck;
    }

    public EndlessDeck<Loot> getAmmoDeck()
    {
        return ammoDeck;
    }

    /**
     * Serializes the content of the class (which contains every important aspect of the match) in json
     * @return Json serialization of the game
     */
    public String jsonSerialize()
    {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
