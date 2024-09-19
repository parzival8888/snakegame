package org.snake.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void testConstructor() {
        // Arrange & Act
        Cell cell = new Cell(5, 10);

        // Assert
        assertEquals(5, cell.getX());
        assertEquals(10, cell.getY());
    }

    @Test
    void testSetX() {
        // Arrange
        Cell cell = new Cell(0, 0);

        // Act
        cell.setX(7);

        // Assert
        assertEquals(7, cell.getX());
    }

    @Test
    void testSetY() {
        // Arrange
        Cell cell = new Cell(0, 0);

        // Act
        cell.setY(15);

        // Assert
        assertEquals(15, cell.getY());
    }

    @Test
    void testGetX() {
        // Arrange
        Cell cell = new Cell(3, 6);

        // Act & Assert
        assertEquals(3, cell.getX());
    }

    @Test
    void testGetY() {
        // Arrange
        Cell cell = new Cell(3, 6);

        // Act & Assert
        assertEquals(6, cell.getY());
    }
}
