package it.polimi.ingsw.model;

public class Point {
    private int x;
    private int y;

    Point(int x, int y)
    {
        set(x, y);
    }

    public void set(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
