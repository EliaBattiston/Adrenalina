package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Interaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGuiExchanger {

    /**
     * Tests the functionality of the setRequest inside the GuiExchanger class
     */
    @Test
    public void testGuiExchangerSetRequest(){
        GuiExchanger ge = GuiExchanger.getInstance();

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

        //assertEquals(ge.getMessage(), "");
    }
}
