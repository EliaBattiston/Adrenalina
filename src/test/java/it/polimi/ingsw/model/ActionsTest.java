package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

/**
 * Tests about the action classes
 */
public class ActionsTest {
    /**
     * Tests if the getters of the Action class are correctly implemented
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
     * Tests if the number of returned actions are consistent with the game's rules
     */
    @Test
    public void checkActivitiesNumber()
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
    }

    /**
     * Tests if the returned actions are correctly matched
     */
    @Test
    public void checkActivities()
    {
        Activities singleton = Activities.getInstance();

        /**
         * Check the correct instantiation of the singleton
         */
        assertTrue(singleton != null);

        /**
         * Check the dependency of the actions in relation with the input parameters
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

        /**
         * Adrenaline actions include base actions
         */
        assertTrue(adr3Return.containsAll(baseReturn));
        assertTrue(adr6Return.containsAll(baseReturn));
        assertTrue(adr6Return.containsAll(adr3Return));


        /**
         * Frenzy actions do NOT contain base and adrenaline actions except for the playPower one
         */
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
