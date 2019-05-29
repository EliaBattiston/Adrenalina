package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class RunningGameTest {

    /**
     * Run a server and some AI clients, wait until the end of the match. Check the game ends with no problems
     */
    @Test
    public void checkGameWithAI(){
        try
        {
            Match m = new Match(8);

            Player simulated;
            for(int i=0; i<5; i++)
            {
                simulated = new Player(Integer.toString(i), "Testing is hard work, but someone has to do it!", Fighter.values()[i]);
                simulated.setConn(new AIConnection());
                m.getGame().addPlayer(simulated);
            }

            Thread runner = new Thread(m);
            runner.start();

            try
            {
                runner.join();
            }
            catch(InterruptedException e)
            {
                fail();
            }

            assertTrue(true);
        }
        catch(FileNotFoundException e)
        {
            fail();
        }

    }

}