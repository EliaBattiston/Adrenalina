package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * this class represents the map or board of the actual game
 */
public class Map {
    /**
     * matrix of cells representing the map
     */
    private Cell[][] cells;

    /**
     * the constructor instantiates a new matrix of cells dimension 4x3 and loads the characteristics of the single cells from a JSON file
     * @param filePath JSON map cnfig file
     */
    Map(String filePath)
    {
        cells = new Cell[4][3];
        /*cells[0][0]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 0);
        cells[0][1]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 1);
        cells[0][2]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 2);
        cells[1][0]= new SpawnCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 4, Color.Red);
        cells[1][1]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 5);
        cells[1][2]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 6);
        cells[2][0]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 7);
        cells[2][1]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 8);
        cells[2][2]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 9);
        cells[3][0]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 10);
        cells[3][1]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 11);
        cells[3][2]= new RegularCell(new Side[]{Side.Wall, Side.Door, Side.Wall, Side.Nothing}, 12);*/
    }

    //TODO rename in the UML (it was loadFromFile)
    /**
     * the function deserialize the JSON config file and loads the data in the matrix
     * @param pathJsonFile JSON map config file
     */
    public static Map jsonDeserialize(String pathJsonFile) throws FileNotFoundException
    {
        JsonReader reader = new JsonReader(new FileReader(pathJsonFile));
        Gson gson = new GsonBuilder().create();

        return gson.fromJson(reader, Map.class);
    }

    /**
     * returns the reference of a seected cell
     * @param x X-coordinate of the cell
     * @param y Y-coordinate of the cell
     * @return cell reference (or null in case of out of bound coordinates or missing cell
     */
    public Cell getCell(int x, int y)
    {
        if(x >= 0 && x < 4 && y >= 0 && y < 3)
            return cells[x][y];

        return null;
    }
}
