package it.polimi.ingsw.model;

/**
 * One box of the killshot track. It can contain a skull or players' tokens
 */
public class Kill {
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
     * @param killer Player that made the kill
     */
    public void setKiller(Player killer)
    {
        if(skull == true && overkill == false)
        {
            skull = false;
            this.killer = killer;
        }
        else
        {
            //TODO: add error
        }
    }

    /**
     * Sets the overkill attribute
     * @param overkill Presence of the 12th damage
     */
    public void setOverkill(boolean overkill)
    {
        if(skull == false && killer != null)
        {
            this.overkill = overkill;
        }
        else
        {
            //TODO: add error
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
}
