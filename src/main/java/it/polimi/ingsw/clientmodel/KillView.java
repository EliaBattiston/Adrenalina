package it.polimi.ingsw.clientmodel;

import java.io.Serializable;

/**
 * View version of the Kill Class
 */
public class KillView implements Serializable
{
    private boolean skull;

    private PlayerView killer;

    private boolean overkill;

    public KillView(boolean skull, PlayerView killer, boolean overkill)
    {
        this.skull = skull;
        this.killer = killer;
        this.overkill = overkill;
    }

    /**
     * Tells if the kill had a skull at the beginning or if it unused
     * @return True if the kill il used, false otherwise
     */
    public boolean isUsed()
    {
        return skull || killer != null;
    }

    /**
     *
     * @return If true the Player got an overkill
     */
    public boolean getOverkill() {
        if(killer != null)
        {
            return overkill;
        }
        else //There can't be an overkill without a killer
        {
            return false;
        }
    }

    public PlayerView getKiller()
    {
        return killer;
    }

    /**
     *
     * @return If true the box has a skull
     */
    public boolean getSkull() {
        return skull;
    }
}
