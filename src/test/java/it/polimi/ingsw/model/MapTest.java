package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.ArrayDimensionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests about functions of the Map
 */
public class MapTest {
    /**
     * Test the cell's constructor
     */
    @Test
    public void checkCellConstructor()
    {
        Side[] sides = new Side[4];
        sides[Direction.NORTH.ordinal()] = Side.WALL;
        sides[Direction.EAST.ordinal()] = Side.WALL;
        sides[Direction.SOUTH.ordinal()] = Side.NOTHING;
        sides[Direction.WEST.ordinal()] = Side.DOOR;
        RegularCell rc = new RegularCell( sides, 3);
        SpawnCell sc = new SpawnCell(sides, 3, Color.BLUE);

        //Check the correct instantiation of both Cell child classes
        assertEquals(3, rc.getRoomNumber());
        assertEquals(3, sc.getRoomNumber());

        assertEquals(4, rc.getSides().length);
        assertEquals(4, sc.getSides().length);

        assertEquals(0, rc.getPawns().size());
        assertEquals(0, rc.getPawns().size());
    }

    /**
     * Test
     */
    @Test
    public void checkCellPawns()
    {
        Side[] sides = new Side[4];
        sides[Direction.NORTH.ordinal()] = Side.WALL;
        sides[Direction.EAST.ordinal()] = Side.WALL;
        sides[Direction.SOUTH.ordinal()] = Side.NOTHING;
        sides[Direction.WEST.ordinal()] = Side.DOOR;
        RegularCell rc = new RegularCell( sides, 3);
        SpawnCell sc = new SpawnCell(sides, 3, Color.BLUE);

        //Check the correct player insertion and deletion procedure in the cell
        Player pl = new Player("nickname", "whoaaaa", Fighter.DSTRUTTOR3);
        //Adding
        rc.addPawn(pl);
        sc.addPawn(pl);
        assertTrue(rc.getPawns().size() == 1 && rc.getPawns().contains(pl));
        assertTrue(sc.getPawns().size() == 1 && sc.getPawns().contains(pl));

        //Removing
        sc.removePawn(pl);
        rc.removePawn(pl);
        assertEquals(0, rc.getPawns().size());
        assertEquals(0, rc.getPawns().size());
    }

    /**
     * Check RegularCell Class for edge cases
     */
    @Test
    public void checkRegularCellClass()
    {
        Side[] sides = new Side[4];
        sides[Direction.NORTH.ordinal()] = Side.WALL;
        sides[Direction.EAST.ordinal()] = Side.WALL;
        sides[Direction.SOUTH.ordinal()] = Side.NOTHING;
        sides[Direction.WEST.ordinal()] = Side.DOOR;
        RegularCell rc = new RegularCell( sides, 3);
        Loot l = null;

        try
        {
            l = new Loot(new Color[]{Color.BLUE, Color.YELLOW, Color.RED});
        }
        catch(ArrayDimensionException e)
        {
            fail();
        }

        rc.refillLoot(l);

        /**
         * Check the correct functionality of the Loot filling and picking
         */
        assertSame(rc.getLoot(), l);
        assertSame(rc.pickLoot(), l); //now we don't have it
        assertNull(rc.getLoot());
        assertNull(rc.pickLoot());
    }

    /**
     * Check shared functions for checking if there is a spawn point
     */
    @Test
    public void checkSpawn()
    {
        Side[] sides = new Side[4];
        sides[Direction.NORTH.ordinal()] = Side.WALL;
        sides[Direction.EAST.ordinal()] = Side.WALL;
        sides[Direction.SOUTH.ordinal()] = Side.NOTHING;
        sides[Direction.WEST.ordinal()] = Side.DOOR;
        RegularCell rc = new RegularCell( sides, 3);
        SpawnCell sc = new SpawnCell(sides, 3, Color.BLUE);

        assertFalse(sc.hasSpawn(Color.YELLOW));
        assertFalse(sc.hasSpawn(Color.RED));
        assertTrue(sc.hasSpawn(Color.BLUE));

        assertFalse(rc.hasSpawn(Color.YELLOW));
        assertFalse(rc.hasSpawn(Color.RED));
        assertFalse(rc.hasSpawn(Color.BLUE));
    }

