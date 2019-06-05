package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.Kill;
import it.polimi.ingsw.model.Map;
import it.polimi.ingsw.model.Player;

import java.io.Serializable;
import java.util.List;

/**
 * View of the game from the viewer player's perspective
 */
public class GameView implements Serializable {
    private MapView map;

    private List<PlayerView> players;

    private List<KillView> kills;

    public GameView(MapView mv, List<KillView> kills, List<PlayerView> players)
    {
        this.map = map;
        this.kills = kills;
        this.players = players;
    }

    public MapView getMap()
    {
        return map;
    }

    public List<PlayerView> getPlayers()
    {
        return players;
    }

    public List<KillView> getSkullsBoard()
    {
        return kills;
    }
}
