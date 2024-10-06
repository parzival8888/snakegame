package org.snake.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.snake.util.Cell;

import java.util.ArrayList;

public class SnakeTest {
    private Snake snake;
    @BeforeEach
    public void setUp() {
        // Initialize a Snake object before each test
        snake = new Snake(5, 5, 3); // Starting at (5, 5) with a length of 3
    }

    @Test
    public void testInitialSnakeLength() {
        assertEquals(3, snake.getSnakeLength(), "Initial snake length should be 3 (1 head + 2 body segments)");
    }

    @Test
    public void testGetSnakeHead() {
        Cell head = snake.getSnakeHead();
        assertNotNull(head, "Snake head should not be null");
        assertEquals(5, head.getX(), "Snake head X coordinate should be 5");
        assertEquals(5, head.getY(), "Snake head Y coordinate should be 5");
    }

    @Test
    public void testGetBodySegment() {
        Cell bodySegment = snake.getBodySegment(1); // Get the first body segment
        assertNotNull(bodySegment, "Body segment should not be null");
        assertEquals(5, bodySegment.getX(), "Body segment X coordinate should be 5");
        assertEquals(6, bodySegment.getY(), "Body segment Y coordinate should be 6");
    }

    @Test
    public void testAddBodySegment() {
        Cell newSegment = new Cell(5, 8); // New body segment to add
        snake.addBodySegment(newSegment);
        
        ArrayList<Cell> snakeSegments = snake.getSnake();
        assertEquals(4, snakeSegments.size(), "Snake length should now be 4 after adding a new segment");
        assertEquals(newSegment, snakeSegments.get(snakeSegments.size() - 1), "Last segment should be the newly added segment");
    }

    @Test
    public void testGetSnake() {
        ArrayList<Cell> segments = snake.getSnake();
        assertNotNull(segments, "Snake segments should not be null");
        assertEquals(3, segments.size(), "Initial number of segments should be 3 (1 head + 2 body segments)");
        
        // Check if the first segment is the head
        assertEquals(snake.getSnakeHead(), segments.get(0), "First segment should be the head of the snake");
    }
}