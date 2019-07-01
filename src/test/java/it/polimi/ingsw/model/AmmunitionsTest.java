package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AmmunitionsTest {

    /**
     * Check for Ammunition edge cases
     */
    @Test
    public void checkAmmunitions() {
        Ammunitions ammo = new Ammunitions();
        assertEquals(1, ammo.getBlue());
        assertEquals(1, ammo.getRed());
        assertEquals(1, ammo.getYellow());

        assertFalse(ammo.add(Color.POWER, 2));

        assertFalse(ammo.add(Color.BLUE, 4));
        assertFalse(ammo.add(Color.RED, 4));
        assertFalse(ammo.add(Color.YELLOW, 4));

        assertFalse(ammo.add(Color.BLUE, -1));
        assertFalse(ammo.add(Color.RED, -1));
        assertFalse(ammo.add(Color.YELLOW, -1));

        assertFalse(ammo.useBlue(-1));
        assertFalse(ammo.useRed(-1));
        assertFalse(ammo.useYellow( -1));
    }

    /**
     * Check of correct instantiation of Ammunition object from another Ammunition
     */
    @Test
    public void checkAmmunitionsCopy() {
        Ammunitions ammo = new Ammunitions();

        assertTrue(ammo.add(Color.BLUE, 1));
        assertTrue(ammo.add(Color.YELLOW, 2));
        assertTrue(ammo.add(Color.RED, 3));

        Ammunitions ammoCopy = new Ammunitions(ammo);

        assertEquals(ammo.getBlue(), ammoCopy.getBlue());
        assertEquals(ammo.getRed(), ammoCopy.getRed());
        assertEquals(ammo.getYellow(), ammoCopy.getYellow());
    }
}
