package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

//TODO make it handle maps that are not 3x4!!!
/**
 * this class represents the map or board of the actual game
 */
public class Map {
    /**
     * matrix of cells representing the map
     */
    private Cell[][] cells;

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

    //TODO add to the
    /**
     * Return the viewer players
     * @param viewer the one who is looking
     * @return the list of visible players
     */
    public static List<Player> visibles(Player viewer){
        ArrayList<Player> visibles = new ArrayList<>();
        int x,y;
        x = viewer.getPosition().getX();
        y = viewer.getPosition().getY();

        //TODO find a way to get the map from the game and delete this try-catch that's just for development
        Map m = null;
        try {
            m = Map.jsonDeserialize("resources/map1.json");
        }catch (FileNotFoundException e){
            ;
        }

        //TODO check that we are looking in the right direction
        //First get all the rooms visible
        List<Integer> roomsN = new ArrayList<>();
        roomsN.add(m.getCell(x, y).getRoomNumber());
        Side[] sides = m.getCell(x,y).getSides();
        if(sides[0] == Side.DOOR)
            roomsN.add(m.getCell(x,y-1).getRoomNumber());
        if(sides[1] == Side.DOOR)
            roomsN.add(m.getCell(x+1,y).getRoomNumber());
        if(sides[2] == Side.DOOR)
            roomsN.add(m.getCell(x,y+1).getRoomNumber());
        if(sides[3] == Side.DOOR)
            roomsN.add(m.getCell(x-1,y).getRoomNumber());

        //Get all the people in those rooms
        for(int i=0; i<3;i++)
            for(int j=0; j<2; j++)
                if(roomsN.contains(m.getCell(i,j).getRoomNumber()))
                    visibles.addAll(m.getCell(i, j).getPawns());

                //remove the player itself
        visibles.remove(viewer);

        return visibles;
    }
}
