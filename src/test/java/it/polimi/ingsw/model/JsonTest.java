package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ArrayDimensionException;
import org.junit.Test;

import java.io.FileNotFoundException;

import static junit.framework.TestCase.fail;

public class JsonTest {
    /**
     * Tests the json serialization using Gson
     */
    @Test
    public void TestJsonGenerate()
    {
        Deck<Weapon> dW = new Deck<>();
        dW.add(new Weapon(1, "WeaponOne", "desc",null, null, null, Color.Blue));
        dW.add(new Weapon(2, "WeaponOQwo", "desc",null, null, null, Color.Red));
        dW.add(new Weapon(3, "WeaponTree", "desc",null, null, null, Color.Yellow));

        EndlessDeck<Power> dP = new EndlessDeck<>();
        dP.add(new Power(1, "Pow1", null, Color.Red));
        dP.add(new Power(2, "Pow2", null, Color.Blue));
        dP.add(new Power(3, "Pow3", null, Color.Yellow));

        EndlessDeck<Loot> dL = new EndlessDeck<>();
        try {
            dL.add(new Loot(new Color[]{Color.Red, Color.Blue, Color.Power}));
            dL.add(new Loot(new Color[]{Color.Red, Color.Yellow, Color.Power}));
            dL.add(new Loot(new Color[]{Color.Blue, Color.Blue, Color.Power}));
        }catch(ArrayDimensionException e){
            fail();
        }

        Game g = new Game(5, new Map("dummy"), dP, dL, dW );
        String json = g.jsonSerialize();

        //TODO fix this with a file
       /* try {
            Game g2 = Game.jsonDeserialize(json);
            String json2 = g.jsonSerialize();

            assertEquals(json, json2);
        }catch(FileNotFoundException e){
            fail();
        }*/
    }

    @Test
    public void TestJsonBaseGame(){
        String baseGame = "resources/baseGame.json";
        String baseGameWithMap = "resources/withMap.json";
        //String baseGameWithMapSingleCell = "resources/withMapSingleCell.json";
        String mapA = "resources/mapA.json";

        try {
            //Game g = Game.jsonDeserialize(baseGame);
            //g.setMap(new Map(""));
            //g.setMap(Map.jsonDeserialize(mapA));
            Game g = Game.jsonDeserialize(baseGameWithMap);
            String j = g.jsonSerialize();
            g.getAmmoDeck(); //just do something for breakpoint
        }catch(FileNotFoundException e){
            fail();
        }
    }
}
