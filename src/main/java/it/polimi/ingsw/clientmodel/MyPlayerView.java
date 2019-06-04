package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Power;
import it.polimi.ingsw.model.Weapon;

import java.io.Serializable;
import java.util.List;

public class MyPlayerView extends PlayerView implements Serializable
{
    private AmmoView ammo;

    private List<Power> powers;

    public MyPlayerView(String nick, Fighter character, List<Weapon> weapons, boolean frenzyBoard,
                        List<String> receivedDamage, List<String> receivedMarks, int skullsNum, Point position,
                        List<Power> powers, AmmoView av)
    {
        super(nick, character, weapons, frenzyBoard, receivedDamage, receivedMarks, skullsNum, position);
        this.ammo = av;
        this.powers = powers;
    }

    public AmmoView getAmmo()
    {
        return ammo;
    }

    @Override
    public List<Power> getPowers()
    {
        return powers;
    }
}
