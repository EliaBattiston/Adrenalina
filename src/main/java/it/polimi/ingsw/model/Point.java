package it.polimi.ingsw.model;

/**
 * Coordinates of a Player in the Map. Because of its use X values have to be between 0 and 2, Y values between 0 and 3
 */
public class Point {
    /**
     * X coordinate
     */
    private int x;
    /**
     * Y coordinate
     */
    private int y;

    /**
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    Point(int x, int y)
    {
        //TODO: add error checking
        set(x, y);
    }

    /**
     * Sets the two coordinates
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void set(int x, int y)
    {
        if(x>=0 && x<=2 && y>=0 && y<= 3)
        {
            this.x = x;
            this.y = y;
        }
        else
        {
            //TODO: add error
        }
    }

    /**
     *
     * @return X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @return Y coordinate
     */
    public int getY() {
        return y;
    }

}
