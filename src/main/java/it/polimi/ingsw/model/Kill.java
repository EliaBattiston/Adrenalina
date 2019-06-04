package it.polimi.ingsw.model;

import it.polimi.ingsw.clientmodel.KillView;

import java.io.Serializable;

/**
 * One box of the killshot track. It can contain a skull or players' tokens
 */
public class Kill implements Serializable {
    /**
     * If true, the kill is still to be done
     */
    private boolean skull;

    /**
     * Reference of the player who made the kill
     */
    private Player killer;

    /**
     * If true, the killer has inflicted the 12th damage to the killed player
     */
    private boolean overkill;

    /**
     *
     * @param hasSkull If true, the box is created with a skull on it
     */
    Kill(boolean hasSkull)
    {
        this.skull = hasSkull;
        this.killer = null;
        this.overkill = false;
    }

    /**
     * Sets the killer for this box
     * @param k Player that made the kill
     * @param over Presence of the 12th damage
     * @return True if the state of the kill is correct, false otherwise
     */
    public boolean setKiller(Player k, boolean over)
    {
        if(skull && this.killer == null && k != null)
        {
            skull = false;
            this.killer = k;
            this.overkill = over;

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     *
     * @return If true the box has a skull
     */
    public boolean getSkull() {
        return skull;
    }

    /**
     *
     * @return Player who got the kill
     */
    public Player getKiller() {
        return killer;
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

    /**
     * Tells if the kill had a skull at the beginning or if it unused
     * @return True if the kill il used, false otherwise
     */
    public boolean isUsed()
    {
        return skull || killer != null;
    }

    public KillView getView()
    {
        return new KillView(skull, killer==null?null:killer.getView(), overkill);
    }
}