    /**
     * Check shared functions for checking if there is an available item
     */
    @Test
    public void checkItem()
    {
        Side[] sides = new Side[4];
        sides[Direction.NORTH.ordinal()] = Side.WALL;
        sides[Direction.EAST.ordinal()] = Side.WALL;
        sides[Direction.SOUTH.ordinal()] = Side.NOTHING;
        sides[Direction.WEST.ordinal()] = Side.DOOR;
        RegularCell rc = new RegularCell( sides, 3);
        SpawnCell sc = new SpawnCell(sides, 3, Color.BLUE);
        Player pl = new Player("nickname", "whoaaaa", Fighter.DSTRUTTOR3);

        //The regular cell doesn't currently have items
        assertFalse(rc.hasItems(pl));

        Loot l = null;
        try
        {
            l = new Loot(new Color[]{Color.BLUE, Color.YELLOW, Color.RED});
        }
        catch(ArrayDimensionException e)
        {
            fail();
        }

        rc.refillLoot(l);

        //The regular cell has an item
        assertTrue(rc.hasItems(pl));

        //The spawn cell has no weapons
        assertFalse(sc.hasItems(pl));
    }

    /**
     * Check SpawnCell Class for edge cases
     */
    @Test
    public void checkSpawnCellClass()
    {
        Side[] sides = new Side[4];
        sides[Direction.NORTH.ordinal()] = Side.WALL;
        sides[Direction.EAST.ordinal()] = Side.WALL;
        sides[Direction.SOUTH.ordinal()] = Side.NOTHING;
        sides[Direction.WEST.ordinal()] = Side.DOOR;
        SpawnCell sc = new SpawnCell( sides, 3, Color.BLUE);

        /**
         * Check the correct configuration of the spawn color
         */
        assertSame(Color.BLUE, sc.getSpawn());
        assertTrue(sc.getWeapons().isEmpty());

        ArrayList<Color> cost = new ArrayList<>();
        cost.add(Color.BLUE);
        cost.add(Color.YELLOW);
        Action base = new Action("BaseAction", "Make a basic action", cost, null);
        ArrayList<Action> acts = new ArrayList<>();
        acts.add(base);
        Weapon w = new Weapon(1, "Weapon","desc", base, acts, base, Color.RED);

        sc.refillWeapon(w);

        /**
         * Check the correct functionality of the Weapon filling and picking
         */
        assertEquals(1, sc.getWeapons().size());
        assertTrue(sc.getWeapons().contains(w));
        assertSame(w, sc.pickWeapon(w));
        assertTrue(sc.getWeapons().isEmpty());
    }

    /**
     * Tests the correct elaboration of visible rooms from the standpoint of a player
     */
    @Test
    public void checkVisibleRooms()
    {
        Map m = Map.jsonDeserialize(1);
        Point pos1 = new Point(1,1);
        List<Integer> visible1 = new ArrayList<>();
        visible1.add(1);
        visible1.add(4);
        visible1.add(3);

        List<Integer> calculated = Map.visibleRooms(pos1, m);

        assertTrue(visible1.containsAll(calculated) && visible1.size() == calculated.size());
    }

    /**
     * Tests the visiblePlayers from a user
     */
    @Test
    public void testVisiblePlayersFromUser()
    {
        Map m = Map.jsonDeserialize(1);
        List<Player> pl = new ArrayList<>();
        pl.add(new Player("one", "hi", Fighter.DSTRUTTOR3));
        pl.add(new Player("two", "hi", Fighter.DOZER));
        pl.add(new Player("three", "hi", Fighter.VIOLETTA));
        m.getCell(1,0).addPawn(pl.get(0));
        m.getCell(2,1).addPawn(pl.get(1));
        m.getCell(3,1).addPawn(pl.get(2));

        Player viewer = new Player("viewer", "hi", Fighter.SPROG);
        viewer.applyEffects(EffectsLambda.move(viewer, new Point(1,1), m));

        List<Player> calculated = Map.visiblePlayers(viewer, m);

        assertFalse(calculated.containsAll(pl));

        assertTrue(calculated.contains(pl.get(0)));
        assertTrue(calculated.contains(pl.get(1)));
        assertFalse(calculated.contains(pl.get(2)));
    }

