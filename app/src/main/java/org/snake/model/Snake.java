package org.snake.model;

import java.util.ArrayList;

import org.snake.util.Cell;

public class Snake {
    private ArrayList<Cell> snake;
    private Cell snakeHead;

    public Snake(int x, int y) {
        snake = new ArrayList<Cell>();
        snakeHead = new Cell(x, y);
        snake.add(snakeHead);
    }

    public int getSnakeLength() {
        return snake.size();
    }

    public ArrayList<Cell> getSnake() {
        return snake;
    }

    public Cell getSnakeHead() {
        return snakeHead;
    }
    public Cell getBodySegment(int x) {
        return snake.get(x);
    }

    public void createSnakeBody(int snakeLength) {
        int x = snakeHead.getX();
        int y = snakeHead.getY();
        for (int i = 0; i < snakeLength; i++) {
            Cell bodySegment = new Cell(x, y + i);
            snake.add(bodySegment);
        }
    }

    public void addBodySegment(Cell bodySegment) {
        snake.add(bodySegment);
    }

}