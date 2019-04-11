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
        base = new ArrayList<>();
        adrenalin3 = new ArrayList<>();
        adrenalin6 = new ArrayList<>();
        frenzyBefore = new ArrayList<>();
        frenzyAfter = new ArrayList<>();

        /**
         * Implementation of all the possible actions
         */
        ArrayList<Color> cost = new ArrayList<>();
        cost.add(Color.BLUE);
        cost.add(Color.YELLOW);
        cost.add((Color.RED));
        playPower = new Action("PlayPower", "Play POWER card", null, null);
        Action act = new Action("SimpleAction", "Simple action 1", cost, null);
        Action adr3 = new Action("Simple3", "Simple action 2", cost, null);
        Action adr6 = new Action("Simple6", "Simple action 3", cost, null);
        Action frA = new Action("SimpleFrenzyA", "Simple action 4", cost, null);
        Action frB = new Action("SimpleFrenzyB", "Simple action 5", cost, null);

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
        ArrayList<Action> returnList = new ArrayList<>();
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
