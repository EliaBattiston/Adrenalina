package it.polimi.ingsw.controller;

import it.polimi.ingsw.clientmodel.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checkout of the correct information hiding process of the clientview package classes
 */
public class ViewTest {

    /**
     * Check playerView hiding
     */
    @Test
    public void checkPlayerView() {
        //Inizialization of the needed classes with random (but coherent) parameters
        Player player = new Player("Player", "Yeeee", Fighter.DSTRUTTOR3);
        List<Weapon> weapons = new ArrayList<>();
        List<String> marks = new ArrayList<>();
        List<PlayerView> views = new ArrayList<>();

        Point pos = new Point(2,2);

        weapons.add(new Weapon(1, "Pippo", "", null, null, null, Color.BLUE));
        weapons.add(new Weapon(2, "Pluto", "", null, null, null, Color.BLUE));
        weapons.add(new Weapon(3, "Paperino", "", null, null, null, Color.BLUE));

        weapons.get(0).setLoaded(false);
        weapons.get(1).setLoaded(true);
        weapons.get(2).setLoaded(false);

        marks.add("d");
        marks.add("e");

        player.setFrenzyBoard(true);

        player.addSkull();
        player.addSkull();

        player.addPoints(5);

        //Application of the parameters to the player via a lambda call
        player.applyEffects(((damage, mark, position, weapon, power, ammo) -> {
            weapon[0] = weapons.get(0);
            weapon[1] = weapons.get(1);
            weapon[2] = weapons.get(2);
            damage[0] = "a";
            damage[1] = "b";
            damage[2] = "c";
            mark.addAll(marks);
            position.set(pos);
            ammo.add(Color.BLUE, 2);
            ammo.add(Color.YELLOW, 1);
        }));

        PlayerView view = player.getView();

        //Check of the correct information hiding
        assertEquals(player.getNick(), view.getNick());
        assertEquals(player.getCharacter(), view.getCharacter());

        List<Weapon> unloaded = new ArrayList<>();
        unloaded.add(weapons.get(0));
        unloaded.add(weapons.get(2));
        //other players can see only unloaded weapons
        assertEquals(unloaded, view.getWeapons());
        //General players cannot access a precise player's powers
        assertEquals(new ArrayList<>(), view.getPowers());

        assertTrue(view.getFrenzyBoard());

        assertEquals("a", view.getDamage(0));
        assertEquals("b", view.getDamage(1));
        assertEquals("c", view.getDamage(2));
        //check of the edge cases
        assertNull(view.getDamage(-1));
        assertNull(view.getDamage(13));

        assertEquals(marks, view.getReceivedMarks());

        assertEquals(2, view.getSkulls());

        assertTrue(pos.samePoint(view.getPosition()));

        assertEquals(3, view.getAmmo(Color.BLUE));
        assertEquals(2, view.getAmmo(Color.YELLOW));
        assertEquals(1, view.getAmmo(Color.RED));
        assertEquals(0, view.getAmmo(Color.POWER));

        assertEquals(5, view.getPoints());

        views.add(view);
        views.add(new Player("Player2", "Yeeee", Fighter.BANSHEE).getView());
        views.add(new Player("Player3", "Yeeee", Fighter.DOZER).getView());

        assertEquals(Fighter.BANSHEE, PlayerView.fighterFromNick(views, "Player2"));
        assertNull(PlayerView.fighterFromNick(views, "Player4"));
    }

    /**
     * Check of myPlayerView information hiding
     */
    @Test
    public void checkMyPlayerViwew() {
        //Inizialization of the needed classes with random (but coherent) parameters
        Player player = new Player("Player", "Yeeee", Fighter.DSTRUTTOR3);
        List<Power> powers = new ArrayList<>();

        powers.add(new Power(1, "aaa", null, null));
        powers.add(new Power(2, "bbb", null, null));

        player.applyEffects(((damage, mark, position, weapon, power, ammo) -> {
            power[0] = powers.get(0);
            power[1] = powers.get(1);
        }));

        //The player itself can see its powers
        MyPlayerView fullView = player.getFullView();
        assertEquals(powers, fullView.getPowers());
    }

    /**
     * Check of ammoView correct information hiding
     */
    @Test
    public void checkAmmoView() {
        //Inizialization of the needed classes with random (but coherent) parameters
        Player player = new Player("Player", "Yeeee", Fighter.DSTRUTTOR3);

        player.applyEffects(((damage, mark, position, weapon, power, ammo) -> {
            ammo.add(Color.BLUE, 2);
            ammo.add(Color.YELLOW, 1);
        }));

        PlayerView view = player.getView();
        //Player initialization implies assignment of one ammo per color
        assertEquals(3, view.getAmmo().getBlue());
        assertEquals(2, view.getAmmo().getYellow());
        assertEquals(1, view.getAmmo().getRed());
    }

    /**
     * Check of killView information hiding
     */
    @Test
    public void checkKillView() {
        try {
            //Inizialization of the needed classes with random (but coherent) parameters
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
        catch (FileNotFoundException e) { ; }
    }

    /**
     * Check of gameView information hiding
     */
    @Test
    public void checkGameView() {
        try {
            //Inizialization of the needed classes with random (but coherent) parameters
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
        catch (FileNotFoundException e) { ; }
    }

    /**
     * Check of matchView information hiding
     */
    @Test
    public void checkMatchView() {
        try {
            //Inizialization of the needed classes with random (but coherent) parameters
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
            assertEquals(GamePhase.INITIALIZING, m.getView(p).getPhase());
            assertEquals(Configuration.getInstance().getPlayerTurnSeconds(), m.getView(p).getTimeForAction());

        }
        catch (FileNotFoundException e) { ; }
    }

    /**
     * Check of mapView, spawnCellView and regularCellView information hiding
     */
    @Test
    public void checkMapView() {
        try {
            //Inizialization of the needed classes with random (but coherent) parameters
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

        }
        catch (FileNotFoundException e) { ; }
    }
}
