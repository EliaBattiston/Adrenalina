package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.WrongPointException;

import java.util.Collections;

/**
 * Tests about lambda functions used for the main logic of activities, weapons and effects on players
 */
public class LambdaTest {

    /**
     * Tests the correct dealing of damage using an EffectLambda
     */
    @Test
    public void TestDamageLambda()
    {
        Map m = Map.jsonDeserialize(1);
        Player p = new Player("Andre", "Ciao ragazzi!", Fighter.BANSHEE);
        Player att = new Player("Ale", "Vi distrugg0o0o0o0o0o0o0o0!", Fighter.VIOLETTA);

        int damages;

        //Check damage
        p.applyEffects(EffectsLambda.damage(3, att));
        damages=0;
        for(int i=0; i<12; i++)
            if(p.getReceivedDamage()[i] != null && p.getReceivedDamage()[i].equals(att.getNick()))
                damages++;
        assertEquals(3, damages);


    }

    /**
     * Tests the correct dealing of marks using an EffectLambda
     */
    @Test
    public void TestMarksLambda()
    {
        Map m = Map.jsonDeserialize(1);
        Player p = new Player("Andre", "Ciao ragazzi!", Fighter.BANSHEE);
        Player att = new Player("Ale", "Vi distrugg0o0o0o0o0o0o0o0!", Fighter.VIOLETTA);
        int damages = 0;

        //Check marks
        p.applyEffects(EffectsLambda.marks(2, att));
        assertEquals( 2, Collections.frequency(p.getReceivedMarks(), att.getNick()));

        //Test the marks don't go over the 3-per-enemy limit
        p.applyEffects(EffectsLambda.marks(2, att));
        assertEquals( 3, Collections.frequency(p.getReceivedMarks(), att.getNick()));

        //Check that the mark correctly converts to damage after another damage
        p.applyEffects(EffectsLambda.damage(2, att));
        damages=0;
        for(int i=0; i<12; i++)
            if( p.getReceivedDamage()[i] != null && p.getReceivedDamage()[i].equals(att.getNick()) )
                damages++;
        assertEquals(5, damages);
    }

    /**
     * Tests the correct movement of a player using an EffectLambda
     */
    @Test
    public void TestMoveLambda()
    {
        Map m = Map.jsonDeserialize(1);
        Player p = new Player("Andre", "Ciao ragazzi!", Fighter.BANSHEE);

        //Move the player
        try {
            p.applyEffects(EffectsLambda.move(p, new Point(2, 1), m));
            assertEquals(2, p.getPosition().getX());
            assertEquals(1, p.getPosition().getY());
            assertTrue(m.getCell(2, 1).getPawns().contains(p));
        }catch (WrongPointException ex){
            fail();
        }

        //Move the player again
        try {
            p.applyEffects(EffectsLambda.move(p, new Point(3, 2), m));
            assertEquals(p.getPosition().getX(),3);
            assertEquals(p.getPosition().getY(),2);
            assertFalse(m.getCell(2, 1).getPawns().contains(p));
            assertTrue(m.getCell(3,2).getPawns().contains(p));
        }catch (WrongPointException ex){
            fail();
        }
    }

}
