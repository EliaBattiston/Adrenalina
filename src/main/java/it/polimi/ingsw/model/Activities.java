package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Activities {
    private final static int firstDamageLimit = 3;
    private final static int secondDamageLimit = 6;

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
        base = new ArrayList<>();
        adrenalin3 = new ArrayList<>();
        adrenalin6 = new ArrayList<>();
        frenzyBefore = new ArrayList<>();
        frenzyAfter = new ArrayList<>();

        /**
         * Implementation of all the possible actions
         */
        playPower = new Action(
                "Potenziamento",
                "Usa una delle carte potenziamento dalla mano",
                new ArrayList<>(),
                "a-p"
        );

        base.add(new Action(
                "Correre",
                "Muovi fino a 3 quadrati",
                new ArrayList<>(),
                "a-b1"
        ));

        base.add(new Action(
                "Raccogliere",
                "Muovi fino a 1 quadrato e raccogli il contenuto della cella",
                new ArrayList<>(),
                "a-b2"
        ));

        base.add(new Action(
                "Sparare",
                "Spara con una delle tue armi",
                new ArrayList<>(),
                "a-b3"
        ));

        adrenalin3.add(new Action(
                "Raccolta adrenalinica",
                "Muovi fino a 2 quadrati e raccogli il contenuto della cella",
                new ArrayList<>(),
                "a-a1"
        ));

        adrenalin6.add(new Action(
                "Sparo adrenalinico",
                "Muovi fino a 1 quadrato e spara con una delle tue armi",
                new ArrayList<>(),
                "a-a2"
        ));

        frenzyBefore.add(new Action(
                "Sparo frenetico",
                "Muovi fino a 1 quadrato, ricarica se vuoi e poi spara",
                new ArrayList<>(),
                "a-f1"
        ));

        frenzyBefore.add(new Action(
                "Corsa frenetica",
                "Muovi fino a 4 quadrati",
                new ArrayList<>(),
                "a-f2"
        ));

        frenzyBefore.add(new Action(
                "Raccolta frenetica",
                "Muovi fino a 2 quadrati e raccogli qualcosa",
                new ArrayList<>(),
                "a-f3"
        ));

        frenzyAfter.add(new Action(
                "Sparo frenetico",
                "Muovi fino a 2 quadrati, ricarica se vuoi e poi spara",
                new ArrayList<>(),
                "a-f4"
        ));

        frenzyAfter.add(new Action(
                "Corsa frenetica",
                "Muovi fino a 3 quadrati e raccogli qualcosa",
                new ArrayList<>(),
                "a-f5"
        ));
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
            if(damage >= firstDamageLimit) {
                returnList.addAll(adrenalin3);
                if(damage >= secondDamageLimit) {
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

    public Action getPlayPower(){
        return playPower;
    }
}
