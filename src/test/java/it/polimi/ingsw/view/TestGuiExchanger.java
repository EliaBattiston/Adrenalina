package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Interaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestGuiExchanger {

    /**
     * Tests the functionality of the setRequest inside the GuiExchanger class
     */
    @Test
    public void testGuiExchangerSetRequest(){
        GuiExchanger ge = GuiExchanger.getInstance();

        ge.setActualInteraction(Interaction.NONE);

        assertEquals(ge.getActualInteraction(), Interaction.NONE);

        ge.setRequest(Interaction.CHOOSEBASEACTION, "Prova", ge, true);

        assertEquals(ge.getActualInteraction(), Interaction.CHOOSEBASEACTION);
        assertEquals(ge.getMessage(), "Prova");
        assertEquals(ge.getRequest(), ge);
        assertTrue(ge.isMustChoose());
        ge.setActualInteraction(Interaction.NONE);
        assertEquals(ge.getLastRealInteraction(), Interaction.CHOOSEBASEACTION);
    }

    /**
     * Tests the Log inside the GuiExchanger class
     */
    @Test
    public void testGuiExchangerLog(){
        GuiExchanger ge = GuiExchanger.getInstance();
        assertEquals(ge.getLog(), "");

        ge.addToLog("Prova");
        assertNotEquals(ge.getLog(), "Prova"); //It added also the datetime!
        assertNotEquals(ge.getLog(), "");

        ge.setActualInteraction(Interaction.SERVERIP);
        ge.setNewLogIncoming();
        assertEquals(ge.getActualInteraction(), Interaction.SERVERIP);

        ge.setActualInteraction(Interaction.NONE);
        ge.setNewLogIncoming();
        assertEquals(ge.getActualInteraction(), Interaction.LOG);
    }
}
