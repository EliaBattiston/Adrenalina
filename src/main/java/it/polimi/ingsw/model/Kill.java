package it.polimi.ingsw.model;

public class Kill {
    private boolean skull;
    private Player killer;
    private boolean overkill;

    Kill(boolean hasSkull)
    {}

    public void setKiller(Player killer)
    {}

    public void setOverkill(boolean overkill)
    {}

    public boolean getSkull() {
        return skull;
    }

    public Player getKiller() {
        return killer;
    }

    public boolean getOverkill() {
        return overkill;
    }
}
