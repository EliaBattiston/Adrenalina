package it.polimi.ingsw.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.exceptions.ArrayDimensionException;
import it.polimi.ingsw.exceptions.UsedNameException;
import it.polimi.ingsw.exceptions.WrongPointException;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import static junit.framework.TestCase.*;

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

        assertTrue(a.getBlue()==3); //no more than 3 ammos per type

        //test add Red
        a.addRed(1);
        assertTrue(a.getRed()==1);
        a.addRed(1);
        assertTrue(a.getRed()==2);
        a.addRed(1);
        assertTrue(a.getRed()==3);

        assertTrue(a.getRed()==3); //no more than 3 ammos per type

        //test add Yellow
        a.addYellow(1);
        assertTrue(a.getYellow()==1);
        a.addYellow(1);
        assertTrue(a.getYellow()==2);
        a.addYellow(1);
        assertTrue(a.getYellow()==3);

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

        Player b = new Player("DamageGiver", "Phrase2", Fighter.Dozer);
        Action doNothing = new Action("Do nothing", "Does nothing", new ArrayList<Color>(), null);
        Weapon weapon = new Weapon(0, "Gun","desc", doNothing, null, null, Color.Red);
        Power power = new Power(1, "Power", doNothing, Color.Blue);

        //check constructor
        assertEquals(p.getNick(), "Nick");
        assertEquals(p.getActionPhrase(), "Phrase");
        assertEquals(p.getCharacter(), Fighter.Dozer);

        //check that arrays are initially empty
        assertEquals(p.getWeapons().size(), 0);
        assertEquals(p.getPowers().size(), 0);

        //check setting of values with lambda
        p.applyEffects( (damage, marks, position, weapons, powers, ammo) -> {
            damage[0] = b;

            marks.add(b);
            marks.add(b);

            try
            {
                position.set(2, 3);
            }
            catch(WrongPointException e)
            {
                ;
            }

            weapons[0] = weapon;
            powers[1] = power;

            ammo.addRed(2);
            ammo.addBlue(3);
            ammo.useBlue(1);

        } );

        assertSame(p.getReceivedDamage()[0], b);
        assertSame(p.getReceivedDamage()[1], null);
        assertEquals(p.getReceivedMarks().size(), 2);
        assertTrue(p.getReceivedMarks().contains(b));
        assertEquals(Collections.frequency(p.getReceivedMarks(), b), 2);
        assertEquals(p.getPosition().getX(), 2);
        assertEquals(p.getPosition().getY(), 3);
        assertTrue(p.getWeapons().contains(weapon));
        assertTrue(p.getPowers().contains(power));
        assertEquals(p.getAmmo(Color.Red), 3);
        assertEquals(p.getAmmo(Color.Blue), 2);
    }

    /**
     * Check Point Class for edge cases
     */
    @Test
    public void checkPointClass()
    {
        Point p = null;

        try
        {
            p = new Point(1, 1);
        }
        catch(WrongPointException e)
        { fail();};


        assertTrue(p.getX() == 1);
        assertTrue(p.getY() == 1);

        try
        {
            p.set(5, 3);
            fail();
        }
        catch(WrongPointException e)
        {
            ;
        }

        //it didn't change the values
        assertTrue(p.getX() == 1);
        assertTrue(p.getY() == 1);

        try
        {
            p.set(3, -5);
            fail();
        }
        catch(WrongPointException e)
        {
            ;
        }
        //it didn't change the values
        assertTrue(p.getX() == 1);
        assertTrue(p.getY() == 1);

        try
        {
            p.set(2, 3);
        }
        catch(WrongPointException e)
        {
            fail();
        }
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

        k.setKiller(p, false);
        assertFalse(k.getSkull());
        assertSame(k.getKiller(), p);
        assertFalse(k.getOverkill());
        assertTrue(k.isUsed());
    }

    /**
     * Tests the correct retaining of a "without a skull" state of a false initialized Kill class
     */
    @Test
    public void FalseKillClass()
    {
        Player p = new Player("ERap320", "Yay!", Fighter.Dstruttor3);

        //Usable Kill
        Kill k = new Kill(false);
        assertFalse(k.getSkull());
        assertEquals(k.getKiller(), null);

        k.setKiller(p, false);
        assertFalse(k.getSkull());
        assertNotSame(k.getKiller(), p);
        assertFalse(k.getOverkill());
        assertFalse(k.isUsed());
    }

    /**
     * Test the correct initialization of a Game and the addition of players
     */
    public void TestGameClass()
    {
        Game g = new Game(5, new Map("dummy"), null, null, null);
        Player p1 = new Player("ERap320", "Yay!", Fighter.Dstruttor3);
        Player p1_doubledNick = new Player("ERap320", "Yuy!", Fighter.Dozer);
        Player p2 = new Player("ERap321", "Yay!", Fighter.Dstruttor3);
        Player p3 = new Player("ERap322", "Yay!", Fighter.Dstruttor3);
        Player p4 = new Player("ERap323", "Yay!", Fighter.Dstruttor3);
        Player p5 = new Player("ERap324", "Yay!", Fighter.Dstruttor3);
        Player p6 = new Player("ERap325", "Yay!", Fighter.Dstruttor3);

        //Check if there are no players at creation
        assertEquals(g.getPlayers().size(), 0);

        try
        {
            assertTrue(g.addPlayer(p1));
            assertEquals(g.getPlayers().size(), 1);
            g.addPlayer(p1);
            assertEquals(g.getPlayers().size(), 1);
        }
        catch(UsedNameException e)
        {
            fail();
        }

        try
        {
            g.addPlayer(p1_doubledNick);
            fail();
        }
        catch(UsedNameException e)
        {
            ;
        }

        assertEquals(g.getPlayers().size(), 1);

        try
        {
            g.addPlayer(p2);
            g.addPlayer(p3);
            g.addPlayer(p4);
            assertTrue(g.addPlayer(p5));
            assertFalse(g.addPlayer(p6));
        }
        catch(UsedNameException e)
        {
            fail();
        }
    }

    /**
     * Tests the json serialization using Gson
     */
    @Test
    public void TestJson()
    {
        Deck<Weapon> dW = new Deck<>();
        dW.add(new Weapon(1, "WeaponOne", "desc",null, null, null, Color.Blue));
        dW.add(new Weapon(2, "WeaponOQwo", "desc",null, null, null, Color.Red));
        dW.add(new Weapon(3, "WeaponTree", "desc",null, null, null, Color.Yellow));

        EndlessDeck<Power> dP = new EndlessDeck<>();
        dP.add(new Power(1, "Pow1", null, Color.Red));
        dP.add(new Power(2, "Pow2", null, Color.Blue));
        dP.add(new Power(3, "Pow3", null, Color.Yellow));

        EndlessDeck<Loot> dL = new EndlessDeck<>();
        try {
            dL.add(new Loot(new Color[]{Color.Red, Color.Blue, Color.Power}));
            dL.add(new Loot(new Color[]{Color.Red, Color.Yellow, Color.Power}));
            dL.add(new Loot(new Color[]{Color.Blue, Color.Blue, Color.Power}));
        }catch(ArrayDimensionException e){
            fail();
        }

        Game g = new Game(5, new Map("dummy"), dP, dL, dW );
        String json = g.jsonSerialize();

        //TODO fix this with a file
       /* try {
            Game g2 = Game.jsonDeserialize(json);
            String json2 = g.jsonSerialize();

            assertEquals(json, json2);
        }catch(FileNotFoundException e){
            fail();
        }*/
    }

    @Test
    public void TestActions(){
        String json = "";

        try {
            Game g = Game.jsonDeserialize(json);
        }catch(FileNotFoundException e){
            fail();
        }
    }
}
