package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ArrayDimensionException;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MapTest {
    /**
     * Check Cell Abstract Class for edge cases
     */
    @Test
    public void checkCellClass()
    {
        Side[] sides = new Side[4];
        sides[Direction.NORTH.ordinal()] = Side.WALL;
        sides[Direction.EAST.ordinal()] = Side.WALL;
        sides[Direction.SOUTH.ordinal()] = Side.NOTHING;
        sides[Direction.WEST.ordinal()] = Side.DOOR;
        RegularCell rc = new RegularCell( sides, 3);
        SpawnCell sc = new SpawnCell(sides, 3, Color.BLUE);

        /**
         * Check the correct instantiation of both Cell child classes
         */
        assertTrue(rc.getRoomNumber() == 3);
        assertTrue(sc.getRoomNumber() == 3);

        assertTrue(rc.getSides().length == 4);
        assertTrue(sc.getSides().length == 4);

        assertTrue(rc.getPawns().size() == 0);
        assertTrue(rc.getPawns().size() == 0);

        /**
         * Check the correct player insertion and deletion procedure in the cell
         */
        Player pl = new Player("nickname", "whoaaaa", Fighter.DSTRUTTOR3);
        rc.addPawn(pl);
        sc.addPawn(pl);

        assertTrue(rc.getPawns().size() == 1 && rc.getPawns().contains(pl));
        assertTrue(sc.getPawns().size() == 1 && sc.getPawns().contains(pl));

        sc.removePawn(pl);
        rc.removePawn(pl);

        assertTrue(rc.getPawns().size() == 0);
        assertTrue(rc.getPawns().size() == 0);
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
        assertTrue(rc.getLoot() == l);
        assertTrue(rc.pickLoot() == l); //now we don't have it
        assertTrue(rc.getLoot() == null);
        assertTrue(rc.pickLoot() == null);
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
        assertTrue(sc.getSpawn() == Color.BLUE);
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
        assertTrue(sc.getWeapons().size() == 1);
        assertTrue(sc.getWeapons().contains(w));
        assertTrue(sc.pickWeapon(w) == w);
        assertTrue(sc.getWeapons().isEmpty());
    }

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
     * Tests both visiblePlayer methods
     */
    @Test
    public void checkVisiblePlayers()
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
        assertTrue(!calculated.contains(pl.get(2)));

        //in a cardinal direction
        calculated = Map.visiblePlayers(viewer, m, Direction.EAST);
        assertFalse(calculated.containsAll(pl));

        assertTrue(!calculated.contains(pl.get(0)));
        assertTrue(calculated.contains(pl.get(1)));
        assertTrue(calculated.contains(pl.get(2)));
    }

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

        List<Player> calculated = Map.playersAtGivenDistance(viewer, m, false, (p1,p2)->Map.distance(p1,p2)<=1);

        assertFalse(calculated.containsAll(pl));

        assertTrue(calculated.contains(pl.get(0)));
        assertTrue(calculated.contains(pl.get(1)));
        assertTrue(!calculated.contains(pl.get(2)));
    }

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

        assertEquals(possible.size(),9);
        for(Point e : expected)
        {
            assertEquals( possible.stream().filter(point -> point.getX() == e.getX() && point.getY() == e.getY()).count(), 1 );
        }
    }

    @Test
    public void checkVisiblePoints()
    {
        Map m = Map.jsonDeserialize(1);
        Player viewer = new Player("viewer", "hi", Fighter.SPROG);
        viewer.applyEffects(EffectsLambda.move(viewer, new Point(1,1), m));

        List<Point> visible = Map.visiblePoints(viewer.getPosition(), m, 0);

        assertTrue(visible.size() == 7);
    }
}
