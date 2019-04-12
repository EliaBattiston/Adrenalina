package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.exceptions.WrongPointException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

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
        if(x >= 0 && x < 4 && y >= 0 && y < 3)//TODO make it handle maps that are not 3x4!!!
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

    /**
     * Return the list of all the players on the map
     * @param m the map
     * @return the List of players
     */
    public static List<Player> playersInTheMap(Map m){
        ArrayList<Player> p = new ArrayList<>();

        for(Cell[] row:m.cells){
            for(Cell c:row)
                p.addAll(c.getPawns());
        }

        return p;
    }

    //TODO add to the UML
    //TODo add the max distance from me, add the minimum distance, add the same cell
    /**
     * Return the viewer players
     * @param viewer the one who is looking
     * @return the list of visible players
     */
    public static List<Player> visibles(Player viewer, Map map){
        ArrayList<Player> visibles = new ArrayList<>();
        int x,y;
        x = viewer.getPosition().getX();
        y = viewer.getPosition().getY();

        //TODO check that we are looking in the right direction
        //First get all the rooms visible
        List<Integer> roomsN = new ArrayList<>();
        roomsN.add(map.getCell(x, y).getRoomNumber());
        Side[] sides = map.getCell(x,y).getSides();
        if(sides[0] == Side.DOOR)
            roomsN.add(map.getCell(x,y-1).getRoomNumber());
        if(sides[1] == Side.DOOR)
            roomsN.add(map.getCell(x+1,y).getRoomNumber());
        if(sides[2] == Side.DOOR)
            roomsN.add(map.getCell(x,y+1).getRoomNumber());
        if(sides[3] == Side.DOOR)
            roomsN.add(map.getCell(x-1,y).getRoomNumber());

        //Get all the people in those rooms
        for(int i=0; i<3;i++)
            for(int j=0; j<2; j++)
                if(roomsN.contains(map.getCell(i,j).getRoomNumber()))
                    visibles.addAll(map.getCell(i, j).getPawns());

                //remove the player itself
        visibles.remove(viewer);

        return visibles;
    }

    /**
     * Return the manhattan distance between two players
     * @param p1
     * @param p2
     * @return the distance
     */
    public static int distance(Player p1, Player p2){
        return Math.abs(p1.getPosition().getX() - p2.getPosition().getX()) + Math.abs(p1.getPosition().getY() - p2.getPosition().getY());
    }

    /**
     * Check if two players are in the same room
     * @param p1
     * @param p2
     * @return true if they are in the same room
     */
    public static boolean sameRoom(Player p1, Player p2, Map m){
        return m.getCell(p1.getPosition().getX(), p1.getPosition().getX()).getRoomNumber() == m.getCell(p2.getPosition().getX(), p2.getPosition().getX()).getRoomNumber();
    }

    /**
     * Find the list of the points reachable at a maximum given distance
     * @param startPoint start position
     * @param dist max dist to look for
     * @param map the map
     * @return the list of points reachable with that amount of movements
     */
    public static List<Point> possibleMovements(Point startPoint, int dist, Map map){//TODO check the right use of x and y, the recursion and the presence of duplicates
        List<Point> points = new ArrayList<>();
        Cell s = map.getCell(startPoint.getX(), startPoint.getY());
        Point tempP = null; //temp var for new points found

        try {
            if (s.getSides()[0] == Side.DOOR || s.getSides()[0] == Side.NOTHING){
                tempP = new Point(startPoint.getX(), startPoint.getY() + 1);
                points.add(tempP);
                points.addAll(Map.possibleMovements(tempP, dist-1, map));
            }
            if (s.getSides()[1] == Side.DOOR || s.getSides()[1] == Side.NOTHING){
                tempP = new Point(startPoint.getX()+1, startPoint.getY());
                points.add(tempP);
                points.addAll(Map.possibleMovements(tempP, dist-1, map));
            }
            if (s.getSides()[2] == Side.DOOR || s.getSides()[2] == Side.NOTHING){
                tempP = new Point(startPoint.getX(), startPoint.getY() - 1);
                points.add(tempP);
                points.addAll(Map.possibleMovements(tempP, dist-1, map));
            }
            if (s.getSides()[3] == Side.DOOR || s.getSides()[3] == Side.NOTHING){
                tempP = new Point(startPoint.getX()-1, startPoint.getY());
                points.add(tempP);
                points.addAll(Map.possibleMovements(tempP, dist-1, map));
            }
        }catch (WrongPointException ex){
            ;
        }

        return points;
    }

}
