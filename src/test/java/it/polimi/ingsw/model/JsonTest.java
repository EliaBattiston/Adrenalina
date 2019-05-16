package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.ArrayDimensionException;
import it.polimi.ingsw.exceptions.EmptyDeckException;

import java.io.FileNotFoundException;

public class JsonTest {
    /**
     * Tests the json serialization using Gson
     */
    @Test
    public void TestJsonGenerate()
    {
        Deck<Weapon> dW = new Deck<>();
        dW.add(new Weapon(1, "WeaponOne", "desc",null, null, null, Color.BLUE));
        dW.add(new Weapon(2, "WeaponOQwo", "desc",null, null, null, Color.RED));
        dW.add(new Weapon(3, "WeaponTree", "desc",null, null, null, Color.YELLOW));

        EndlessDeck<Power> dP = new EndlessDeck<>();
        dP.add(new Power(1, "Pow1", null, Color.RED));
        dP.add(new Power(2, "Pow2", null, Color.BLUE));
        dP.add(new Power(3, "Pow3", null, Color.YELLOW));

        EndlessDeck<Loot> dL = new EndlessDeck<>();
        try {
            dL.add(new Loot(new Color[]{Color.RED, Color.BLUE, Color.POWER}));
            dL.add(new Loot(new Color[]{Color.RED, Color.YELLOW, Color.POWER}));
            dL.add(new Loot(new Color[]{Color.BLUE, Color.BLUE, Color.POWER}));
        }catch(ArrayDimensionException e){
            fail();
        }

        Game g = new Game(5, new Map(), dP, dL, dW );
        String json = g.jsonSerialize();
    }

    @Test
    public void TestJsonBaseGame() throws EmptyDeckException {
        String baseGame = "baseGame.json";
        String map1 = "map1.json";

        try {
            Game g = Game.jsonDeserialize(baseGame);
            g.loadMap(1);
            g.initializeSkullsBoard(6);

            g.getAmmoDeck().draw();
            g.getPowersDeck().scrapCard(g.getPowersDeck().draw());

            String j = g.jsonSerialize();
        }catch(FileNotFoundException e){
            fail();
        }
    }
}
