package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.controller.Match;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class MapViewTest {
    /**
     * Check of mapView, spawnCellView and regularCellView information hiding
     */
    @Test
    public void checkMapView() {
        try {
            //Initialization of the needed classes with random (but coherent) parameters
            Match m = new Match(5);
            Player p = new Player("Pippo", "", Fighter.DSTRUTTOR3);
            Player q = new Player("Pluto", "", Fighter.DSTRUTTOR3);
            m.getGame().loadMap(1);
            //In map 1, cell (0, 0) corresponds to a regular cell and cell (2, 0) to a spawn cell
            m.getGame().getMap().getCell(0,0).refill(m.getGame());
            m.getGame().getMap().getCell(2,0).refill(m.getGame());

            MapView view = m.getGame().getMap().getView();

            assertEquals(m.getGame().getMap().getCell(0,0).getView().getRoomNumber(), view.getCell(0,0).getRoomNumber());
            assertEquals(m.getGame().getMap().getCell(2,0).getView().getRoomNumber(), view.getCell(2,0).getRoomNumber());

            //Out of bound cells
            assertNull(view.getCell(-1,0));
            assertNull(view.getCell(-1,-1));
            assertNull(view.getCell(-1,4));
            assertNull(view.getCell(1,4));
            assertNull(view.getCell(4,4));

            RegularCell rc = (RegularCell) m.getGame().getMap().getCell(0,0);
            SpawnCell sc = (SpawnCell) m.getGame().getMap().getCell(2,0);

            RegularCellView rv = (RegularCellView) view.getCell(0,0);
            SpawnCellView sv = (SpawnCellView) view.getCell(2,0);

            //Check of regular cell and spawn cell particularities
            assertEquals(rc.getLoot(), rv.getLoot());
            assertEquals(sc.getWeapons(), sv.getWeapons());

            //check the spawn color of the cell
            assertTrue(sv.hasSpawn(Color.BLUE));
            assertFalse(sv.hasSpawn(Color.YELLOW));
            assertFalse(sv.hasSpawn(Color.RED));

        }
        catch (FileNotFoundException ignore) {
            fail();
        }
    }
}
