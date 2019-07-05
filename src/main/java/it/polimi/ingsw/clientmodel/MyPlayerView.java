package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Power;
import it.polimi.ingsw.model.Weapon;

import java.io.Serializable;
import java.util.List;

/**
 * This class contains all the information needed by the client to display its user's player information.
 * It contains information only available to the player whose perspective is reflected in the MatchView.
 */
public class MyPlayerView extends PlayerView implements Serializable
{
    private List<Power> powers;

    public MyPlayerView(String nick, Fighter character, List<Weapon> weapons, boolean frenzyBoard,
                        List<String> receivedDamage, List<String> receivedMarks, int skullsNum, Point position,
                        AmmoView av, int points, List<Power> powers)
    {
        super(nick, character, weapons, frenzyBoard, receivedDamage, receivedMarks, skullsNum, position, av, points);
        this.powers = powers;
    }

    @Override
    public List<Power> getPowers()
    {
        return powers;
    }
}
