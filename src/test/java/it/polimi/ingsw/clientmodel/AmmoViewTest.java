package it.polimi.ingsw.clientmodel;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the ammo view class
 */
public class AmmoViewTest {
    /**
     * Check of ammoView correct information hiding
     */
    @Test
    public void checkAmmoView() {
        //Initialization of the needed classes with random (but coherent) parameters
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
}
