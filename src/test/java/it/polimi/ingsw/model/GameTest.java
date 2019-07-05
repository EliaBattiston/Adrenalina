package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.UsedNameException;
import it.polimi.ingsw.exceptions.WrongPointException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Tests about the correct functioning of Games
 */
public class GameTest {
    /**
     * Initialization used to prepare a test game environment to make sure weapons behave correctly
     * @param playersNumber Number of players to be instantiated to carry out the test
     * @return Resulting test game
     */
    public static Game initializeGameForTest(int playersNumber)
    {
        //Create an empty game with map 1
        Game game = new Game(5, null, null, null, null);
        try
        {
            game.loadMap(1);
        }
        catch(FileNotFoundException e)
        {
            fail();
        }

        /*
            Put the requested number of players in the game
            They need to be moved before being able to see each other on the map
            They have no weapons or powers
         */
        ArrayList<Player> players = new ArrayList<>();
        for(int i=0; i<playersNumber && i<5; i++)
        {
            players.add(new Player("p"+i, "phrase", Fighter.values()[i]));
            //Attach a test connection to the fake player to make him answer predictably
            players.get(i).setConn(new ConnectionTest());
        }

        for(Player p : players)
            game.addPlayer(p);

        return game;
    }

    /**
     * Tests the ammunitions' class constructor
     */
    @Test
    public void testAmmunitionsConstructor(){
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
    }

    /**
     * Check Ammunitions Class for edge cases
     */
    @Test
    public void checkAmmunitionsClass()
    {
        Ammunitions a = new Ammunitions();

        //Emptying ammo
        a.useRed(1);
        a.useBlue(1);
        a.useYellow(1);

        //can't get ammo if there's none
        assertFalse(a.useRed(1));
        assertFalse(a.useYellow(1));
        assertFalse(a.useBlue(1));

        //test add BLUE
        a.addBlue(1);
        assertTrue(a.getBlue()==1);
        a.addBlue(1);
        assertTrue(a.getBlue()==2);
        a.addBlue(1);
        assertTrue(a.getBlue()==3);

        assertTrue(a.getBlue()==3); //no more than 3 ammos per type

        //test add RED
        a.addRed(1);
        assertTrue(a.getRed()==1);
        a.addRed(1);
        assertTrue(a.getRed()==2);
        a.addRed(1);
        assertTrue(a.getRed()==3);

        assertTrue(a.getRed()==3); //no more than 3 ammos per type

        //test add YELLOW
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

        //can't get ammo if there's none
        assertFalse(a.useRed(1));
        assertFalse(a.useYellow(1));
        assertFalse(a.useBlue(1));
    }

    /**
     * Check the constructor of the Player class
     */
    @Test
    public void checkPlayerConstructor()
    {
        Player p = new Player("Nick", "Phrase", Fighter.DOZER);

        //check constructor
        assertEquals("Nick", p.getNick());
        assertEquals("Phrase", p.getActionPhrase());
        assertEquals(Fighter.DOZER, p.getCharacter());

        //check that arrays are initially empty
        assertEquals(0, p.getWeapons().size());
        assertEquals(0, p.getPowers().size());
    }

    /**
     * Tests that the powers have been removed from the player class after calling the method clearForView
     */
    @Test
    public void testPlayerClearForView(){
        Player p = new Player("Andre", "try", Fighter.DSTRUTTOR3);
        p.applyEffects((damage, marks, position, weapons, powers, ammo) -> {
            powers[0] = new Power(1, "try", null, null);
        });

        assertEquals(p.getPowers().size(), 1);

        p.clearForView();

        assertNull(p.getPowers());
    }

    /**
     * Check how the player gets modified with PlayerLambdas
     */
    @Test
    public void checkPlayerLambda()
    {
        Player p = new Player("Nick", "Phrase", Fighter.DOZER);

        //Setting up an enemy, a weapon and a power
        Player b = new Player("DamageGiver", "Phrase2", Fighter.DOZER);
        Action doNothing = new Action("Do nothing", "Does nothing", new ArrayList<Color>(), null);
        Weapon weapon = new Weapon(0, "Gun","desc", doNothing, null, null, Color.RED);
        Power power = new Power(1, "POWER", doNothing, Color.BLUE);

        //Check setting of values with lambda
        p.applyEffects( (damage, marks, position, weapons, powers, ammo) -> {
            damage[0] = b.getNick();

            marks.add(b.getNick());
            marks.add(b.getNick());

            try
            {
                position.set(3,2);
            }
            catch(WrongPointException e)
            {
                fail();
            }

            weapons[0] = weapon;
            powers[1] = power;

            ammo.addRed(2);
            ammo.addBlue(3);
            ammo.useBlue(1);

        } );

        assertSame(p.getReceivedDamage()[0], b.getNick());
        assertSame(null, p.getReceivedDamage()[1]);
        assertEquals(2, p.getReceivedMarks().size());
        assertTrue(p.getReceivedMarks().contains(b.getNick()));
        assertEquals(2, Collections.frequency(p.getReceivedMarks(), b.getNick()));
        assertEquals(3, p.getPosition().getX());
        assertEquals(2, p.getPosition().getY());
        assertTrue(p.getWeapons().contains(weapon));
        assertTrue(p.getPowers().contains(power));
        assertEquals(3, p.getAmmo(Color.RED, false));
        assertEquals(2, p.getAmmo(Color.BLUE, false));
    }

