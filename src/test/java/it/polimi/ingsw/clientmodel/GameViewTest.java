package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.controller.Match;
import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the game view class
 */
public class GameViewTest {
    /**
     * Check of gameView information hiding
     */
    @Test
    public void checkGameView() {
        try {
            //Initialization of the needed classes with random (but coherent) parameters
            Match m = new Match(5);
            Player p = new Player("Pippo", "", Fighter.DSTRUTTOR3);
            Player q = new Player("Pluto", "", Fighter.DSTRUTTOR3);
            m.getGame().loadMap(1);
            m.getGame().addPlayer(p);
            m.getGame().addPlayer(q);

            List<PlayerView> list = new ArrayList<>();
            list.add(p.getView());
            list.add(p.getView());

            //Other map parameters are checked in the corresponding class test
            assertEquals(m.getGame().getMap().getId(), m.getGame().getView().getMap().getId());

            //Check of the same size of the Player lists and chekout of the corespondency of each element of the first list to the second list equals to check that all and only the elements of the first list are in the second list
            List<PlayerView> viewList = m.getGame().getView().getPlayers();
            assertEquals(list.size(), viewList.size());

            for (PlayerView pv: list) {
                boolean found = false;
                for(PlayerView pvv: viewList) {
                    if(pv.getNick().equals(pvv.getNick()))
                        found = true;
                }
                assertTrue(found);
            }

        }
        catch (FileNotFoundException ignore) {
            fail();
        }
    }
}
