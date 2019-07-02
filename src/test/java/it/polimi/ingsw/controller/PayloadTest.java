package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the correct functioning of Payload getters and setters
 */
public class PayloadTest {

    @Test
    public void checkPayload() {
        Payload payload = new Payload();
        Player p = new Player("Pippo", "", Fighter.DSTRUTTOR3);

        //Setup of the values
        payload.setType(Interaction.PING);
        payload.setParameters("parameters");
        payload.setMustChoose(true);
        payload.setEnemy(p.getView());

        //Check of the correct values containes
        assertEquals(Interaction.PING, payload.getType());
        assertEquals("parameters", payload.getParameters());
        assertTrue(payload.isMustChoose());
        assertEquals(p.getNick(), payload.getEnemy().getNick());
    }
}
