package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.model.*;

import java.util.List;

/**
 * View of the game for the current player.
 * From the model.Game class the missing parts are: the decks, the other players data
 * From the controller.Match class it gets:
 */
public class GameView {
    /**
     * Map used in this match
     */
    private String map;
    /**
     * Players participating the match
     */
    private List<Player> players;
    /**
     * Killshot track, made of 8 Kills
     */
    private Kill[] skullsBoard;

    public GameView(Map map, List<Player> players, Kill[] skullsBoard) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Cell.class, new CellAdapter());
        Gson gson = gsonBuilder.create();
        this.map = gson.toJson(map, Map.class);
        this.players = players;
        this.skullsBoard = skullsBoard;
    }

    public Map getMap() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Cell.class, new CellAdapter());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(map, Map.class);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Kill[] getSkullsBoard() {
        return skullsBoard;
    }

    public void setMap(Map map) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Cell.class, new CellAdapter());
        Gson gson = gsonBuilder.create();
        this.map = gson.toJson(map, Map.class);
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setSkullsBoard(Kill[] skullsBoard) {
        this.skullsBoard = skullsBoard;
    }
}
