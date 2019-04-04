package it.polimi.ingsw.model;

/**
 * This class represents the management of ammunitions that a player already has on his board, and can be used to pay for effects
 */
public class Ammunitions {
    /**
     * Number of red ammo
     */
    private int red;
    /**
     * Number of blue ammo
     */
    private int blue;
    /**
     * Number of yellow ammo
     */
    private int yellow;

    /**
     * Creation of the class with 1 ammo for every type
     */
    Ammunitions()
    {
        this.red = 1;
        this.blue = 1;
        this.yellow = 1;
    }

    /**
     * Adds red ammunition
     * @param i Number of ammo to add
     * @return False if the value of i is wrong
     */
    public boolean addRed(int i)
    {
        //TODO: decide if returning false in some situation
        if(i>=0 && i<=3)
        {
            red = red + i;
            if (red > 3)
                red = 3;

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Adds blue ammunition
     * @param i Number of ammo to add
     * @return False if the value of i is wrong
     */
    public boolean addBlue(int i)
    {
        //TODO: decide if returning false in some situation
        if(i>=0 && i<=3)
        {
            blue = blue + i;
            if (blue > 3)
                blue = 3;

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Adds yellow ammunition
     * @param i Number of ammo to add
     * @return False if the value of i is wrong
     */
    public boolean addYellow(int i)
    {
        //TODO: decide if returning false in some other situation
        if(i>=0 && i<=3)
        {
            yellow = yellow + i;
            if (yellow > 3)
                yellow = 3;

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Uses red ammunition
     * @param i Number of ammo to be used
     * @return False if the value of i is more than the user currently has or is negative
     */
    public boolean useRed(int i)
    {
        if(i>=0 && i <= red)
        {
            red -= i;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Uses blue ammunition
     * @param i Number of ammo to be used
     * @return False if the value of i is more than the user currently has or is negative
     */
    public boolean useBlue(int i)
    {
        if(i>=0 && i <= blue)
        {
            blue -= i;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Uses yellow ammunition
     * @param i Number of ammo to be used
     * @return False if the value of i is more than the user currently has or is negative
     */
    public boolean useYellow(int i)
    {
        if(i>=0 && i <= yellow)
        {
            yellow -= i;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     *
     * @return Number of red ammo
     */
    public int getRed()
    {
        return red;
    }

    /**
     *
     * @return Number of red ammo
     */
    public int getBlue()
    {
        return blue;
    }

    /**
     *
     * @return Number of red ammo
     */
    public int getYellow()
    {
        return yellow;
    }

}
