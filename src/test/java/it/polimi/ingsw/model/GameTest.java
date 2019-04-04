package it.polimi.ingsw.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameTest {
    /**
     * Check Ammunitions Class for edge cases
     */
    @Test
    public void checkAmmunitionsClass()
    {
        Ammunitions a = new Ammunitions();

        //test initial value of all ammos are 1
        assertTrue(a.getRed() == 1);
        assertTrue(a.getBlue() == 1);
        assertTrue(a.getYellow() == 1);

        //Emptying ammo
        a.useRed(1);
        a.useBlue(1);
        a.useYellow(1);

        //Test that all values are now 0
        assertTrue(a.getRed() == 0);
        assertTrue(a.getBlue() == 0);
        assertTrue(a.getYellow() == 0);

        //can't get ammo if there's none
        assertFalse(a.useRed(1));
        assertFalse(a.useYellow(1));
        assertFalse(a.useBlue(1));

        //test add Blue
        a.addBlue(1);
        assertTrue(a.getBlue()==1);
        a.addBlue(1);
        assertTrue(a.getBlue()==2);
        a.addBlue(1);
        assertTrue(a.getBlue()==3);

        //TODO check this test
        //assertFalse(a.addBlue(1));
        assertTrue(a.getBlue()==3); //no more than 3 ammos per type

        //test add Red
        a.addRed(1);
        assertTrue(a.getRed()==1);
        a.addRed(1);
        assertTrue(a.getRed()==2);
        a.addRed(1);
        assertTrue(a.getRed()==3);

        //TODO check this test
        //assertFalse(a.addRed(1));
        assertTrue(a.getRed()==3); //no more than 3 ammos per type

        //test add Yellow
        a.addYellow(1);
        assertTrue(a.getYellow()==1);
        a.addYellow(1);
        assertTrue(a.getYellow()==2);
        a.addYellow(1);
        assertTrue(a.getYellow()==3);

        //TODO check this test
        //assertFalse(a.addYellow(1));
        assertTrue(a.getYellow()==3); //no more than 3 ammos per type

        //get all the ammos
        assertTrue(a.useRed(3));
        assertTrue(a.useBlue(3));
        assertTrue(a.useYellow(3));

        //can't get ammo if there's no one
        assertFalse(a.useRed(1));
        assertFalse(a.useYellow(1));
        assertFalse(a.useBlue(1));
    }

    /**
     * Check Player Class for edge cases
     */
    @Test
    public void checkPlayerClass()
    {
        Player p = new Player("Nick", "Phrase", Fighter.Dozer);

        //check constructor
        assertEquals(p.getNick(), "Nick");
        assertEquals(p.getActionPhrase(), "Phrase");
        assertEquals(p.getCharacter(), Fighter.Dozer);

        //TODO add testing on applied PlayerLambda
    }

    /**
     * Check Point Class for edge cases
     */
    @Test
    public void checkPointClass()
    {
        Point p = new Point(1,1);

        assertTrue(p.getX() == 1);
        assertTrue(p.getY() == 1);

        p.set(5,3); //TODO check bool/exception
        //it didn't change the values
        assertTrue(p.getX() == 1);
        assertTrue(p.getY() == 1);

        p.set(3,-5);
        //it didn't change the values
        assertTrue(p.getX() == 1);
        assertTrue(p.getY() == 1);

        p.set(2,3);
        //it changed the values
        assertTrue(p.getX() == 2);
        assertTrue(p.getY() == 3);
    }

    /**
     * Tests the correct functioning of Kill class that has been initialized to true
     */
    @Test
    public void TreuKillClass()
    {
        Player p = new Player("ERap320", "Yay!", Fighter.Dstruttor3);

        //Usable Kill
        Kill k = new Kill(true);
        assertTrue(k.getSkull());

        k.setKiller(p);
        assertFalse(k.getSkull());
        assertSame(k.getKiller(), p);
        assertFalse(k.getOverkill());

        k.setOverkill(true);
        assertTrue(k.getOverkill());
    }

    /**
     * Tests the correct retaining of a "without a skull" state of a false initialized Kill class
     */
    @Test
    public void FalseKillClass()
    {
        Player p = new Player("ERap320", "Yay!", Fighter.Dstruttor3);

        //Unusable Kill
        Kill k = new Kill(false);
        assertFalse(k.getSkull());

        k.setKiller(p);
        assertSame(k.getKiller(), null);
        assertFalse(k.getSkull());

        k.setOverkill(true);
        assertFalse(k.getOverkill());
    }

    /**
     * Test the behaviour of a Point instance
     */
    @Test
    public void TestPoint()
    {
        Point p = new Point(1,2);
        assertSame(p.getX(), 1);
        assertSame(p.getY(), 2);

        p.set(3,5);
        assertNotSame(p.getX(),3);
        assertNotSame(p.getY(),5);

        p.set(-2,-5);
        assertNotSame(p.getX(),-2);
        assertNotSame(p.getY(),-3);
    }
}
