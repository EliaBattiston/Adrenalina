package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests about the correct execution of matches with simulated players
 */
public class RunningGameTest {

    /**
     * Run a game with 3 AI players and tests it ends without generating issues
     */
    @Test
    public void testThreePlayers(){
        new Thread(()->
                checkGameWithAI(3)
        ).run();
        new Thread(()->
                checkGameWithAI(3)
        ).run();
    }

    /**
     * Run a game with 4 AI players and tests it ends without generating issues
     */
    @Test
    public void testFourPlayers(){
        new Thread(()->
                checkGameWithAI(4)
        ).run();
        new Thread(()->
                checkGameWithAI(4)
        ).run();
    }

    /**
     * Run a game with 5 AI players and tests it ends without generating issues
     */
    @Test
    public void testFivePlayers(){
        new Thread(()->
                checkGameWithAI(5)
        ).run();
        new Thread(()->
                checkGameWithAI(5)
        ).run();
    }

    /**
     * Run a server and some AI clients, wait until the end of the match. Check the game ends with no problems
     */
    private void checkGameWithAI(int numOfPlayers){
        try {
            int skullsNum = ((int)(Math.random()*4))+5;
            Match m = new Match(skullsNum);

            Player simulated;
            for(int i=0; i<numOfPlayers; i++) {
                simulated = new Player(Integer.toString(i +1), "Testing is hard work, but someone has to do it!", Fighter.values()[i]);
                simulated.setConn(new AIConnection());
                m.getGame().addPlayer(simulated);
            }

            Thread runner = new Thread(m);
            runner.start();

            try {
                runner.join();
            } catch(InterruptedException e) {
                fail();
            }

            System.out.println("\n\n\u001B[32mPartita completata con successo\u001B[0m\n\n\n\n");
            assertTrue(true);
        } catch(FileNotFoundException e) {
            fail();
        }
    }
}