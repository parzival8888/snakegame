package org.snake;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import org.snake.util.ConfigReader;

public class SnakegameModel {
    private static String configFilename = "snakegame.config";
    private String gameTitle;
    private int boardSize;
    private int numberOfColumns;
    private int cellSize;
    private Color boardColour;
    private Color boardGridColour;
    private boolean gameOver;
    private boolean newGame;
    private int timerInterval;
    private char direction;
    private int snakeLength;

    // The snake, with the head in the first position
    private ArrayList<Cell> snake;
    private Cell food;
    private static final Random randomNumberGenerator = new Random();

    public SnakegameModel() {
        readConfig();
        this.gameOver = false;
    }

    private void readConfig() {
        ConfigReader.readConfig(configFilename);
        this.gameTitle = ConfigReader.getProperty("gametitle");
        this.boardSize = Integer.parseInt(ConfigReader.getProperty("boardsize"));
        this.numberOfColumns = Integer.parseInt(ConfigReader.getProperty("numberofcolumns"));
        this.snakeLength = Integer.parseInt(ConfigReader.getProperty("snakelength"));
        this.cellSize = this.boardSize / this.numberOfColumns;
        this.boardColour = Color.decode(ConfigReader.getProperty("boardcolour"));
        this.boardGridColour = Color.decode(ConfigReader.getProperty("boardgridcolour"));
        this.timerInterval = Integer.parseInt(ConfigReader.getProperty("timerinterval"));
        this.direction = ConfigReader.getProperty("startdirection").charAt(0);
        System.out.println("game title is: " + this.gameTitle);
        System.out.println("board size is: " + this.boardSize);
        System.out.println("time interval is" + this.timerInterval);
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public int getTimerInterval() {
        return timerInterval;
    }

    public int getSnakeLength() {
        return snakeLength;
    }

    public int getCellSize() {
        return cellSize;
    }

    public Color getBoardColour() {
        return boardColour;
    }

    public Color getBoardGridColour() {
        return boardGridColour;
    }

    public Cell getFoodLocation() {
        return food;
    }

    public ArrayList<Cell> getSnake() {
        return snake;
    }

    public char getDirection() {
        return direction;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    /**
     * Check whether two Cells have collided. This could be the
     * snake head colliding with food, or the snake head colliding
     * with its own body, or with a wall.
     * 
     * A collision occurs when the 'x' and 'y' coordinates of both Cells
     * are identical.
     * 
     * @param cell1 - the first Cell
     * @param cell2 - the second Cell
     * @return a boolean indicating whether the two Cells refer to the same location
     */



    /**
     * Initialise the snake with the snake head at position 0 in the ArrayList
     * 
     * @return an ArrayList of Cells representing the snake body with the snake head in position 0
     */ 
    public ArrayList<Cell> initialiseSnake() {
        int x = randomNumberGenerator.nextInt(boardSize / cellSize);
        int y = randomNumberGenerator.nextInt(boardSize / cellSize);
        snake = new ArrayList<Cell>();
        Cell snakeHead = new Cell(x, y);
        snake.add(snakeHead);
        for (int i = 0; i < snakeLength; i++) {
            Cell bodySegment = new Cell(x, y + i);
            snake.add(bodySegment); 
        }
        System.out.println("Snake length in initialiseSnake is: " + snake.size());
        return snake;
    }

    public Cell placeFood() {
        int x = randomNumberGenerator.nextInt(boardSize / cellSize);
        int y = randomNumberGenerator.nextInt(boardSize / cellSize);
        food = new Cell(x, y);
        return food;
    }

    public void startNewGame() {
        this.initialiseSnake();
        this.placeFood();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setNewGame(boolean newGame) {
        this.newGame = newGame;
    }

    public boolean getNewGame() {
        return newGame;
    }

}
