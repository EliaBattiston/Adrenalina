package it.polimi.ingsw.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ModelTest
{
    /**
     * Check Ammunitions Class for edge cases
     */
    @Test
    public void checkAmmunitionsClass()
    {
        Ammunitions a = new Ammunitions();

        //test initial value of all ammos as 0
        assertTrue(a.getRed() == 0);
        assertTrue(a.getBlue() == 0);
        assertTrue(a.getYellow() == 0);

        //can't get ammo if there's no one
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
        assertFalse(a.addBlue(1));
        assertTrue(a.getBlue()==3); //no more than 3 ammos per type

        //test add Red
        a.addRed(1);
        assertTrue(a.getRed()==1);
        a.addRed(1);
        assertTrue(a.getRed()==2);
        a.addRed(1);
        assertTrue(a.getRed()==3);
        assertFalse(a.addRed(1));
        assertTrue(a.getRed()==3); //no more than 3 ammos per type

        //test add Yellow
        a.addYellow(1);
        assertTrue(a.getYellow()==1);
        a.addYellow(1);
        assertTrue(a.getYellow()==2);
        a.addYellow(1);
        assertTrue(a.getYellow()==3);
        assertFalse(a.addYellow(1));
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

        //TODO add testing on applyed PlayerLambda
    }

    /**
     * Check Point Class for edge cases
     */
    @Test
    public void checkPointClass()
    {
        Point p = new Point(1,1);

        //X 0->4
        //Y 0->3
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
     * Check Deck Class for edge cases
     */
    @Test
    public void checkDeckClass()
    {
        Deck dP = new Deck<Power>();
        Deck dW = new Deck<Weapon>();
        Deck dL = new Deck<Loot>();

        //TODO add of the cards
        Power p1, p2, p3;
        dP.add(p1);
        dP.add(p2);
        dP.add(p3);
        assertTrue(dP.getCards().contains(p1));
        assertTrue(dP.getCards().contains(p2));
        assertTrue(dP.getCards().contains(p3));

        Weapon w1, w2, w3;
        dW.add(w1);
        dW.add(w2);
        dW.add(w3);
        assertTrue(dW.getCards().contains(w1));
        assertTrue(dW.getCards().contains(w2));
        assertTrue(dW.getCards().contains(w3));

        Loot l1, l2, l3;
        dL.add(l1);
        dL.add(l2);
        dL.add(l3);
        assertTrue(dL.getCards().contains(l1));
        assertTrue(dL.getCards().contains(l2));
        assertTrue(dL.getCards().contains(l3));

        //check clone
        Deck dP2 = dP.clone();
        Deck dW2 = dW.clone();
        Deck dL2 = dL.clone();
        assertTrue(dP.getCards().equals(dP2.getCards()));
        assertTrue(dW.getCards().equals(dW2.getCards()));
        assertTrue(dL.getCards().equals(dL2.getCards()));

        //check Shuffle
        dP.shuffle();
        dW.shuffle();
        dL.shuffle();
        assertFalse(dP.getCards().equals(dP2.getCards()));
        assertFalse(dW.getCards().equals(dW2.getCards()));
        assertFalse(dL.getCards().equals(dL2.getCards()));

        //Draw some cards
        assertTrue(dW.draw() == w1); //TODO or w3? what's the order?

    }

    /**
     * Check Power Class for edge cases
     */
    @Test
    public void checkPowerClass()
    {
        Action base = null; //TODO change the null
        Power p = new Power(1, "One", base, Color.Red);

        assertTrue(p.getId() == 1);
        assertTrue(p.getName().equals("One"));
        assertTrue(p.getBase() == base);
        assertTrue(p.getColor() == Color.Red;
    }

    /**
     * Check Weapon Class for edge cases
     */
    @Test
    public void checkWeaponClass()
    {
        Action base = null; //TODO change the null
        Weapon w = new Weapon(1, "WeaponOne", base, null, null, Color.Red);

        assertTrue(w.isLoaded()); //at true from the constructor

        w.setLoaded(false);
        assertFalse(w.isLoaded());

        w.setLoaded(true);
        assertTrue(w.isLoaded());
    }

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