package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.WrongPointException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains all the info about a Player, from his nick to the current status of his boards
 *
 */
public class Player
{
    /**
     * Nickname used to log in
     */
    private String nick;
    /**
     * Phrase shouted by the user when killing an opponent, chosen at login
     */
    private String actionPhrase;
    /**
     * Game's character chosen by the user
     */
    private Fighter character;
    /**
     * Counter of acquired points, will be used to choose the winner at the end of the game
     */
    private int points;
    /**
     * Weapons in the hand of the user
     */
    private Weapon[] weapons;
    /**
     * Representation of taken damage by the use of Player tokens
     */
    private Player[] receivedDamage;
    /**
     * Representation of taken marks by the use of Player tokens
     */
    private List<Player> receivedMarks;
    /**
     * Number of kills taken by the player, used to calculate how many points other players take when killing this player
     */
    private int skulls;
    /**
     * Manager of the player's useable ammo
     */
    private Ammunitions ammo;
    /**
     * Power cards the player has in his hand
     */
    private Power[] powers;
    /**
     * Position of the player's pawn in the map
     */
    private Point position;

    /**
     * Creates a new user, in a suitable configuration to start the game
     * @param nick Nickname
     * @param phrase Action phrase
     * @param f Chosen Character
     */
    Player(String nick, String phrase, Fighter f)
    {
        this.nick = nick;
        this.actionPhrase = phrase;
        this.character = f;
        this.points = 0;
        this.weapons = new Weapon[3];
        this.receivedDamage = new Player[12];
        this.receivedMarks = new ArrayList<>();
        this.skulls = 0;
        this.ammo = new Ammunitions();
        this.powers = new Power[3];
        try
        {
            this.position = new Point(0, 0);
        }
        catch(WrongPointException e)
        {
            this.position = null;
        }

    }

    /**
     * Executes the PlayerLambda, passing private attributes as the parameters
     * @param pl Lambda function that has to be executed
     */
    public void applyEffects(PlayerLambda pl)
    {
        pl.execute(receivedDamage, receivedMarks, position, weapons, powers, ammo);
    }

    /**
     *
     * @return Nickname
     */
    public String getNick()
    {
        return nick;
    }

    /**
     *
     * @return Action phrase
     */
    public String getActionPhrase()
    {
        return actionPhrase;
    }

    /**
     *
     * @return Chosen character
     */
    public Fighter getCharacter()
    {
        return character;
    }

    /**
     *
     * @return Player's position
     */
    public Point getPosition()
    {
        return position;
    }

    /**
     *
     * @return Weapons in the hand of the player
     */
    public List<Weapon> getWeapons()
    {
        List<Weapon> list = new ArrayList<Weapon>(Arrays.asList(weapons));
        while(list.contains(null))
            list.remove(null);
        return list;
    }

    /**
     *
     * @return Player's received damage
     */
    public Player[] getReceivedDamage()
    {
        return receivedDamage;
    }

    /**
     *
     * @return Player's received marks
     */
    public List<Player> getReceivedMarks()
    {
        return receivedMarks;
    }

    /**
     *
     * @return Number of skulls on the player's board
     */
    public int getSkulls()
    {
        return skulls;
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
            case Red:
            {
                return ammo.getRed();
            }
            case Blue:
            {
                return ammo.getBlue();
            }
            case Yellow:
            {
                return ammo.getYellow();
            }
            default:
            {
                return 0;
            }
        }
    }

    /**
     *
     * @return Powers in the player's hand
     */
    public List<Power> getPowers()
    {
        List<Power> list = new ArrayList<Power>(Arrays.asList(powers));
        while(list.contains(null))
            list.remove(null);
        return list;
    }

    /**
     *
     * @return Number of points
     */
    public int getPoints()
    {
        return points;
    }
}
