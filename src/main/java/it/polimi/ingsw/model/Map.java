package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.exceptions.WrongPointException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * returns the reference of a selected cell
     * @param p
     * @return
     */
    public Cell getCell(Point p)
    {
        return cells[p.getX()][p.getY()];
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

    /**
     * Return the list of visible rooms from a point
     * @param pos
     * @param map
     * @return
     */
    public static List<Integer> visibleRooms(Point pos, Map map){
        List<Integer> rooms = new ArrayList<>();
        int x,y;
        x = pos.getX();
        y = pos.getY();

        rooms.add(map.getCell(x, y).getRoomNumber());

        Side[] sides = map.getCell(x,y).getSides();
        if(sides[Direction.NORTH.ordinal()] == Side.DOOR)
            rooms.add(map.getCell(x,y-1).getRoomNumber());
        if(sides[Direction.EAST.ordinal()] == Side.DOOR)
            rooms.add(map.getCell(x+1,y).getRoomNumber());
        if(sides[Direction.SOUTH.ordinal()] == Side.DOOR)
            rooms.add(map.getCell(x,y+1).getRoomNumber());
        if(sides[Direction.WEST.ordinal()] == Side.DOOR)
            rooms.add(map.getCell(x-1,y).getRoomNumber());

        return rooms;
    }

    /**
     * Return the list of points visible from the startPoint plus all the ones at a maximum distance from a visible one
     * that is less than notVisDist
     * @param startPoint starting point
     * @param map map where you need to look
     * @param notVisDist if 0 the method returns only visible points, otherwise it returns also points that has a
     *                   notVisDist as max dist from a visible point
     * @return
     */
    public static List<Point> pointsAround(Point startPoint, Map map, int notVisDist){
        List<Integer> visRooms = Map.visibleRooms(startPoint, map);
        List<Point> points = new ArrayList<>();

        //Get all the visible points
        for(int i=0; i<3;i++)
            for(int j=0; j<2; j++)
                if(visRooms.contains(map.getCell(i,j).getRoomNumber())) {
                    try {
                        points.add(new Point(j, i));
                    } catch (WrongPointException e) {

                    }
                }

        if(notVisDist > 0){
            HashSet<Point> notVisible = new HashSet<>();
            for(Point p:points)
                notVisible.addAll(Map.possibleMovements(p, notVisDist, map));

            notVisible.addAll(points);

            return new ArrayList<>(notVisible);
        }

        return points;
    }

    /**
     * Return the visible players from the viewer players
     * @param viewer the one who is looking
     * @param map the map on wich we are looking for
     * @return the list of visible players
     */
    public static List<Player> visiblePlayers(Player viewer, Map map){
        ArrayList<Player> visibles = new ArrayList<>();

        List<Integer> visRooms = Map.visibleRooms(viewer.getPosition(), map);

        //Get all the people in those rooms
        for(int i=0; i<3;i++)
            for(int j=0; j<2; j++)
                if(visRooms.contains(map.getCell(i,j).getRoomNumber()))
                    visibles.addAll(map.getCell(i, j).getPawns());

        //remove the player itself
        visibles.remove(viewer);

        return visibles;
    }

    /**
     * Return the visible players from the viewer players in a cardinal direction, it doesn't care about walls
     * @param viewer the one who is looking
     * @param map the map on which we are looking for
     * @param dir the cardinal direction where you want to lo
     * @return the list of visible players
     */
    public static List<Player> visiblePlayers(Player viewer, Map map, Direction dir){
        ArrayList<Player> visible = new ArrayList<>();

        switch (dir){
            case NORTH:
                for(int y=viewer.getPosition().getY()-1; y>=0; y--)
                    visible.addAll(map.getCell(viewer.getPosition().getX(), y).getPawns());
                break;
            case EAST:
                for(int x=viewer.getPosition().getX()+1; x<4; x++)
                    visible.addAll(map.getCell(x, viewer.getPosition().getY()).getPawns());
                break;
            case SOUTH:
                for(int y=viewer.getPosition().getY()+1; y<3; y++)
                    visible.addAll(map.getCell(viewer.getPosition().getX(), y).getPawns());
                break;
            case WEST:
                for(int x=viewer.getPosition().getX()-1; x>=0; x--)
                    visible.addAll(map.getCell(x, viewer.getPosition().getY()).getPawns());
                break;
        }

        return visible;
    }

    /**
     * Return the manhattan distance between two players
     * @param p1 player 1
     * @param p2 player 2
     * @return the distance
     */
    public static int distance(Player p1, Player p2){
        return Math.abs(p1.getPosition().getX() - p2.getPosition().getX()) + Math.abs(p1.getPosition().getY() - p2.getPosition().getY());
    }

    /**
     * Return the list of players in distance (calculated by the MapDistanceStrategy) already removing the player who you start looking from
     * @param pl Player from where you start looking
     * @param map Map
     * @param strategy strategy that accept/refuse an enemy
     * @return The list of visible players accepted from the strategy
     */
    public static List<Player> distanceStrategy(Player pl, Map map, MapDistanceStrategy strategy){
        List<Player> visible = Map.visiblePlayers(pl, map);
        List<Player> targets = new ArrayList<>();
        for(Player p:visible)
            if(strategy.calculate(pl, p))
                targets.add(p);

        targets.remove(pl);
        return targets;
    }

    /**
     * Check if two players are in the same room
     * @param p1 player 1
     * @param p2 player 2
     * @return true if they are in the same room
     */
    public static boolean sameRoom(Player p1, Player p2, Map m){
        return m.getCell(p1.getPosition()).getRoomNumber() == m.getCell(p2.getPosition()).getRoomNumber();
    }

    /**
     * Find the list of the points reachable at a maximum given distance
     * @param startPoint start position
     * @param dist max dist to look for
     * @param map the map
     * @return the list of points reachable with that amount of movements
     */
    public static List<Point> possibleMovements(Point startPoint, int dist, Map map) {
        Set<Point> points = new HashSet<>();
        possibleMovements(startPoint, dist, map, points);
        return new ArrayList<>(points);
    }

    /**
     * Find the list of the points reachable at a maximum given distance
     * @param startPoint start position
     * @param dist max dist to look for
     * @param map the map
     * @param points the Set of points
     */
    private static Set<Point> possibleMovements(Point startPoint, int dist, Map map, Set<Point> points){
        Cell s = map.getCell(startPoint);
        Point tempP = null; //temp var for new points found

        try {
            if (s.getSides()[Direction.NORTH.ordinal()] == Side.DOOR || s.getSides()[Direction.NORTH.ordinal()] == Side.NOTHING){
                tempP = new Point(startPoint.getX(), startPoint.getY() + 1);
                points.add(tempP);
                points.addAll(Map.possibleMovements(tempP, dist-1, map, points));
            }
            if (s.getSides()[Direction.EAST.ordinal()] == Side.DOOR || s.getSides()[Direction.EAST.ordinal()] == Side.NOTHING){
                tempP = new Point(startPoint.getX()+1, startPoint.getY());
                points.add(tempP);
                points.addAll(Map.possibleMovements(tempP, dist-1, map, points));
            }
            if (s.getSides()[Direction.SOUTH.ordinal()] == Side.DOOR || s.getSides()[Direction.SOUTH.ordinal()] == Side.NOTHING){
                tempP = new Point(startPoint.getX(), startPoint.getY() - 1);
                points.add(tempP);
                points.addAll(Map.possibleMovements(tempP, dist-1, map, points));
            }
            if (s.getSides()[Direction.WEST.ordinal()] == Side.DOOR || s.getSides()[Direction.WEST.ordinal()] == Side.NOTHING){
                tempP = new Point(startPoint.getX()-1, startPoint.getY());
                points.add(tempP);
                points.addAll(Map.possibleMovements(tempP, dist-1, map, points));
            }
        }catch (WrongPointException ex){
            ;
        }

        return points;
    }
}
