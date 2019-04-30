package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.WrongPointException;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class LambdaTest {
    @Test
    public void TestEffectsLambda()
    {
        Map m = Map.jsonDeserialize(1);
        Player p = new Player("Andre", "Ciao ragazzi!", Fighter.BANSHEE);
        Player att = new Player("Ale", "Vi distrugg0o0o0o0o0o0o0o0!", Fighter.VIOLETTA);

        int d;

        //Check damage
        p.applyEffects(EffectsLambda.damage(3, att));
        d=0;
        for(int i=0; i<12; i++)
            if(p.getReceivedDamage()[i] == att)
                d++;
        assertTrue(d == 3);

        //Check marks
        p.applyEffects(EffectsLambda.marks(2, att));
        assertTrue(Collections.frequency(p.getReceivedMarks(), att) == 2);

        //Give damage (+ the old marks becomes damage)
        p.applyEffects(EffectsLambda.damage(2, att));
        d=0;
        for(int i=0; i<12; i++)
            if(p.getReceivedDamage()[i] == att)
                d++;
        assertTrue(d == (3+2+2));

        //Move the player
        try {
            p.applyEffects(EffectsLambda.move(p, new Point(2, 1), m));
            assertEquals(p.getPosition().getX(), 2);
            assertEquals(p.getPosition().getY(), 1);
            assertTrue(m.getCell(2, 1).getPawns().contains(p));
        }catch (WrongPointException ex){
            fail();
        }

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
