package it.polimi.ingsw.clientmodel;

import java.io.Serializable;


/**
 * Immutable map which can be sent to a player's client
 */
public class MapView implements Serializable
{
    private int id;

    private CellView[][] cells;

    //Defines block
    private transient static final int mapWidth = 4;
    private transient static final int mapHeight = 3;

    public MapView(int id, CellView[][] cv)
    {
        this.id = id;
        this.cells = cv;
    }

    public int getId()
    {
        return id;
    }

    /**
     * Returns the reference of a selected cell
     * @param x X-coordinate of the cell
     * @param y Y-coordinate of the cell
     * @return cell reference (or null in case of out of bound coordinates or missing cell)
     */
    public CellView getCell(int x, int y) {
        if(x >= 0 && x < mapWidth && y >= 0 && y < mapHeight)
            return cells[x][y];
        return null;
    }
}
