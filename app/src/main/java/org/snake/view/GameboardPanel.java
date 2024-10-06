package org.snake.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.snake.model.Snake;
import org.snake.model.SnakegameModel;
import org.snake.util.Cell;

/**
 * GameboardPanel is a JPanel that represents the game board for the Snake game.
 * It handles rendering the game elements such as the snake, food, and game
 * state,
 * as well as user input for controlling the snake's movement.
 */
public class GameboardPanel extends JPanel {

    private SnakegameModel model;
    private Timer controlTimer;
    private Timer gameTimer;
    private Snake snake;
    private Cell snakeHead;
    private Cell food;
    private int cellSize;
    private int timerInterval;
    private int gameTime;
    private char direction;
    private JLabel scoreLabel;
    private JLabel gameTimerLabel;
    private JLabel sessionTimerLabel;
    private static int sessionTimerInterval = 1000;

    /**
     * Constructs a GameboardPanel with the specified model and labels.
     *
     * @param model             The SnakegameModel containing game logic.
     * @param scoreLabel        The JLabel to display the score.
     * @param gameTimerLabel    The JLabel to display the game time.
     * @param sessionTimerLabel The JLabel to display session time.
     */
    public GameboardPanel(SnakegameModel model, JLabel scoreLabel, JLabel gameTimerLabel, JLabel sessionTimerLabel) {
        this.model = model;
        this.scoreLabel = scoreLabel;
        this.gameTimerLabel = gameTimerLabel;
        this.sessionTimerLabel = sessionTimerLabel;
        this.timerInterval = model.getTimerInterval();
        this.addKeyListener(new MyKeyAdapter());
    }

    /**
     * Paints the component, rendering the game board, snake, food, and game over
     * message if applicable.
     *
     * @param graphic The Graphics context used for painting.
     */
    @Override
    protected void paintComponent(Graphics graphic) {
        super.paintComponent(graphic);
        setFocusable(true); // Make sure the panel can receive key events
        requestFocusInWindow(); // Request focus for key events

        if (model.isNewGame()) {
            model.setNewGame(false);
            model.startNewGame();
            prepareBoard(graphic);
            startGame();
        }

        prepareBoard(graphic); // Prepare and draw board

        // Draw the snake head
        graphic.setColor(Color.GREEN);
        graphic.fillRect(snakeHead.getX() * cellSize, snakeHead.getY() * cellSize, cellSize, cellSize);

        // Draw the snake body
        for (Cell bodySegment : snake.getSnake()) {
            graphic.fillRect(bodySegment.getX() * cellSize, bodySegment.getY() * cellSize, cellSize, cellSize);
        }

        // Draw the food
        graphic.setColor(Color.RED);
        graphic.fillRect(food.getX() * cellSize, food.getY() * cellSize, cellSize, cellSize);

        // Display game over message if applicable
        if (model.isGameOver()) {
            int y = this.getHeight() / 2;
            Font font = new Font("Arial", Font.PLAIN, 24);
            graphic.setFont(font);
            int x;

            if (model.isTimeAllocationUsed()) {
                x = (this.getWidth() - 600) / 2;
                graphic.drawString("Game Over. You have used your time allocation for today!", x, y);
            } else {
                x = (this.getWidth() - 100) / 2;
                graphic.drawString("Game Over", x, y);
            }
        }
    }

    /**
     * Prepares the game board by drawing it and setting up grid lines.
     *
     * @param graphic The Graphics context used for drawing.
     */
    private void prepareBoard(Graphics graphic) {
        int boardSize = model.getBoardSize();
        int numColumns = model.getNumberOfColumns();
        cellSize = model.getCellSize();

        // Draw the game board background
        graphic.setColor(model.getBoardColour());
        graphic.fillRect(0, 0, boardSize, boardSize);

        // Draw a grid pattern
        graphic.setColor(model.getBoardGridColour());
        for (int i = 0; i <= numColumns; i++) {
            int x = i * cellSize;
            graphic.drawLine(x, 0, x, boardSize); // Vertical lines

            int y = i * cellSize;
            graphic.drawLine(0, y, boardSize, y); // Horizontal lines
        }

        // Draw walls around the board
        graphic.setColor(Color.BLACK);
        for (int i = 0; i < numColumns; i++) {
            graphic.fillRect(0, i * cellSize, cellSize, cellSize); // Left wall
            graphic.fillRect(i * cellSize, 0, cellSize, cellSize); // Top wall
            graphic.fillRect((numColumns - 1) * cellSize, i * cellSize, cellSize, cellSize); // Right wall
            graphic.fillRect(i * cellSize, (numColumns - 1) * cellSize, cellSize, cellSize); // Bottom wall
        }
    }

    /**
     * Starts a new game by initializing scores and placing the snake and food on
     * the board.
     */
    public void startGame() {
        scoreLabel.setText("Score: " + model.getCurrentScore());

        // Positioning snake head randomly on the board
        snake = model.getSnake();
        snakeHead = snake.getSnakeHead();

        System.out.println("Snake head positioned at x: " + snakeHead.getX());

        // Positioning food randomly on the board
        food = model.getFoodLocation();

        System.out.println("Food positioned at x: " + food.getX());

        if (controlTimer != null && !controlTimer.isRunning()) {
            controlTimer.restart(); // Restart timer if already created and not running
        } else if (controlTimer == null) {
            controlTimer = new Timer(timerInterval, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveSnake();
                    setFocusable(true);
                    requestFocusInWindow();
                    repaint();

                    if (model.isGameOver()) {
                        handleGameOver();
                    }
                }
            });
            controlTimer.start(); // Start control timer

            sessionTimerLabel.setText("Session time: " + model.getCurrentSessionTime());

            if (gameTimer == null) {
                gameTimer = new Timer(sessionTimerInterval, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        gameTime++;
                        gameTimerLabel.setText("Game time: " + gameTime);
                    }
                });
                gameTimer.start();
            }
        }
    }

    /**
     * Handles actions when the game is over.
     */
    private void handleGameOver() {
        controlTimer.stop();
        if (!(this.gameTimer == null)) {
            gameTimer.stop();
            gameTimer = null;
        }

        model.storeGameTime(gameTime);
        sessionTimerLabel.setText("Session time: " + model.getCurrentSessionTime());
        gameTime = 0;
    }

    /**
     * Moves the snake in its current direction and checks for collisions with food
     * or walls.
     */
    private void moveSnake() {
        if (!(snakeHead == null)) {

            direction = model.getDirection();
            Cell storeSnakeHead = new Cell(snakeHead.getX(), snakeHead.getY());

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
            for (int i = snake.getSnakeLength() - 1; i > 0; i--) {
                Cell bodySegment = snake.getBodySegment(i);
                Cell prevBodySegment = snake.getBodySegment(i - 1);
                // Move the first body segment to the previous location of the head
                if (i == 1) {
                    prevBodySegment = storeSnakeHead;
                }
                bodySegment.setX(prevBodySegment.getX());
                bodySegment.setY(prevBodySegment.getY());
            }

            // Check for collisions with the game walls and the snake body
            model.isCollisionWall();
            model.isCollisionBody();
        }
    }

    /**
     * Key adapter class to handle user input from keyboard events.
     */
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

                case KeyEvent.VK_P:
                    if (controlTimer != null && controlTimer.isRunning()) {
                        controlTimer.stop(); // Pause the game if running
                    } else {
                        controlTimer.restart(); // Restart timer if paused or stopped
                    }
                    break;
            }
        }
    }
}