package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.exceptions.WrongPointException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class represents the map or board of the actual game
 */
public class Map {
    /**
     * Matrix of cells representing the map
     */
    private Cell[][] cells;

    /**
     * Returns the reference of a selected cell
     * @param x X-coordinate of the cell
     * @param y Y-coordinate of the cell
     * @return cell reference (or null in case of out of bound coordinates or missing cell)
     */
    public Cell getCell(int x, int y) {
        if(x >= 0 && x < 4 && y >= 0 && y < 3)
            return cells[x][y];
        return null;
    }

    /**
     * Returns the reference of a selected cell
     * @param p Point with X and Y coordinates of the cell
     * @return Cell in the correct position, or null in case of out of bound coordinates or missing cell
     */
    public Cell getCell(Point p)
    {
        return cells[p.getX()][p.getY()];
    }

    /**
     * Gives a point containing the coordinates of a cell
     * @param c Requested cell
     * @return Point with X and Y coordinates of the cell in the map
     */
    public Point getCellPosition(Cell c){
        for(int i=0; i<=3;i++)
            for(int j=0; j<=2; j++)
                if(cells[i][j] == c)
                    return new Point(i,j);

        return null;
    }

    /**
     * Deserialize one of the four maps
     * @param mapNumber 1 to 4, the number of the map
     * @return the deserialized map or null if mapNumber it's not in the range
     */
    public static Map jsonDeserialize(int mapNumber) {
        try{
            if(mapNumber >= 1 && mapNumber <= 4)
            {
                return Map.jsonDeserialize("resources/map" + mapNumber + ".json");
            }
            else
            {
                return null;
            }
        }catch(FileNotFoundException ex){
            return null;
        }
    }

    /**
     * Deserializes the JSON config file and loads the data in the matrix
     * @param pathJsonFile JSON map config file
     * @return Map class with its matrix correctly initialized according to the json file's content
     * @throws FileNotFoundException If there is a problem accessing the file
     */
    public static Map jsonDeserialize(String pathJsonFile) throws FileNotFoundException {
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
                if(c!=null)
                    p.addAll(c.getPawns());
        }

