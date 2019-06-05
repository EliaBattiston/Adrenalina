package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.clientmodel.KillView;
import it.polimi.ingsw.clientmodel.PlayerView;
import it.polimi.ingsw.exceptions.UsedNameException;
import it.polimi.ingsw.clientmodel.GameView;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
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
     * @param powersDeck POWER cards deck
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

    public Player getNextPlayer(Player current)
    {
        int position = players.indexOf(current);

        if(position == players.size() -1)
            return players.get(0);

        return players.get( position + 1  );
    }

    /**
     * Add a player to the game
     * @param pl Added player
     * @return  If false there are too many players already or there is already a player with the same name
     * @throws UsedNameException When the new player has the same nickname as another one already in the game, but is not the same player
     */
    public boolean addPlayer(Player pl)
    {
        if(players.size() < 5 && !players.contains(pl))
        {
            if(players.stream().anyMatch(player -> player.getNick().equals(pl.getNick()) ))
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

    public void removePlayer(String nickname) {
        for(Player p: players) {
            if(p.getNick().equals(nickname)) {
                removePlayer(p);
            }
        }
    }

    public void removePlayer(Player pl) {
        players.remove(pl);
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

    public Kill[] getSkulls() { return skullsBoard; }

    /**
     * Serializes the content of the class (which contains every important aspect of the match) in json
     * @return Json serialization of the game
     */
    public String jsonSerialize()
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Cell.class, new CellAdapter());
        Gson gson = gsonBuilder.create();

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
        JsonReader reader = new JsonReader(new InputStreamReader(Game.class.getClassLoader().getResourceAsStream(pathJsonFile)));

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Cell.class, new CellAdapter());
        Gson gson = gsonBuilder.create();

        return gson.fromJson(reader, Game.class);
    }

    /**
     * Load the map from a file
     * @param mapID number of the json file
     * @throws FileNotFoundException if the file is not found
     */
    public void loadMap(int mapID) throws FileNotFoundException{
        this.map = Map.jsonDeserialize(mapID);
    }

    /**
     * Initialize the skulls board when the first player choose the number of skulls of the match
     * @param skullsNum number of the skulls chosen for the match (between 5 and 8)
     * @return true if the number of skulls was compatible with the specification
     */
    public boolean initializeSkullsBoard(int skullsNum){
        if(skullsNum>=5 && skullsNum<=8){
            skullsBoard = new Kill[8];
            for(int i=0; i<8; i++)
                skullsBoard[i] = new Kill(i<skullsNum);

            return true;
        }
        return false;
    }

    /**
     * Gives a simpler representation of the game, to be sent to a client
     * @return GameView of this game
     */
    public GameView getView(){
        List<KillView> kills = new ArrayList<>();
        for(Kill k: skullsBoard)
        {
            kills.add(k.getView());
        }

        List<PlayerView> playerViews = new ArrayList<>();
        for(Player p: players)
        {
            playerViews.add(p.getView());
        }

        return new GameView(map.getView(), kills, playerViews);
    }

    /**
     * Returns the reference of the player with the desired nickname
     * @param nickname Nickname of the desired player
     * @return Reference of the desired player
     */
    public Player getPlayer(String nickname)
    {
        for(Player p : players)
        {
            if (p.getNick().equals(nickname))
            {
                return p;
            }
        }

        return null;
    }
}
