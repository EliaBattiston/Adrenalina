package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the player view class
 */
public class PlayerViewTest {

    /**
     * Check playerView hiding
     */
    @Test
    public void checkPlayerView() {
        //Initialization of the needed classes with random (but coherent) parameters
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
    public void checkMyPlayerView() {
        //Initialization of the needed classes with random (but coherent) parameters
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
}
