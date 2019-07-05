package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerView implements Serializable
{
    private static final transient int DAMAGES_NUMBER = 12;

    private String nickname;

    private Fighter character;

    private List<Weapon> weapons;

    private boolean frenzyBoard;

    private List<String> receivedDamage;

    private List<String> receivedMarks;

    private int skullsNum;

    private Point position;

    private AmmoView ammo;

    private int points;

    public PlayerView(String nick, Fighter character, List<Weapon> weapons, boolean frenzyBoard,
                      List<String> receivedDamage, List<String> receivedMarks, int skullsNum, Point position,
                      AmmoView av, int points)
    {
        this.nickname = nick;
        this.character = character;
        this.weapons = weapons;
        this.frenzyBoard = frenzyBoard;
        this.receivedDamage = receivedDamage;
        this.receivedMarks = receivedMarks;
        this.skullsNum = skullsNum;
        this.position = position;
        this.ammo = av;
        this.points = points;
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
        if(i >= 0 && i < DAMAGES_NUMBER)
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

    public AmmoView getAmmo()
    {
        return ammo;
    }

    /**
     * Gives the number of requested ammunition
     * @param c Color of desired ammo
     * @return Number of available ammo for that color
     */
    public int getAmmo(Color c)
    {
        switch(c)
        {
            case RED:
                return ammo.getRed();
            case BLUE:
                return ammo.getBlue();
            case YELLOW:
                return ammo.getYellow();
            default:
                return 0;
        }
    }

    public int getPoints()
    {
        return points;
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

        Logger.getGlobal().log(Level.INFO, "Player not found!");
        return null;
    }
}
