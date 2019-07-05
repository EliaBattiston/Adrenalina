package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.controller.Configuration;
import it.polimi.ingsw.controller.GamePhase;
import it.polimi.ingsw.controller.Match;
import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the match view class
 */
public class MatchViewTest {
    /**
     * Check of matchView information hiding
     */
    @Test
    public void checkMatchView() {
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

            //Check of general infos, in that case the match is in initialising status
            assertEquals(m.getGame().getView().getPlayers().size(), m.getView(p).getGame().getPlayers().size());
            assertEquals(m.getGame().getView().getMap().getId(), m.getView(p).getGame().getMap().getId());
            assertEquals(p.getNick(), m.getView(p).getMyPlayer().getNick());
            assertNull(m.getView(p).getActive());
            Assertions.assertEquals(GamePhase.INITIALIZING, m.getView(p).getPhase());
            Assertions.assertEquals(Configuration.getInstance().getPlayerTurnSeconds(), m.getView(p).getTimeForAction());

        }
        catch (FileNotFoundException ignore) {
            fail();
        }
    }
}
