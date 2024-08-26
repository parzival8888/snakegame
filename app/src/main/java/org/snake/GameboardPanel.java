package org.snake;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameboardPanel extends JPanel {
    private SnakegameModel model;
    private Timer timer;
    private ArrayList<Cell> snake;
    private Cell snakeHead;
    private Cell food;
    private int cellSize;
    private int timerInterval;

    public GameboardPanel(SnakegameModel model) {
        this.model = model;
        this.timerInterval = model.getTimerInterval();
        this.addKeyListener(new MyKeyAdapter());
        // Key listener to handle direction changes

        // Create a timer that moves the snake. The timer delay is configurable
        timer = new Timer(timerInterval, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSnake();
                repaint(); // Request a repaint
                if(model.isGameOver()){
                    timer.stop();
                }
            }
        });
        timer.start(); // Start the timer
    }

    @Override
    protected void paintComponent(Graphics graphic) {
        super.paintComponent(graphic);
        setFocusable(true); // Make sure the panel can receive key events
        requestFocusInWindow(); // Request focus for key events
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

        // Draw the snake body
        for (Cell bodySegment : snake) {
            graphic.fillRect(bodySegment.getX() * cellSize, bodySegment.getY() * cellSize, cellSize, cellSize);
        }

        // Draw the food
        graphic.setColor(Color.RED);
        graphic.fillRect(food.getX() * cellSize, food.getY() * cellSize, cellSize, cellSize);
        if (model.isGameOver()){
            graphic.drawString("Game Over", 5, 10);
        }
    }

    private void prepareBoard(Graphics graphic) {
        // Draw the game board
        graphic.setColor(model.getBoardColour());
        graphic.fillRect(0, 0, model.getBoardSize(), model.getBoardSize());

        // Draw a grid pattern
        graphic.setColor(model.getBoardGridColour());
        for (int i = 0; i <= model.getNumberOfColumns(); i++) {
            int x = i * cellSize;
            graphic.drawLine(x, 0, x, model.getBoardSize());
        }
        for (int i = 0; i <= model.getNumberOfColumns(); i++) {
            int y = i * cellSize;
            graphic.drawLine(0, y, model.getBoardSize(), y);
        }
        cellSize = model.getCellSize();
    }

    private void startGame() {
        // Initialize the snake and its starting position
        snake = new ArrayList<>();
        snake.add(new Cell(5, 5)); // Example starting position
        snakeHead = snake.get(0);
        System.out.println("Snake head positioned at x: " + snakeHead.getX());

        // Place a food item randomly on the game board
        food = model.getFoodLocation();
        System.out.println("Food positioned at x: " + food.getX());
    }

    private void moveSnake() {
        if (snakeHead != null) {
            // Get the current direction from the model
            String direction = model.getDirection();

            // Move the snake head based on the direction
            switch (direction) {
                case "U": // Up
                    snakeHead.setY(snakeHead.getY() - 1);
                    break;
                case "D": // Down
                    snakeHead.setY(snakeHead.getY() + 1);
                    break;
                case "L": // Left
                    snakeHead.setX(snakeHead.getX() - 1);
                    break;
                case "R": // Right
                    snakeHead.setX(snakeHead.getX() + 1);
                    break;
            }

            // Move the snake body
            for (int i = snake.size() - 1; i > 0; i--) {
                Cell bodySegment = snake.get(i);
                Cell prevBodySegment = snake.get(i - 1);
                bodySegment.setX(prevBodySegment.getX());
                bodySegment.setY(prevBodySegment.getY());
            }

            // Check for collision between snake head and food
            if (model.isCollision(snakeHead,food)){
               food = model.placeFood();
    
            }
            model.isCollisionWall(snakeHead);

        }
    }
        
           // Handle pressing the arrow keys
    // First, get the snake direction from the model
    // Then use a 'switch' statement on the key pressed using e.getKeyCode()
    // If the left key was pressed, KeyEvent.VK_LEFT, then set the snake direction
    // to 'L'
    // But only if it wasn't previously 'R', otherwise the snake will double back on
    // itself, which isn't allowed
    // The same for the other arrow keys
    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (!model.getDirection().equals("R")) {
                        model.setDirection("L");
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (!model.getDirection().equals("L")) {
                        model.setDirection("R");
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (!model.getDirection().equals("D")) {
                        model.setDirection("U");
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (!model.getDirection().equals("U")) {
                        model.setDirection("D");
                    }
                    break;
            }
        }
    };
    
    
    
}