package it.polimi.ingsw.model;

import it.polimi.ingsw.clientmodel.MyPlayerView;
import it.polimi.ingsw.clientmodel.PlayerView;
import it.polimi.ingsw.controller.Connection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains all the info about a Player, from his nick to the current status of his boards
 *
 */
public class Player implements Serializable
{
    private transient final static int maxWeaponsNumber = 3;
    private transient final static int maxPowersNumber = 3;
    private transient final static int damagesNumber = 12;

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
     * Representation of taken damage by the use of Player's nickname
     */
    private String[] receivedDamage;
    /**
     * Representation of taken marks by the use of Player's nickname
     */
    private List<String> receivedMarks;
    /**
     * Number of kills taken by the player, used to calculate how many points other players take when killing this player
     */
    private int skulls;
    /**
     * Manager of the player's useable ammo
     */
    private Ammunitions ammo;
    /**
     * POWER cards the player has in his hand
     */
    private Power[] powers;
    /**
     * Position of the player's pawn in the map
     */
    private Point position;

    /**
     * Connection of the player
     */
    private transient Connection conn;

    /**
     * False if the player has to be spawned in the map
     */
    private boolean spawned;

    /**
     * List of used power cards, waiting to be sent to the scraps deck at the end of the turn
     */
    private List<Power> usedPowers;

    /**
     * True if the player is using its frenzy board, false otherwise
     */
    private boolean frenzyBoard;

    /**
     * Creates a new user, in a suitable configuration to start the game
     * @param nick Nickname
     * @param phrase Action phrase
     * @param f Chosen Character
     */
    public Player(String nick, String phrase, Fighter f)
    {
        this.nick = nick;
        this.actionPhrase = phrase;
        this.character = f;
        this.points = 0;
        this.weapons = new Weapon[maxWeaponsNumber];
        this.receivedDamage = new String[damagesNumber];
        this.receivedMarks = new ArrayList<>();
        this.skulls = 0;
        this.ammo = new Ammunitions();
        this.powers = new Power[maxPowersNumber];
        this.position = new Point(0, 0);
        this.spawned = false;
        this.usedPowers = new ArrayList<>();
        this.frenzyBoard = false;

        this.conn = null;
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
        List<Weapon> list = new ArrayList<>(Arrays.asList(weapons));
        while(list.contains(null))
            list.remove(null);
        return list;
    }

    /**
     *
     * @return Player's received damage
     */
    public String[] getReceivedDamage()
    {
        return receivedDamage;
    }

    /**
     *
     * @return Player's received marks
     */
    public List<String> getReceivedMarks()
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

    public Ammunitions getAmmo(){
        return new Ammunitions(ammo);
    }

    /**
     * Gives the number of requested ammunition
     * @param c Color of desired ammo
     * @param withPowers If true the available amount includes useable powers
     * @return Number of available ammo for that color
     */
    public int getAmmo(Color c, boolean withPowers)
    {
        int amount = 0;

        switch(c)
        {
            case RED:
                amount += ammo.getRed();
                break;
            case BLUE:
                amount += ammo.getBlue();
                break;
            case YELLOW:
                amount += ammo.getYellow();
                break;
            default:
                amount = 0;
                break;
        }

        if(withPowers)
        {
            for (Power p : getPowers())
            {
                if (p.getColor() == c)
                    amount++;
            }
        }

        return amount;
    }

    /**
     *
     * @return Powers in the player's hand
     */
    public List<Power> getPowers()
    {
        List<Power> list = new ArrayList<>(Arrays.asList(powers));
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

    /**
     * Adds points when killing an enemy
     * @param num Number of points to add
     */
    public void addPoints(int num)
    {
        if(num > 0)
        {
            points += num;
        }
    }

    /**
     * Adds a skull, used when the user gets killed
     */
    public void addSkull()
    {
        skulls++;
    }

    /**
     * Return the player connection
     * @return the connection
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * Set the connection
     * @param conn the player connection
     */
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void clearForView(){
        for(int i=0; i<maxWeaponsNumber; i++)
            if(weapons[i] != null && weapons[i].isLoaded())
                weapons[i] = null;

        this.powers = null;
        this.conn = null;
    }

    /**
     * Tells if the player has already been spawned
     * @return True if the player is already on the map, false if it has to be spawned
     */
    public boolean isSpawned()
    {
        return spawned;
    }

    /**
     * Sets the 'spawned' state of the player
     * @param s Value to be set
     */
    public void setSpawned(boolean s)
    {
        spawned = s;
    }

    /**
     * Puts a power in the temporary used powers list
     * @param p Power to throw
     */
    public void throwPower(Power p)
    {
        usedPowers.add(p);
    }

    /**
     * Cleans the used powers list by scrapping them in the correct deck
     * @param powersDeck Deck in which the power cards will be scrapped
     */
    public void cleanUsedPowers(EndlessDeck<Power> powersDeck)
    {
        for(Power p: usedPowers)
        {
            powersDeck.scrapCard(p);
        }

        usedPowers.clear();
    }

    /**
     * Tells whether the player is using the frenzy board or not
     * @return True if it is in use, false otherwise
     */
    public boolean getFrenzyBoard()
    {
        return frenzyBoard;
    }

    /**
     * Sets the value of frenzyBoard
     * @param val True if the player is using the frenzy board, false otherwise
     */
    public void setFrenzyBoard(boolean val)
    {
        frenzyBoard = val;
    }

    public MyPlayerView getFullView()
    {
        return new MyPlayerView(
                nick,
                character,
                getWeapons(),
                frenzyBoard,
                Arrays.asList(receivedDamage),
                receivedMarks,
                skulls,
                position,
                ammo.getView(),
                points,
                getPowers()
        );
    }

    public PlayerView getView()
    {
        return new PlayerView(
                nick,
                character,
                getWeapons().stream().filter(w->!w.isLoaded()).collect(Collectors.toList()),
                frenzyBoard,
                Arrays.asList(receivedDamage),
                receivedMarks,
                skulls,
                position,
                ammo.getView(),
                points
        );
    }
}