    /**
     * Tests the visible players in a single direction from a player
     */
    @Test
    public void testVisiblePlayersFromUserCardinalDirection()
    {
        Map m = Map.jsonDeserialize(1);
        List<Player> pl = new ArrayList<>();
        pl.add(new Player("one", "hi", Fighter.DSTRUTTOR3));
        pl.add(new Player("two", "hi", Fighter.DOZER));
        pl.add(new Player("three", "hi", Fighter.VIOLETTA));
        m.getCell(1,0).addPawn(pl.get(0));
        m.getCell(2,1).addPawn(pl.get(1));
        m.getCell(3,1).addPawn(pl.get(2));

        Player viewer = new Player("viewer", "hi", Fighter.SPROG);
        viewer.applyEffects(EffectsLambda.move(viewer, new Point(1,1), m));

        List<Player> calculated = Map.visiblePlayers(viewer, m, Direction.EAST);
        assertFalse(calculated.containsAll(pl));

        assertFalse(calculated.contains(pl.get(0)));
        assertTrue(calculated.contains(pl.get(1)));
        assertTrue(calculated.contains(pl.get(2)));
    }

    /**
     * Tests the Visible players from a specified position
     */
    @Test
    public void testVisiblePlayerFromPoint(){
        Map m = Map.jsonDeserialize(2);
        Player p1 = new Player("Player 1", "", Fighter.DSTRUTTOR3);
        Player p2 = new Player("Player 2", "", Fighter.DSTRUTTOR3);

        try {
            m.getCell(0, 0).addPawn(p1);
            m.getCell(0, 0).addPawn(p2);

            List<Player> vis = Map.visiblePlayers(new Point(0, 0), m);

            assertTrue(vis.contains(p1));
            assertTrue(vis.contains(p2));
            assertEquals(2, vis.size());
        }catch (Exception ignore){
            fail();
        }
    }

    /**
     * Test elaboration of visible players
     */
    @Test
    public void checkPlayersAtGivenDistance()
    {
        Map m = Map.jsonDeserialize(1);
        List<Player> pl = new ArrayList<>();
        pl.add(new Player("one", "hi", Fighter.DSTRUTTOR3));
        pl.add(new Player("two", "hi", Fighter.DOZER));
        pl.add(new Player("three", "hi", Fighter.VIOLETTA));
        pl.get(0).applyEffects(EffectsLambda.move(pl.get(0), new Point(1,0), m));
        pl.get(1).applyEffects(EffectsLambda.move(pl.get(1), new Point(2,1), m));
        pl.get(2).applyEffects(EffectsLambda.move(pl.get(2), new Point(3,1), m));


        m.getCell(1,0).addPawn(pl.get(0));
        m.getCell(2,1).addPawn(pl.get(1));
        m.getCell(3,1).addPawn(pl.get(2));

        Player viewer = new Player("viewer", "hi", Fighter.SPROG);
        viewer.applyEffects(EffectsLambda.move(viewer, new Point(1,1), m));

        List<Player> calculated = Map.playersAtGivenDistance(viewer, m, false, (p1,p2)->m.distance(p1,p2)<=1);

        assertFalse(calculated.containsAll(pl));

        assertTrue(calculated.contains(pl.get(0)));
        assertTrue(calculated.contains(pl.get(1)));
        assertFalse(calculated.contains(pl.get(2)));
    }

