package it.polimi.ingsw.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MapTest {
    /**
     * Check Cell Abstract Class for edge cases
     */
    @Test
    public void checkCellClass()
    {
        //RegularCell rc = new RegularCell( null, 3);
        //Loot l = new Loot([Color.Blue, Color.Yellow, Color.Red]); //TODO check if is possible to declare the array as here

        assertTrue(true); //TODO check if we want to check something about this class
    }

    /**
     * Check RegularCell Class for edge cases
     */
    @Test
    public void checkRegularCellClass()
    {
        RegularCell rc = new RegularCell( null, 3);
        Loot l = new Loot(new Color[]{Color.Blue, Color.Yellow, Color.Red});

        rc.refillLoot(l);

        assertTrue(rc.getLoot() == l);
        assertTrue(rc.pickLoot() == l); //now we don't have it
        assertTrue(rc.getLoot() == null);
        assertTrue(rc.pickLoot() == null);
    }
}
