package it.polimi.ingsw.model;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class Player
{
    private String nick;
    private String actionPhrase;
    private Fighter character;
    private int points;
    private Weapon[] weapons;
    private Player[] receivedDamage;
    private List<Player> receivedMarks;
    private int skulls;
    private Ammunitions ammo;
    private Power[] powers;
    private Point position;

    Player(String nick, String phrase, Fighter f)
    {}

    public void applyEffects(PlayerLambda pl)
    {}

    public String getNick()
    {
        return nick;
    }

    public String getActionPhrase()
    {
        return actionPhrase;
    }

    public Fighter getCharacter()
    {
        return character;
    }

    public Point getPosition()
    {
        return position;
    }

    public List<Weapon> getWeapons()
    {
        return Arrays.asList(weapons);
    }

    public List<Player> getReceivedDamage()
    {
        return Arrays.asList(receivedDamage);
    }

    public List<Player> getReceivedMarks()
    {
        return receivedMarks;
    }

    public int getSkulls()
    {
        return skulls;
    }

    public Ammunitions getAmmo()
    {
        return ammo;
    }

    public List<Power> getPowers()
    {
        return Arrays.asList(powers);
    }


}
