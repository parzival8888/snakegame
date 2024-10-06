package org.snake.model;

import java.util.ArrayList;
import org.snake.util.Cell;

/**
 * Represents a Snake in a game, consisting of a head and a series of body segments.
 * The Snake can grow by adding new body segments, and its length can be queried.
 */
public class Snake {
    private ArrayList<Cell> snake;
    private Cell snakeHead; 

    /**
     * Constructs a Snake object with a specified starting position and length.
     *
     * @param x          The x-coordinate of the snake's head.
     * @param y          The y-coordinate of the snake's head.
     * @param snakeLength The initial length of the snake (including the head).
     */
    public Snake(int x, int y, int snakeLength) {
        snake = new ArrayList<Cell>();
        snakeHead = new Cell(x, y);
        snake.add(snakeHead);
        for (int i = 1; i < snakeLength; i++) {
            Cell bodySegment = new Cell(x, y + i);
            snake.add(bodySegment);
        }
    }

    /**
     * Returns the current length of the snake.
     *
     * @return The number of segments in the snake, including the head.
     */
    public int getSnakeLength() {
        return snake.size();
    }

    /**
     * Retrieves all segments of the snake as an ArrayList.
     *
     * @return An ArrayList containing all segments of the snake.
     */
    public ArrayList<Cell> getSnake() {
        return snake;
    }

    /**
     * Gets the head segment of the snake.
     *
     * @return The Cell object representing the head of the snake.
     */
    public Cell getSnakeHead() {
        return snakeHead;
    }

    /**
     * Retrieves a specific body segment of the snake based on its index.
     *
     * @param x The index of the body segment to retrieve (0 for head, 1 for first body segment, etc.).
     * @return The Cell object representing the specified body segment.
     */
    public Cell getBodySegment(int x) {
        return snake.get(x);
    }

    /**
     * Adds a new body segment to the end of the snake.
     *
     * @param bodySegment The Cell object representing the new body segment to add.
     */
    public void addBodySegment(Cell bodySegment) {
        snake.add(bodySegment);
    }
}