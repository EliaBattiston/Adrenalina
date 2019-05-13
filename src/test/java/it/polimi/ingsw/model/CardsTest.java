package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import it.polimi.ingsw.exceptions.ArrayDimensionException;
import it.polimi.ingsw.exceptions.EmptyDeckException;

public class CardsTest {
    /**
     * Check POWER Class: check the functionality of the constructor
     */
    @Test
    public void checkPowerClass()
    {
        Action base = new Action("MyAction","description", new ArrayList<>(), null);
        Power p = new Power(1, "One", base, Color.RED);

        assertTrue(p.getId() == 1);
        assertTrue(p.getName().equals("One"));
        assertTrue(p.getBase() == base);
        assertTrue(p.getColor() == Color.RED);
    }

    /**
     * Check Loot Class: check the functionality of the constructor
     */
    @Test
    public void checkLootClass()
    {
        Color[] c = new Color[]{Color.RED, Color.BLUE, Color.POWER};
        try {
            Loot l = new Loot(c);
            assertTrue(l.getContent() == c);

            assertTrue(l.getContentAsString().equals("RBP"));
        }catch (ArrayDimensionException e){
            fail();
        }

        Color[] c2 = new Color[]{Color.RED, Color.BLUE};
        try {
            Loot l = new Loot(c2);
            fail();
        }catch (ArrayDimensionException e){
            assertTrue(true);
        }

    }

    /**
     * Check Weapon Class: check the functionality of the constructor and the function of the attribute loaded
     */
    @Test
    public void checkWeaponClass()
    {
        Action base = new Action("MyAction","description", new ArrayList<>(), null);
        Weapon w = new Weapon(1, "WeaponOne", "desc",base, null, null, Color.RED);

        assertTrue(w.getName().equals("WeaponOne"));
        assertTrue(w.getAdditional() == null);
        assertTrue(w.getAlternative() == null);
        assertTrue(w.getBase() == base);
        assertTrue(w.getColor() == Color.RED);
        assertTrue(w.getId() == 1);
        assertTrue(w.isLoaded()); //at true from the constructor

        w.setLoaded(false);
        assertFalse(w.isLoaded());

        w.setLoaded(true);
        assertTrue(w.isLoaded());
        w.setLoaded(true);
        assertTrue(w.isLoaded());
    }

    /**
     * Check Deck Class: check it can contain the cards and give them correctly, check the shuffle function and the behavior when it's empty
     */
    @Test
    public void checkDeckClass()
    {
        Deck<Power> dP = new Deck<>();
        Power p1, p2, p3;

        p1 = new Power(1, "Pow1", null, Color.RED);
        p2 = new Power(2, "Pow2", null, Color.BLUE);
        p3 = new Power(3, "Pow3", null, Color.YELLOW);
        dP.add(p1);
        dP.add(p2);
        dP.add(p3);

        //Check that the cards are inside
        assertTrue(dP.getCards().contains(p1));
        assertTrue(dP.getCards().contains(p2));
        assertTrue(dP.getCards().contains(p3));

        //check the clone function
        Deck<Power> dP2 = dP.clone();
        assertTrue(dP.getCards().equals(dP2.getCards()));

        //check the shuffle function: try tree times to shuffle, if none of them change the order, there's probably a problem
        ArrayList<Power> powsConn = (ArrayList<Power>) dP.getCards();
        ArrayList<Power> pows = (ArrayList<Power>) powsConn.clone();
        dP.shuffle();
        if(!dP.getCards().equals(pows))
            assertTrue(true);
        else{
            dP.shuffle();
            if(!dP.getCards().equals(pows))
                assertTrue(true);
            else{
                dP.shuffle();
                if(!dP.getCards().equals(pows))
                    assertTrue(true);
                else
                    fail();
            }
        }

        //check the function of the draw -> use the cloned deck because dP has been shuffled
        try {
            assertTrue(dP2.draw() == p1);
            assertTrue(dP2.draw() == p2);
            assertTrue(dP2.draw() == p3);
        }catch(EmptyDeckException e){
            fail(); //enter here if there's a problem in the try
        }

        try {
            dP2.draw();

            fail(); //arrives here if there's a new card
        }catch(EmptyDeckException e){
            assertTrue(true);
        }
    }

    /**
     * Check EndlessDeck Class for edge cases extended from the ones already tested for its parent class Deck
     */
    @Test
    public void checkEndlessDeckClass()
    {
        EndlessDeck<Loot> dL = new EndlessDeck<>();

        Loot l1, l2, l3;
        try {
            l1 = new Loot(new Color[]{Color.RED, Color.BLUE, Color.POWER});
            l2 = new Loot(new Color[]{Color.RED, Color.YELLOW, Color.POWER});
            l3 = new Loot(new Color[]{Color.BLUE, Color.BLUE, Color.POWER});

            dL.add(l1);
            dL.add(l2);
            dL.add(l3);
            assertTrue(dL.getCards().contains(l1));
            assertTrue(dL.getCards().contains(l2));
            assertTrue(dL.getCards().contains(l3));
        }catch(ArrayDimensionException e){
            fail();
        }

        //draw the cards and check the EndlessDeck functionality is fine
        try {
            dL.scrapCard(dL.draw());
            dL.scrapCard(dL.draw());
            dL.scrapCard(dL.draw());
            dL.scrapCard(dL.draw());
            dL.scrapCard(dL.draw());
            dL.scrapCard(dL.draw());
            dL.scrapCard(dL.draw());
        }catch (EmptyDeckException e){
            fail();
        }
    }
}