    /**
     * Check Point Class for edge cases
     */
    @Test
    public void checkPointClass()
    {
        Point p = null;

        //Correct instantiation of a point
        try {
            p = new Point(1, 1);
        } catch(WrongPointException e) {
            fail();
        }
        assertEquals(1, p.getX());
        assertEquals(1, p.getY());

        //x value out of bounds, it shouldn't change the value from before
        try {
            p.set(5, 3);
            fail();
        } catch(WrongPointException e)
        {
            ;
        }
        assertEquals(1, p.getX());
        assertEquals(1, p.getY());

        //Negative y value, it shouldn't change the value from before
        try
        {
            p.set(3, -5);
            fail();
        }
        catch(WrongPointException e)
        {
            ;
        }
        assertEquals(1, p.getX());
        assertEquals(1, p.getY());

        //Correct setting
        try
        {
            p.set(3,2);
        }
        catch(WrongPointException e)
        {
            fail();
        }
        assertEquals( 3, p.getX());
        assertEquals( 2, p.getY());
    }

    /**
     * Tests the correct functioning of a Kill that has a skull
     */
    @Test
    public void KillClassWithSkull()
    {
        Player p = new Player("ERap320", "Yay!", Fighter.DSTRUTTOR3);

        //Usable Kill
        Kill k = new Kill(true);
        assertTrue(k.getSkull());

        k.setKiller(p, false);
        assertFalse(k.getSkull());
        assertSame(p, k.getKiller());
        assertFalse(k.getOverkill());
        assertTrue(k.isUsed());
    }

    /**
     * Tests the correct retaining of a "without a skull" state of a false initialized Kill class
     */
    @Test
    public void FalseKillClass()
    {
        Player p = new Player("ERap320", "Yay!", Fighter.DSTRUTTOR3);

        //Usable Kill
        Kill k = new Kill(false);
        assertFalse(k.getSkull());
        assertNull(k.getKiller());

        k.setKiller(p, false);
        assertFalse(k.getSkull());
        assertNotSame(k.getKiller(), p);
        assertFalse(k.getOverkill());
        assertFalse(k.isUsed());
    }

    /**
     * Test the correct initialization of a Game and the addition of players
     */
    @Test
    public void TestGameClass()
    {
        Game g = new Game(5, new Map(), null, null, null);
        Player p1 = new Player("ERap320", "Yay!", Fighter.DSTRUTTOR3);
        Player p1_doubledNick = new Player("ERap320", "Yuy!", Fighter.DOZER);
        Player p2 = new Player("ERap321", "Yay!", Fighter.DSTRUTTOR3);
        Player p3 = new Player("ERap322", "Yay!", Fighter.DSTRUTTOR3);
        Player p4 = new Player("ERap323", "Yay!", Fighter.DSTRUTTOR3);
        Player p5 = new Player("ERap324", "Yay!", Fighter.DSTRUTTOR3);
        Player p6 = new Player("ERap325", "Yay!", Fighter.DSTRUTTOR3);

        //Check if there are no players at creation
        assertEquals( 0, g.getPlayers().size());

        //Test addition of a single player
        try
        {
            assertTrue(g.addPlayer(p1));
            assertEquals( 1, g.getPlayers().size());
            g.addPlayer(p1);
            assertEquals( 1, g.getPlayers().size());
        }
        catch(UsedNameException e)
        {
            fail();
        }

        //Test the addition of another player with the same name
        try
        {
            g.addPlayer(p1_doubledNick);
            fail();
        }
        catch(UsedNameException e)
        {
            ;
        }
        assertEquals(1, g.getPlayers().size());

        //Test the addition of too many players (More than 5)
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
     * Tests the removePlayer methods
     */
    @Test
    public void testRemovePlayerFromGame(){
        Game g =  new Game(5, new Map(), null, null, null);
        Player p1 = new Player("ERap320", "Yay!", Fighter.DSTRUTTOR3);

        g.addPlayer(p1);
        assertTrue(g.getPlayers().contains(p1));
        g.removePlayer(p1);
        assertFalse(g.getPlayers().contains(p1));

        g.addPlayer(p1);
        assertTrue(g.getPlayers().contains(p1));
        g.removePlayer(p1.getNick());
        assertFalse(g.getPlayers().contains(p1));
    }
}
