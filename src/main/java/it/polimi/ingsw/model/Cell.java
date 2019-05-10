package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ClientDisconnectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * The class represent the single cell of the board
 */
public abstract class Cell {
    /**
     * The attribute represents the sides of the cell (wall, door or nothing), ordered NORTH-EAST-SOUTH-WEST
     */
    private Side[] sides;
    /**
     * The attribute represents the players positioned in the cell
     */
    private List<Player> pawns;
    /**
     * The attribute represents the Room Number (needed to distinguish the cells composing each room)
     */
    private int roomNumber;

    /**
     * The constructor instantiate a cell without players inside, with the given sides and room number
     * @param sides array describing the sides of the cell
     * @param roomNumber identification number for the room
     */
    Cell(Side[] sides, int roomNumber)
    {
        this.sides = sides;
        this.roomNumber = roomNumber;
        pawns = new ArrayList<>();
    }

    /**
     * Returns an array with the sides of the cell, ordered N-E-S-W
     * @return array of sides
     */
    public Side[] getSides() {
        return sides;
    }

    /**
     * Returns a list with the players inside the cell
     * @return list of players in the cell
     */
    public List<Player> getPawns() {
        return pawns;
    }

    /**
     * returns the room identification number
     * @return room id number
     */
    public int getRoomNumber() {
        return roomNumber;
    }

    /**
     * Add a player to the cell (the player moved into the cell)
     * @param pl player to be added in the cell
     */
    public void addPawn(Player pl)
    {
        pawns.add(pl);
    }

    /**
     * The function removes the given player from the cell (if exists)
     * @param pl player to be removed
     */
    public void removePawn(Player pl)
    {
        if(pawns.contains(pl)) {
            pawns.remove(pl);
        }
    }

    /**
     * The function tells whether it is worth to move in this cell for picking up items
     * @param pl Player who would like to pick an item
     * @return True if the cell has items, false otherwise
     */
    public abstract boolean hasItems(Player pl);

    /**
     * Executes the acquisition of an item from the cell
     * @param pl Player who picks
     * @param lootDeck Loot cards' deck
     * @param powersDeck Power cards' deck
     */
    public abstract void pickItem(Player pl, EndlessDeck<Loot> lootDeck, EndlessDeck<Power> powersDeck) throws ClientDisconnectedException;

    /**
     * Refill the cell's items if needed
     * @param game Game which contains needed decks
     */
    public abstract void refill(Game game);

    /**
     * Tells if the cell has a spawn point of color c
     * @param c Desired spawn color
     * @return True if the cell has the desired spawn point
     */
    public abstract boolean hasSpawn(Color c);

}
