package it.polimi.ingsw.clientmodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test the cell view class
 */
public class CellViewTest {

    /**
     * Tests of the getters methods of the CellView class
     */
    @Test
    public void testGetters(){
        List<PlayerView> players = new ArrayList<>();
        players.add(new PlayerView("1", Fighter.DSTRUTTOR3, null, false, null, null, 5, null, null, 0));

        Side[] sides = new Side[4];
        sides[Direction.NORTH.ordinal()] = Side.WALL;
        sides[Direction.EAST.ordinal()] = Side.DOOR;
        sides[Direction.SOUTH.ordinal()] = Side.DOOR;
        sides[Direction.WEST.ordinal()] = Side.NOTHING;

        CellView c = new RegularCellView(players, 2, sides, null);

        assertTrue(c.getPawns().contains(players.get(0)));
        assertEquals(Side.NOTHING, c.getSide(Direction.WEST));
        assertEquals(2, c.getRoomNumber());
        assertFalse(c.hasSpawn(null));
    }

    @Test
    public void testAdapter(){
        //initialize Gson
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Cell.class, new CellAdapter());
        gsonBuilder.registerTypeAdapter(CellView.class, new CellViewAdapter());
        Gson gson = gsonBuilder.create();

        //Initialize the CellView
        List<PlayerView> players = new ArrayList<>();
        players.add(new PlayerView("1", Fighter.DSTRUTTOR3, null, false, null, null, 5, null, null, 0));
        Side[] sides = new Side[4];
        sides[Direction.NORTH.ordinal()] = Side.WALL;
        sides[Direction.EAST.ordinal()] = Side.DOOR;
        sides[Direction.SOUTH.ordinal()] = Side.DOOR;
        sides[Direction.WEST.ordinal()] = Side.NOTHING;
        CellView c = new RegularCellView(players, 2, sides, null);

        String json = gson.toJson(c);
        CellView c2 = gson.fromJson(json, RegularCellView.class);

        assertEquals(c.getRoomNumber(), c2.getRoomNumber());
        assertEquals(c.getSide(Direction.WEST), c2.getSide(Direction.WEST));
    }
}
