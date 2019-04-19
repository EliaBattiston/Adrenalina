package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.WrongPointException;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class LambdaTest {
    @Test
    public void TestEffectsLambda()
    {
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
            p.applyEffects(EffectsLambda.move(new Point(2, 1)));
            assertTrue(p.getPosition().getX() == 2);
            assertTrue(p.getPosition().getY() == 1);
        }catch (WrongPointException ex){
            fail();
        }
    }

}
