package it.polimi.ingsw.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MapTest {
    /**
     * Check Cell Abstract Class for edge cases
     */
    @Test
    public void checkCellClass()
    {
        Side[] sides = new Side[4];
        sides[Direction.North.ordinal()] = Side.Wall;
        sides[Direction.East.ordinal()] = Side.Wall;
        sides[Direction.South.ordinal()] = Side.Nothing;
        sides[Direction.West.ordinal()] = Side.Door;
        RegularCell rc = new RegularCell( sides, 3);
        SpawnCell sc = new SpawnCell(sides, 3, Color.Blue);

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
        Player pl = new Player("nickname", "whoaaaa", Fighter.Dstruttor3);
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
        sides[Direction.North.ordinal()] = Side.Wall;
        sides[Direction.East.ordinal()] = Side.Wall;
        sides[Direction.South.ordinal()] = Side.Nothing;
        sides[Direction.West.ordinal()] = Side.Door;
        RegularCell rc = new RegularCell( sides, 3);
        Loot l = null;
        try
        {
            l = new Loot(new Color[]{Color.Blue, Color.Yellow, Color.Red});
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
        sides[Direction.North.ordinal()] = Side.Wall;
        sides[Direction.East.ordinal()] = Side.Wall;
        sides[Direction.South.ordinal()] = Side.Nothing;
        sides[Direction.West.ordinal()] = Side.Door;
        SpawnCell sc = new SpawnCell( sides, 3, Color.Blue);

        /**
         * Check the correct configuration of the spawn color
         */
        assertTrue(sc.getSpawn() == Color.Blue);
        assertTrue(sc.getWeapons().isEmpty());

        ArrayList<Color> cost = new ArrayList<>();
        cost.add(Color.Blue);
        cost.add(Color.Yellow);
        Action base = new Action("BaseAction", "Make a basic action", cost, null);
        ArrayList<Action> acts = new ArrayList<>();
        acts.add(base);
        Weapon w = new Weapon(1, "Weapon", base, acts, acts, Color.Red);

        sc.refillWeapon(w);

        /**
         * Check the correct functionality of the Weapon filling and picking
         */
        assertTrue(sc.getWeapons().size() == 1);
        assertTrue(sc.getWeapons().contains(w));
        assertTrue(sc.pickWeapon(w) == w);
        assertTrue(sc.getWeapons().isEmpty());
    }

    /**
     * Check Map Class for edge cases
     */
    @Test
    public void checkMapClass()
    {
        //TODO: insert Map control for cells insertion and cell presence
    }
}
