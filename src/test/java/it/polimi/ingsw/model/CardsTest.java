package it.polimi.ingsw.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CardsTest {
    /**
     * Check Deck Class for edge cases
     */
    @Test
    public void checkDeckClass()
    {
        Deck dP = new Deck<Power>();
        Deck dW = new Deck<Weapon>();
        Deck dL = new Deck<Loot>();

        //TODO: correctly initialize the cards
        Power p1, p2, p3;
        /*dP.add(p1);
        dP.add(p2);
        dP.add(p3);
        assertTrue(dP.getCards().contains(p1));
        assertTrue(dP.getCards().contains(p2));
        assertTrue(dP.getCards().contains(p3));*/

        Weapon w1, w2, w3;
        /*dW.add(w1);
        dW.add(w2);
        dW.add(w3);
        assertTrue(dW.getCards().contains(w1));
        assertTrue(dW.getCards().contains(w2));
        assertTrue(dW.getCards().contains(w3));*/

        Loot l1, l2, l3;
        /*dL.add(l1);
        dL.add(l2);
        dL.add(l3);
        assertTrue(dL.getCards().contains(l1));
        assertTrue(dL.getCards().contains(l2));
        assertTrue(dL.getCards().contains(l3));*/

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
        //assertTrue(dW.draw() == w1); //TODO or w3? what's the order?

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
        assertTrue(p.getColor() == Color.Red );
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
}
