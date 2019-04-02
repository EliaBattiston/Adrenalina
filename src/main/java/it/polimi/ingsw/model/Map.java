package it.polimi.ingsw.model;

public class Map {
    private Cell[][] cells;

    Map(String filePath)
    {
        loadFromFile(filePath);
    }

    private void loadFromFile(String path)
    {}

    public Cell getCell(int x, int y)
    {
        return cells[0][0];
    }
}
