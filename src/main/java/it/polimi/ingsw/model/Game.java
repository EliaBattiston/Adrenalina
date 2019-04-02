package it.polimi.ingsw.model;

import java.util.List;

public class Game {
    private Map map;
    private List<Player> players;
    private Deck<Weapon> weaponsDeck;
    private EndlessDeck<Power> powersDeck;
    private EndlessDeck<Loot> ammoDeck;
    private Kill[] skullsBoard;

    Game(int skullsNum, Map gameMap, EndlessDeck<Power> powersDeck, EndlessDeck<Loot> ammoDeck, Deck<Weapon> weaponsDeck)
    {}

    public Player getNextPlayer()
    {
        return players.get(0);
    }

    public void addPlayer(Player pl)
    {}

    public String jsonSerialize()
    {
        return "";
    }

    public void jsonDeserialize(String jsonGame)
    {}

}
