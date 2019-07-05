package it.polimi.ingsw.clientmodel;

import java.io.Serializable;

/**
 * View version of the Ammo Class
 */
public class AmmoView implements Serializable
{
    private int red;
    private int blue;
    private int yellow;

    public AmmoView(int red, int blue, int yellow){
        this.red = red;
        this.blue = blue;
        this.yellow = yellow;
    }

    public int getRed()
    {
        return red;
    }

    public int getBlue()
    {
        return blue;
    }

    public int getYellow()
    {
        return yellow;
    }
}
