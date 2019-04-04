package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Activities {
    private Action playPower;
    private List<Action> base;
    private List<Action> adrenalin3;
    private List<Action> adrenalin6;
    private List<Action> frenzyBefore;
    private List<Action> frenzyAfter;
    private static Activities instance;

    /**
     * instantiates the singleton object with the default game actions
     */
    private Activities() {
        //TODO: implement actions that can be made during the game
        base = new ArrayList<Action>();
        adrenalin3 = new ArrayList<Action>();
        adrenalin6 = new ArrayList<Action>();
        frenzyBefore = new ArrayList<Action>();
        frenzyAfter = new ArrayList<Action>();

        /**
         * Implementation of all the possible actions
         */
        ArrayList<Color> cost = new ArrayList<Color>();
        cost.add(Color.Blue);
        cost.add(Color.Yellow);
        cost.add((Color.Red));
        playPower = new Action("PlayPower", "Play Power card", null, null);
        Action act = new Action("SimpleAction", "Simple action", cost, null);
        Action adr3 = new Action("Simple3", "Simple action", cost, null);
        Action adr6 = new Action("Simple6", "Simple action", cost, null);
        Action frA = new Action("SimpleFrenzyA", "Simple action", cost, null);
        Action frB = new Action("SimpleFrenzyB", "Simple action", cost, null);

        base.add(act);
        base.add(act);
        base.add(act);
        adrenalin3.add(adr3);
        adrenalin6.add(adr6);

        frenzyBefore.add(frB);
        frenzyBefore.add(frB);

        frenzyAfter.add(frA);

    }

    /**
     * returns the instanced Activities object
     * @return instanced Activities object
     */
    public static Activities getInstance() {
        if(instance == null) {
            instance = new Activities();
        }
        return instance;
    }

    /**
     * returns a list with all the possible actions a player can do in the turn
     * @param damage value of the actual damage of the active player
     * @param frenzy flag for the frenzy mode
     * @param beforeFirst flag indicating if the player is before or after the first player of the game (in frenzy mode)
     * @return a list of posslbe actions
     */
    public List<Action> getAvailable(int damage, boolean frenzy, boolean beforeFirst) {
        ArrayList<Action> returnList = new ArrayList<Action>();
        if(!frenzy) {
            returnList.addAll(base);
            if(damage >= 3) {
                returnList.addAll(adrenalin3);
                if(damage >= 6) {
                    returnList.addAll(adrenalin6);
                }
            }
        }
        else {
            if(beforeFirst) {
                returnList.addAll(frenzyBefore);
            }
            else {
                returnList.addAll(frenzyAfter);
            }
        }

        returnList.add(playPower);

        return returnList;
    }
}
