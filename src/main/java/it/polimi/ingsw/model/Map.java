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

    //TODO remove from UML
    /* * !TODO moved * for javadock
     * the constructor instantiates a new matrix of cells dimension 4x3 and loads the characteristics of the single cells from a JSON file
     * @param filePath JSON map config file
     */
    /*Map(String filePath)
    {
        cells = new Cell[4][3];
    }*/

    //TODO rename in the UML (it was loadFromFile)
    /**
     * the function deserialize the JSON config file and loads the data in the matrix
     * @param pathJsonFile JSON map config file
     */
    public static Map jsonDeserialize(String pathJsonFile) throws FileNotFoundException
    {
        JsonReader reader = new JsonReader(new FileReader(pathJsonFile));

        GsonBuilder gsonBilder = new GsonBuilder();
        gsonBilder.registerTypeAdapter(Cell.class, new CellAdapter());
        Gson gson = gsonBilder.create();

        return gson.fromJson(reader, Map.class);
    }

    /**
     * returns the reference of a selected cell
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
