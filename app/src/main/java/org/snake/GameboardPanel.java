package org.snake;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameboardPanel extends JPanel {
    private SnakegameModel model;
    private Timer controlTimer;
    private Timer gameTimer;
    private ArrayList<Cell> snake;
    private Cell snakeHead;
    private Cell food;
    private int cellSize;
    private int timerInterval;
    private int gameTime;
    private char direction;
    private JLabel scoreLabel;
    private JLabel gameTimerLabel;
    private JLabel sessionTimerLabel;

    private static int sessionTimerInterval = 1000; // Will always be 1 second

    public GameboardPanel(SnakegameModel model, JLabel scoreLabel, JLabel gameTimerLabel, JLabel sessionTimerLabel) {
        this.model = model;
        this.scoreLabel = scoreLabel;
        this.gameTimerLabel = gameTimerLabel;
        this.sessionTimerLabel = sessionTimerLabel;
        this.timerInterval = model.getTimerInterval();
        this.addKeyListener(new MyKeyAdapter()); // Key listener to handle direction changes via arrow keys
    }

    @Override
    protected void paintComponent(Graphics graphic) {
        super.paintComponent(graphic);
        setFocusable(true); // Make sure the panel can receive key events
        requestFocusInWindow(); // Request focus for key events
        if (model.getNewGame()) {
            model.setNewGame(false);
            model.startNewGame();
            prepareBoard(graphic);
            startGame();
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

        // Display game over
        if (model.isTimeAllocationUsed()) {
            graphic.drawString("Game Over. You have used your time allocation for today!", 5, 10);
        } else if (model.isGameOver()) {
            String gameOverMessage = "Game Over";
            Font stringFont = new Font("SansSerif", Font.PLAIN, 30);
            graphic.setFont(stringFont);
            graphic.drawString(gameOverMessage, 280, 220);

        }
    }

    private void prepareBoard(Graphics graphic) {
        int boardSize = model.getBoardSize();
        int numColumns = model.getNumberOfColumns();
        cellSize = model.getCellSize();

        // Draw the game board
        graphic.setColor(model.getBoardColour());
        graphic.fillRect(0, 0, boardSize, boardSize);

        // Draw a grid pattern
        graphic.setColor(model.getBoardGridColour());
        for (int i = 0; i <= numColumns; i++) {
            int x = i * cellSize;
            graphic.drawLine(x, 0, x, boardSize);
        }

        // Draw horizontal lines
        for (int i = 0; i <= numColumns; i++) {
            int y = i * cellSize;
            graphic.drawLine(0, y, boardSize, y);
        }

        // Draw the game walls
        for (int i = 0; i < numColumns; i++) {
            graphic.setColor(Color.BLACK);
            graphic.fillRect(0, i * cellSize, cellSize, cellSize);
            graphic.fillRect(i * cellSize, 0, cellSize, cellSize);
            graphic.fillRect((numColumns - 1) * cellSize, i * cellSize, cellSize, cellSize);
            graphic.fillRect(i * cellSize, (numColumns - 1) * cellSize, cellSize, cellSize);
        }
    }

    public void startGame() {
        scoreLabel.setText("Score: " + model.getCurrentScore());

        // Place the snake head randomly on the game board
        snake = model.getSnake();
        snakeHead = snake.get(0);
        System.out.println("Snake head positioned at x: " + snakeHead.getX());

        // Place a food item randomly on the game board
        food = model.getFoodLocation();
        System.out.println("Food positioned at x: " + food.getX());

        // Create a timer that moves the snake. The timer delay is configurable
        // Handle game restart. If the timer has already been created, restart it
        if (controlTimer != null && !controlTimer.isRunning()) {
            controlTimer.restart();
        } else if (controlTimer == null) {
            controlTimer = new Timer(timerInterval, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveSnake();
                    setFocusable(true); // Make sure the panel can receive key events
                    requestFocusInWindow(); // Request focus for key events
                    repaint(); // Request a repaint
                    if (model.isGameOver()) {
                        handleGameOver();
                    }
                }
            });
            controlTimer.start(); // Start the timer
        }
        // Display the total game play time for the current day
        sessionTimerLabel.setText("Session time: " + model.getCurrentSessionTime());

        // Create a timer that tracks time played for each game
        if (gameTimer == null) {
            gameTimer = new Timer(sessionTimerInterval, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Update the label with the elapsed time
                    gameTime++;
                    gameTimerLabel.setText("Game time: " + gameTime);
                }
            });
            gameTimer.start(); // Start the timer
        }
    }

    private void handleGameOver() {
        controlTimer.stop();
        gameTimer.stop();
        gameTimer = null;
        model.storeGameTime(gameTime);
        // Display the total game play time for the current day
        sessionTimerLabel.setText("Session time: " + model.getCurrentSessionTime());
        gameTime = 0;
    }

    private void moveSnake() {
        if (!(snakeHead == null)) {
            // Move the snake head...see the comments above

            direction = model.getDirection();
            switch (direction) {
                case 'U':
                    snakeHead.setY(snakeHead.getY() - 1);
                    break;
                case 'D':
                    snakeHead.setY(snakeHead.getY() + 1);
                    break;
                case 'L':
                    snakeHead.setX(snakeHead.getX() - 1);
                    break;
                case 'R':
                    snakeHead.setX(snakeHead.getX() + 1);
                    break;
            }

            // Check for collision between snake head and food
            if (model.isCollisionFood()) {
                scoreLabel.setText("Score: " + model.getCurrentScore());
                food = model.placeFood();
            }

            // Move the snake body
            for (int i = snake.size() - 1; i > 0; i--) {
                Cell bodySegment = snake.get(i);
                Cell prevBodySegment = snake.get(i - 1);
                bodySegment.setX(prevBodySegment.getX());
                bodySegment.setY(prevBodySegment.getY());
            }

            // Check for collisions with the game walls and the snake body
            model.isCollisionWall();
            model.isCollisionBody();
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
                    if (model.getDirection() != 'R') {
                        model.setDirection('L');
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (model.getDirection() != 'L') {
                        model.setDirection('R');
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (model.getDirection() != 'D') {
                        model.setDirection('U');
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (model.getDirection() != 'U') {
                        model.setDirection('D');
                    }
                    break;
            }
        }
    }
}