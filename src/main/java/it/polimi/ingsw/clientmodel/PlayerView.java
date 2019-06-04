package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Power;
import it.polimi.ingsw.model.Weapon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerView implements Serializable
{
    private String nickname;

    private Fighter character;

    private List<Weapon> weapons;

    private boolean frenzyBoard;

    private List<String> receivedDamage;

    private List<String> receivedMarks;

    private int skullsNum;

    private Point position;

    public PlayerView(String nick, Fighter character, List<Weapon> weapons, boolean frenzyBoard,
                      List<String> receivedDamage, List<String> receivedMarks, int skullsNum, Point position)
    {
        this.nickname = nick;
        this.character = character;
        this.weapons = weapons;
        this.frenzyBoard = frenzyBoard;
        this.receivedDamage = receivedDamage;
        this.receivedMarks = receivedMarks;
        this.skullsNum = skullsNum;
        this.position = position;
    }

    public String getNick()
    {
        return nickname;
    }

    public Fighter getCharacter(){
        return character;
    }

    public List<Weapon> getWeapons()
    {
        return weapons;
    }

    public List<Power> getPowers()
    {
        return new ArrayList<>();
    }

    public boolean getFrenzyBoard(){ return frenzyBoard; }

    public String getDamage(int i)
    {
        if(i >= 0 && i < 12)
        {
            return receivedDamage.get(i);
        }
        return null;
    }

    public List<String> getReceivedMarks()
    {
        return receivedMarks;
    }

    public int getSkulls()
    {
        return skullsNum;
    }

    public Point getPosition()
    {
        return position;
    }

    /**
     * Helps getting the fighter of a player by providing his nickname
     * @param players List of players to search for the nickname
     * @param nick Nickname of the desired player
     * @return Fighter of the player
     */
    public static Fighter fighterFromNick(List<PlayerView> players, String nick){
        for(PlayerView p : players)
            if(p.getNick().equals(nick))
                return p.getCharacter();

        Logger.getGlobal().log(Level.SEVERE, "Error while looking for Fighter from nick");
        return null;
    }
}