    /**
     * Tests the elaboration of positions a player can go to from its current position
     */
    @Test
    public void checkPossibleMovements()
    {
        Map m = Map.jsonDeserialize(1);
        Player viewer = new Player("viewer", "hi", Fighter.SPROG);
        viewer.applyEffects(EffectsLambda.move(viewer, new Point(1,1), m));

        List<Point> expected = new ArrayList<>();
        expected.add(new Point(1,1));
        expected.add(new Point(0,0));
        expected.add(new Point(1,0));
        expected.add(new Point(2,0));
        expected.add(new Point(2,1));
        expected.add(new Point(2,2));
        expected.add(new Point(0,2));
        expected.add(new Point(1,2));
        expected.add(new Point(2,2));

        List<Point> possible = Map.possibleMovements(viewer.getPosition(), 2, m);

        assertEquals(9, possible.size());
        for(Point e : expected)
        {
            assertEquals(1, possible.stream().filter(point -> point.getX() == e.getX() && point.getY() == e.getY()).count());
        }
    }

    /**
     * Tests the correct elaboration of the visible cells from the standpoint of the user
     */
    @Test
    public void checkVisiblePoints()
    {
        Map m = Map.jsonDeserialize(1);
        Player viewer = new Player("viewer", "hi", Fighter.SPROG);
        viewer.applyEffects(EffectsLambda.move(viewer, new Point(1,1), m));

        List<Point> visible = Map.visiblePoints(viewer.getPosition(), m, 0);

        assertEquals(7, visible.size());
    }

    /**
     * Tests for correct functionality of distance function
     */
    @Test
    public void checkDistanceCalculus() {
        Map m = Map.jsonDeserialize(2);
        Player p1 = new Player("Player 1", "", Fighter.DSTRUTTOR3);
        Player p2 = new Player("Player 2", "", Fighter.DSTRUTTOR3);

        Point[] points = new Point[12];
        for(int y = 0; y < 3; y++)
            for(int x = 0; x < 4; x++)
                points[x+4*y] = new Point(x,y);

        int distance;

        p1.applyEffects(EffectsLambda.move(p1, points[0], m));
        p2.applyEffects(EffectsLambda.move(p2, points[0], m));
        distance = m.distance(p1,p2);
        assertEquals(0, distance);

        p1.applyEffects(EffectsLambda.move(p1, points[0], m));
        p2.applyEffects(EffectsLambda.move(p2, points[1], m));
        distance = m.distance(p1,p2);
        assertEquals(1, distance);

        p1.applyEffects(EffectsLambda.move(p1, points[0], m));
        p2.applyEffects(EffectsLambda.move(p2, points[4], m));
        distance = m.distance(p1,p2);
        assertEquals(1, distance);

        p1.applyEffects(EffectsLambda.move(p1, points[4], m));
        p2.applyEffects(EffectsLambda.move(p2, points[1], m));
        distance = m.distance(p1,p2);
        assertTrue(distance == 2);

        p1.applyEffects(EffectsLambda.move(p1, points[0], m));
        p2.applyEffects(EffectsLambda.move(p2, points[5], m));
        distance = m.distance(p1,p2);
        assertEquals(2, distance);

        p1.applyEffects(EffectsLambda.move(p1, points[4], m));
        p2.applyEffects(EffectsLambda.move(p2, points[5], m));
        distance = m.distance(p1,p2);
        assertEquals(1, distance);

        p1.applyEffects(EffectsLambda.move(p1, points[6], m));
        p2.applyEffects(EffectsLambda.move(p2, points[10], m));
        distance = m.distance(p1,p2);
        assertEquals(3, distance);

        p1.applyEffects(EffectsLambda.move(p1, points[0], m));
        p2.applyEffects(EffectsLambda.move(p2, points[11], m));
        distance = m.distance(p1,p2);
        assertEquals(5, distance);

        p1.applyEffects(EffectsLambda.move(p1, points[4], m));
        p2.applyEffects(EffectsLambda.move(p2, points[7], m));
        distance = m.distance(p1,p2);
        assertEquals(3, distance);
    }
}
