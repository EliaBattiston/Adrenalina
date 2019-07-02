package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.controller.Match;
import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class KillViewTest {
    /**
     * Check of killView information hiding
     */
    @Test
    public void checkKillView() {
        try {
            //Initialization of the needed classes with random (but coherent) parameters
            Match m = new Match(5);
            Player p = new Player("Pippo", "", Fighter.DSTRUTTOR3);
            m.getGame().loadMap(1);
            m.getGame().getSkulls()[0].setKiller(p, false);
            m.getGame().getSkulls()[1].setKiller(p, true);

            List<KillView> view = m.getGame().getView().getSkullsBoard();

            //There are always 8 elements (coherent with the maximum number of skulls in a game)
            assertEquals(8, view.size());
            //In this case we use only 5 skulls, so kill 6 to 8 are unused
            assertFalse(view.get(7).isUsed());
            assertTrue(view.get(3).isUsed());
            assertTrue(view.get(1).isUsed());

            assertTrue(view.get(1).getOverkill());
            assertFalse(view.get(0).getOverkill());
            assertFalse(view.get(2).getOverkill());

            assertEquals(p.getView().getNick(), view.get(0).getKiller().getNick());
            assertEquals(p.getView().getNick(), view.get(1).getKiller().getNick());
            assertNull(view.get(2).getKiller());

            assertFalse(view.get(0).getSkull());
            assertTrue(view.get(2).getSkull());
            assertFalse(view.get(6).getSkull());

        }
        catch (FileNotFoundException ignore) { ; }
    }
}
