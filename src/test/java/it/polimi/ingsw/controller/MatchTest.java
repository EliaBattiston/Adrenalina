package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.UsedNameException;
import it.polimi.ingsw.model.*;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static junit.framework.TestCase.*;

public class MatchTest
{
    /**
     * Test the creation of a new match, with loading from json files and replenishment of items in cells
     */
    @Test
    public void checkMatch()
    {
        Match test = null;
        try
        {
             test = new Match(6);
        }
        catch(FileNotFoundException e)
        {
            fail();
        }

        //Check if the loaded map is correct
        Map testMap = test.getGame().getMap();
        assertNull(testMap.getCell(3,0));
        assertTrue(testMap.getCell(0,0) instanceof RegularCell);
        assertTrue(testMap.getCell(0,1) instanceof SpawnCell);

        //Check if the cells are correctly replenished
        assertNotNull( ( (RegularCell)testMap.getCell(0,0) ).getLoot() );
        assertEquals( ( (SpawnCell)testMap.getCell(0,1) ).getWeapons().size(), 3 );

        //Check if the board was correctly initialized
        assertEquals(Arrays.stream(test.getGame().getSkulls()).filter(Kill::isUsed).count(), 6 );
    }

    /**
     * Test the kill elaboration
     */
    @Test
    public void checkKill()
    {
        Match test = null;
        try
        {
            test = new Match(5);
        }
        catch(FileNotFoundException e)
        {
            fail();
        }

        Player pl1 = new Player("Player1", "I'm player 1!", Fighter.DOZER);
        Player pl2 = new Player("Player2", "I'm player 2!", Fighter.VIOLETTA);
        Player pl3 = new Player("Player3", "I'm player 3!", Fighter.BANSHEE);

        assertSame(test.getGame().getPlayers().size(), 0);

        test.getGame().addPlayer(pl1);
        test.getGame().addPlayer(pl2);
        test.getGame().addPlayer(pl3);

        pl1.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
            for(int i=0; i<11; i++)
            {
                if(i<6)
                    damage[i] = pl2;
                else
                    damage[i] = pl3;
            }
        }));

        test.registerKill(pl1);

        assertEquals(Arrays.stream(test.getGame().getSkulls()).filter(Kill::isUsed).count(), 5);
        assertEquals(Arrays.stream(test.getGame().getSkulls()).filter(kill -> kill.getKiller()==pl3).count(),1);
        assertSame(test.getGame().getSkulls()[4].getKiller(), pl3);
        assertTrue(Arrays.stream(test.getGame().getSkulls()).noneMatch(Kill::getOverkill));

        assertSame(pl1.getSkulls(), 1);
        assertTrue(Arrays.stream(pl1.getReceivedDamage()).noneMatch(Objects::nonNull));

        assertSame(pl1.getPoints(), 0);
        assertSame(pl2.getPoints(), 9);
        assertSame(pl3.getPoints(), 6);
    }
}
