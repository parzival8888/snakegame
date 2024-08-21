package org.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameboardPanel extends JPanel {
    private SnakegameModel model;
    private Timer timer;
    private ArrayList<Cell> snake;
    private Cell snakeHead;
    private Cell food;
    private int cellSize;

    public GameboardPanel(SnakegameModel model) {
        this.model = model;
        // Create a timer that moves the snake. The timer delay is configurable
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSnake();
                repaint(); // Request a repaint
            }
        });
        timer.start(); // Start the timer
    }

    @Override
    protected void paintComponent(Graphics graphic) {
        super.paintComponent(graphic);
        if (model.getNewGame()) {
            model.startNewGame();
            prepareBoard(graphic);
            startGame();
            model.setNewGame(false);
        } 
        prepareBoard(graphic);
        // Draw the snake head
        graphic.setColor(Color.GREEN);
        graphic.fillRect(snakeHead.getX() * cellSize, snakeHead.getY() * cellSize, cellSize, cellSize);
        
        // Draw the food

    }

    private void prepareBoard(Graphics graphic) {
        // Draw the game board
        graphic.setColor(model.getBoardColour());
        graphic.fillRect(0, 0, model.getBoardSize(), model.getBoardSize());

        // Draw a grid pattern to allow the player to see each of the cells on the board
        // Draw vertical lines
        graphic.setColor(model.getBoardGridColour());
        for (int i = 0; i <= model.getNumberOfColumns(); i++) {
            int x = i * cellSize;
            graphic.drawLine(x, 0, x, model.getBoardSize());
        }

        // Draw horizontal lines
        for (int i = 0; i <= model.getNumberOfColumns(); i++) {
            int y = i * cellSize;
            graphic.drawLine(0, y, model.getBoardSize(), y);
        }
        cellSize = model.getCellSize();
    }

    private void startGame() {
        // Place the snake head randomly on the game board
        snake = model.getSnake();
        snakeHead = snake.get(0);
        System.out.println("Snake head positioned at x: " + snakeHead.getX());

        // Place a food item randomly on the game board
    }

    private void moveSnake() {
        if (!(snakeHead == null)) {
            snakeHead.setX(snakeHead.getX() + 1);
            System.out.println("Moving snake to x: " + snakeHead.getX());    
        }
    }
}