        return p;
    }

    /**
     * Return the list of visible rooms from a point
     * @param pos pos
     * @param map map
     * @return list of visible rooms
     */
    public static List<Integer> visibleRooms(Point pos, Map map){
        List<Integer> rooms = new ArrayList<>();
        int x;
        int y;
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
     * Return the visible players from the viewer players
     * @param viewer the one who is looking
     * @param map the map on which we are looking for
     * @return the list of visible players
     */
    public static List<Player> visiblePlayers(Player viewer, Map map){
        ArrayList<Player> visibles = new ArrayList<>();

        List<Integer> visRooms = Map.visibleRooms(viewer.getPosition(), map);

        //Get all the people in those rooms
        for(int i=0; i<=3;i++)
            for(int j=0; j<=2; j++)
                if(map.getCell(i,j) != null)
                    if(visRooms.contains(map.getCell(i,j).getRoomNumber()))
                        visibles.addAll(map.getCell(i, j).getPawns());

        //remove the player itself
        visibles.remove(viewer);

        return visibles;
    }

    /**
     * Return the visible players from a position
     * @param position The position of the one who's looking
     * @param map The map on which we are looking for
     * @return the list of visible players
     */
    public static List<Player> visiblePlayers(Point position, Map map){
        ArrayList<Player> visibles = new ArrayList<>();

        List<Integer> visRooms = Map.visibleRooms(position, map);

        //Get all the people in those rooms
        for(int i=0; i<=3;i++)
            for(int j=0; j<=2; j++)
                if(map.getCell(i,j) != null)
                    if(visRooms.contains(map.getCell(i,j).getRoomNumber()))
                        visibles.addAll(map.getCell(i, j).getPawns());

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
                    if(map.getCell(viewer.getPosition().getX(), y) != null)
                        visible.addAll(map.getCell(viewer.getPosition().getX(), y).getPawns());
                break;
            case EAST:
                for(int x=viewer.getPosition().getX()+1; x<4; x++)
                    if(map.getCell(x, viewer.getPosition().getY()) != null)
                        visible.addAll(map.getCell(x, viewer.getPosition().getY()).getPawns());
                break;
            case SOUTH:
                for(int y=viewer.getPosition().getY()+1; y<3; y++)
                    if(map.getCell(viewer.getPosition().getX(), y) != null)
                        visible.addAll(map.getCell(viewer.getPosition().getX(), y).getPawns());
                break;
            case WEST:
                for(int x=viewer.getPosition().getX()-1; x>=0; x--)
                    if(map.getCell(x, viewer.getPosition().getY()) != null)
                        visible.addAll(map.getCell(x, viewer.getPosition().getY()).getPawns());
                break;
        }

        visible.remove(viewer);
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
     * @param mustVisible true if the player needs to be visible
     * @param strategy strategy that accept/refuse an enemy
     * @return The list of visible players accepted from the strategy
     */
    public static List<Player> playersAtGivenDistance(Player pl, Map map, boolean mustVisible, MapDistanceStrategy strategy){
        //TODO IMPORTANT @elia @andrea check this method really works, a call to this method appears sometimes when weird stuffs appear
        List<Player> visible;
        if(mustVisible)
            visible = Map.visiblePlayers(pl, map);
        else
            visible = Map.playersInTheMap(map);
        List<Player> targets = new ArrayList<>();
        for(Player p:visible)
            if(strategy.calculate(pl, p))
                targets.add(p);

        targets.remove(pl);
        return targets;
    }

    /**
     * Find the list of the points reachable at a maximum given distance
     * @param startPoint start position
     * @param dist max dist to look for
     * @param map the map
     * @return the list of points reachable with that amount of movements
     */
    public static List<Point> possibleMovements(Point startPoint, int dist, Map map) {
        Set<Cell> cells = new HashSet<>();
        possibleMovements(startPoint, dist, map, cells);

        List<Point> points = new ArrayList<>();
        for(Cell c:cells)
            points.add(map.getCellPosition(c));

        //TODO check the presence of the starting point
        return points;
    }

    /**
     * Find the list of the points reachable at a maximum given distance
     * @param startPoint start position
     * @param dist max dist to look for
     * @param map the map
     * @param points the Set of points
     * @return Set of cells to which the movement is possible
     */
    private static Set<Cell> possibleMovements(Point startPoint, int dist, Map map, Set<Cell> points){
        Cell s = map.getCell(startPoint);
        Point tempP = null; //temp var for new points found

        try {
            if (s.getSides()[Direction.NORTH.ordinal()] != Side.WALL){
                tempP = new Point(startPoint.getX(), startPoint.getY()-1);
                points.add(map.getCell(tempP));
                if(dist-1 > 0)
                    Map.possibleMovements(tempP, dist-1, map, points);
            }
            if (s.getSides()[Direction.EAST.ordinal()] != Side.WALL){
                tempP = new Point(startPoint.getX()+1, startPoint.getY());
                points.add(map.getCell(tempP));
                if(dist-1 > 0)
                    Map.possibleMovements(tempP, dist-1, map, points);
            }
            if (s.getSides()[Direction.SOUTH.ordinal()] != Side.WALL){
                tempP = new Point(startPoint.getX(), startPoint.getY() + 1);
                points.add(map.getCell(tempP));
                if(dist-1 > 0)
                    Map.possibleMovements(tempP, dist-1, map, points);
            }
            if (s.getSides()[Direction.WEST.ordinal()] != Side.WALL){
                tempP = new Point(startPoint.getX()-1, startPoint.getY());
                points.add(map.getCell(tempP));
                if(dist-1 > 0)
                    Map.possibleMovements(tempP, dist-1, map, points);
            }
        }catch (WrongPointException ex){
            Logger.getGlobal().log( Level.SEVERE, ex.toString(), ex );
        }

        return points;
    }

    /**
     * Return the list of possible movements that are in a single direction, it looks for all the directions
     * @param startPoint Point from which the movement starts
     * @param dist Maximum distance in number of steps
     * @param map Map on which the steps will be calculated
     * @return List of possible destinations
     */
    public static List<Point> possibleMovementsAllSingleDirection(Point startPoint, int dist, Map map){
        List<Point> visible = new ArrayList<>();

        visible.addAll(Map.possibleMovementsSpecificDirection(startPoint, dist, map, Direction.NORTH));
        visible.addAll(Map.possibleMovementsSpecificDirection(startPoint, dist, map, Direction.EAST));
        visible.addAll(Map.possibleMovementsSpecificDirection(startPoint, dist, map, Direction.SOUTH));
        visible.addAll(Map.possibleMovementsSpecificDirection(startPoint, dist, map, Direction.WEST));

        return visible;
    }

    /**
     * Return the list of possible movements that are in a single direction, it looks for a specific direction
     * @param startPoint Point from which the movement starts
     * @param dist Maximum distance in number of steps
     * @param map Map on which the steps will be calculated
     * @param dir the direction where you want to look for
     * @return List of possible destinations
     */
    private static List<Point> possibleMovementsSpecificDirection(Point startPoint, int dist, Map map, Direction dir){
        List<Point> visible = new ArrayList<>();

        if(map.getCell(startPoint) != null) {
            switch (dir) {
                case NORTH:
                    if (map.getCell(startPoint).getSides()[Direction.NORTH.ordinal()] != Side.WALL) {
                        visible.add(new Point(startPoint.getX(), startPoint.getY() - 1));

                        if(dist>0)
                            visible.addAll(Map.possibleMovementsSpecificDirection(new Point(startPoint.getX(), startPoint.getY() - 1), dist-1, map, dir));

                        return visible;
                    }
                    break;
                case EAST:
                    if (map.getCell(startPoint).getSides()[Direction.EAST.ordinal()] != Side.WALL) {
                        visible.add(new Point(startPoint.getX()+1, startPoint.getY()));

                        if(dist>0)
                            visible.addAll(Map.possibleMovementsSpecificDirection(new Point(startPoint.getX()+1, startPoint.getY()), dist-1, map, dir));

                        return visible;
                    }
                    break;
                case SOUTH:
                    if (map.getCell(startPoint).getSides()[Direction.SOUTH.ordinal()] != Side.WALL) {
                        visible.add(new Point(startPoint.getX(), startPoint.getY() + 1));

                        if(dist>0)
                            visible.addAll(Map.possibleMovementsSpecificDirection(new Point(startPoint.getX(), startPoint.getY() + 1), dist-1, map, dir));

                        return visible;
                    }
                    break;
                case WEST:
                    if (map.getCell(startPoint).getSides()[Direction.WEST.ordinal()] != Side.WALL) {
                        visible.add(new Point(startPoint.getX() - 1, startPoint.getY()));

                        if(dist>0)
                            visible.addAll(Map.possibleMovementsSpecificDirection(new Point(startPoint.getX() - 1, startPoint.getY()), dist-1, map, dir));

                        return visible;
                    }
                    break;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Find the second point in the same direction the first has been found from the start
     * @param start start point
     * @param first first point found
     * @return the second point in the same direction as the first
     */
    public static Point nextPointSameDirection(Point start, Point first){
        //Find the next X&Y in the same direction, it's needed for the second part of the effect
        int nX = first.getX();
        int nY = first.getY();
        if(start.getY()-1 == first.getY())
            nY--;
        else if(start.getX()+1 == first.getX())
            nX++;
        else if(start.getY()+1 == first.getY())
            nY++;
        else if(start.getX()-1 == first.getX())
            nX--;

        try{
            Point p = new Point(nX, nY);
            return p;
        }catch(WrongPointException e){
            return null;
        }
    }

    /**
     * Return the list of points visible from the startPoint plus all the ones at a maximum distance from a visible one
     * that is less than notVisDist
     * @param startPoint starting point
     * @param map map where you need to look
     * @param notVisDist if 0 the method returns only visible points, otherwise it returns also points that has a notVisDist as max dist from a visible point
     * @return List of possible destinations
     */
    public static List<Point> visiblePoints(Point startPoint, Map map, int notVisDist){
        List<Integer> visRooms = Map.visibleRooms(startPoint, map);
        List<Point> points = new ArrayList<>();

        //Get all the visible points
        for(int i=0; i<=3;i++)
            for(int j=0; j<=2; j++)
                if(map.getCell(i,j) != null)
                    if(visRooms.contains(map.getCell(i,j).getRoomNumber()))
                        points.add(new Point(i,j));

        if(notVisDist > 0){
            HashSet<Point> notVisible = new HashSet<>();
            for(Point p:points)
                notVisible.addAll(Map.possibleMovements(p, notVisDist, map));

            notVisible.addAll(points);

            return new ArrayList<>(notVisible);
        }

        return points;
    }

}
