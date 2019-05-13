package it.polimi.ingsw.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class ActionsTest {
    /**
     * Check Action Class for edge cases
     */
    @Test
    public void checkActionClass()
    {
        ArrayList<Color> cost = new ArrayList<>();
        cost.add(Color.BLUE);
        cost.add(Color.YELLOW);
        cost.add((Color.RED));
        Action act = new Action("SimpleAction", "Simple action", cost, null);

        //Check of the correct instantiation of the class
        assertTrue(act.getName().equals("SimpleAction"));
        assertTrue(act.getDescription().equals("Simple action"));
        assertTrue(act.getCost() == cost);

    }

    /**
     * Check Activities Class for edge cases
     */
    @Test
    public void checkActivitiesClass()
    {
        Activities singleton = Activities.getInstance();

        /**
         * Check the correct instantiation of the singleton
         */
        assertTrue(singleton != null);

        /**
         * Check the correct number of actions with all the possible combination of input parameters
         * N.B. the number of possible actions is given by the game rules
         */
        for(int i = 0; i < 3; i++) {
            assertTrue(singleton.getAvailable(i, false, true).size() == 4);
            assertTrue(singleton.getAvailable(i, false, false).size() == 4);
            assertTrue(singleton.getAvailable(i, true, true).size() == 4);
            assertTrue(singleton.getAvailable(i, true, false).size() == 3);
        }
        for(int i = 3; i < 6; i++) {
            assertTrue(singleton.getAvailable(i, false, true).size() == 5);
            assertTrue(singleton.getAvailable(i, false, false).size() == 5);
            assertTrue(singleton.getAvailable(i, true, true).size() == 4);
            assertTrue(singleton.getAvailable(i, true, false).size() == 3);
        }
        for(int i = 6; i < 11; i++) {
            assertTrue(singleton.getAvailable(i, false, true).size() == 6);
            assertTrue(singleton.getAvailable(i, false, false).size() == 6);
            assertTrue(singleton.getAvailable(i, true, true).size() == 4);
            assertTrue(singleton.getAvailable(i, true, false).size() == 3);
        }

        /**
         * Check the dependency of the actions in relation with the input parameters
         * Adrenaline actions include base actions
         * Frenzy actions do NOT contain base and adrenaline actions except for the playPower one
         */
        ArrayList<Action> baseReturn = new ArrayList<>();
        ArrayList<Action> adr3Return = new ArrayList<>();
        ArrayList<Action> adr6Return = new ArrayList<>();
        ArrayList<Action> freBReturn = new ArrayList<>();
        ArrayList<Action> freAReturn = new ArrayList<>();

        baseReturn.addAll(singleton.getAvailable(0, false, false));
        adr3Return.addAll(singleton.getAvailable(3, false, false));
        adr6Return.addAll(singleton.getAvailable(6, false, false));
        freBReturn.addAll(singleton.getAvailable(0, true, true));
        freAReturn.addAll(singleton.getAvailable(0, true, false));

        for (int i = 0; i < baseReturn.size(); i++) {
            assertTrue(adr3Return.contains(baseReturn.get(i)));
            assertTrue(adr6Return.contains(baseReturn.get(i)));
        }

        for (int i = 0; i < adr3Return.size(); i++) {
            assertTrue(adr6Return.contains(adr3Return.get(i)));
        }

        int countA = 0;
        int countB = 0;
        for (int i = 0; i < adr6Return.size(); i++) {
            if(freBReturn.contains(adr6Return.get(i))) {
                countB++;
            }
            if(freAReturn.contains(adr6Return.get(i))) {
                countA++;
            }
        }
        assertTrue(countB == 1);
        assertTrue(countA == 1);

    }
}
