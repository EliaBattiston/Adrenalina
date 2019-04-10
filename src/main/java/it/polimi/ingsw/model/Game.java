package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.exceptions.UsedNameException;

import java.io.FileNotFoundException;
import java.io.FileReader;
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

    //TODO add change to the UML diagram and test
    public Player getNextPlayer(Player current)
    {
        int position = players.indexOf(current);

        if(position == players.size() -1)
            return players.get(0);

        return players.get( position + 1  );
    }

    //TODO avoid double character?
    /**
     * Add a player to the game
     * @param pl Added player
     * @return  If false there are too many players already or there is already a player with the same name
     * @throws UsedNameException When the new player has the same nickname as another one already in the game, but is not the same player
     */
    public boolean addPlayer(Player pl) throws UsedNameException
    {
        if(players.size() < 5 && !players.contains(pl))
        {
            if(players.stream().map(Player::getNick).noneMatch(nick -> nick == pl.getNick() ))
            {
                throw new UsedNameException();
            }
            else
            {
                players.add(pl);
                return true;
            }
        }

        return false;
    }

    public List<Player> getPlayers()
    {
        return new ArrayList<>(players);
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
        GsonBuilder gsonBilder = new GsonBuilder();
        gsonBilder.registerTypeAdapter(Cell.class, new CellAdapter());
        Gson gson = gsonBilder.create();

        return gson.toJson(this);
    }

    /**
     * Deserialize a json representing the class
     * @param pathJsonFile the file containing the json representation of the class
     * @return the object made from the json
     * @throws FileNotFoundException if the file is not found
     */
    public static Game jsonDeserialize(String pathJsonFile) throws FileNotFoundException
    {
        JsonReader reader = new JsonReader(new FileReader(pathJsonFile));

        GsonBuilder gsonBilder = new GsonBuilder();
        gsonBilder.registerTypeAdapter(Cell.class, new CellAdapter());
        Gson gson = gsonBilder.create();

        return gson.fromJson(reader, Game.class);
    }

    /**
     * Load the map from a file
     * @param pathJsonFile path to the json file
     * @throws FileNotFoundException if the file is not found
     */
    public void loadMap(String pathJsonFile) throws FileNotFoundException{
        this.map = Map.jsonDeserialize(pathJsonFile);
    }
}
