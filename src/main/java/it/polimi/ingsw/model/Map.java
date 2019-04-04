package it.polimi.ingsw.model;

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
        loadFromFile(filePath);
    }

    /**
     * the function deserialize the JSON config file and loads the data in the matrix
     * @param path JSON map config file
     */
    private void loadFromFile(String path)
    {
        //TODO: implement JSON deserialization
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
