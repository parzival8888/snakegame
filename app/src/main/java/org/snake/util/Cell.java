package org.snake.util;

/**
 * Represents a cell in a 2D grid with x and y coordinates.
 * This class is used to define positions on the game board, such as the snake's head,
 * body segments, and food locations.
 */
public class Cell {
    private int x; 
    private int y; 

    /**
     * Constructs a Cell object with specified x and y coordinates.
     *
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the x-coordinate of this cell.
     *
     * @return The x-coordinate as an integer.
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the y-coordinate of this cell.
     *
     * @return The y-coordinate as an integer.
     */
    public int getY() {
        return y;
    }
    
    /**
     * Sets the x-coordinate of this cell to a new value.
     *
     * @param x The new x-coordinate to set.
     */
    public void setX(int x) {
        this.x = x;
    }
    
    /**
     * Sets the y-coordinate of this cell to a new value.
     *
     * @param y The new y-coordinate to set.
     */
    public void setY(int y) {
        this.y = y;
    }
}
