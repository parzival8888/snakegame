package org.snake;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import org.snake.util.ConfigReader;
import org.snake.util.DataHandler;

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
    private boolean dailyTimeUsed;
    private int timerInterval;
    private char direction;
    private int snakeLength;
    private int wallTop;
    private int wallBottom;
    private int wallLeft;
    private int wallRight;
    private int currentScore;
    private int gameTimeAllowed;
    private int topSomething;
    private int currentSessionTime;
    private DataHandler dataHandler;

    // The snake, with the head in the first position
    private ArrayList<Cell> snake;
    private Cell food;
    private static final Random randomNumberGenerator = new Random();

    public SnakegameModel() {
        readConfig();
        dataHandler = new DataHandler();
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
        this.gameTimeAllowed = Integer.parseInt(ConfigReader.getProperty("gametimeallowed"));
        this.topSomething = Integer.parseInt(ConfigReader.getProperty("topsomething"));

        System.out.println("game title is: " + this.gameTitle);
        System.out.println("board size is: " + this.boardSize);
        System.out.println("time interval is: " + this.timerInterval);

        // Board Size needs to be perfectly divisible by the number of columns
        // Otherwise the board will show an additional part of a column
        // Since the column size is calculated based on board size, this is
        // just a slight adjustment, similar to rounding down to the nearest int
        this.boardSize = this.numberOfColumns * this.cellSize;

        // Set the x and y coordinates of the game wall
        // Though the board is currently square, we'll set x and y coords in
        // case it changes in future to a rectangular share
        wallTop = 0;
        wallBottom = this.numberOfColumns - 1;
        wallLeft = 0;
        wallRight = this.numberOfColumns - 1;
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

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isTimeAllocationUsed() {
        return dailyTimeUsed;
    }

    public void setNewGame(boolean newGame) {
        this.newGame = newGame;
        gameOver = false;
    }

    public boolean getNewGame() {
        return newGame;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getGameTimeAllowed() {
        return gameTimeAllowed;
    }

    public int getCurrentSessionTime() {
        return currentSessionTime;
    }
    

    public int getSessionTime() {
        int sessionTime;
        LocalDateTime currentTime = LocalDateTime.now();
        // Define the format pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Format the current date and time into a string
        String formattedDateTime = currentTime.format(formatter);
        JSONObject sessionInfo = new JSONObject(dataHandler.readSessionTableByDate(formattedDateTime));
        if (sessionInfo.isEmpty())
            sessionTime = 0;
        else
            sessionTime = sessionInfo.getInt("duration");
        return sessionTime;
    }

    public JSONArray getLeaderboard() {
        JSONArray leaderboard = new JSONArray(dataHandler.readLeaderboard(topSomething));
        return leaderboard;
    }
    public JSONArray getGameHistory() {
        JSONArray gameHistory = new JSONArray(dataHandler.readDurationPlayed());
        return gameHistory;
    }

    /**
     * Check whether two Cells have collided. This could be the
     * snake head colliding with food.
     * 
     * A collision occurs when the 'x' and 'y' coordinates of both Cells
     * are identical.
     * 
     * @param cell1 - the first Cell
     * @param cell2 - the second Cell
     * @return a boolean indicating whether the two Cells refer to the same location
     */
    public boolean isCollision(Cell cell1, Cell cell2) {
        if (cell1.getX() == cell2.getX() && cell1.getY() == cell2.getY()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCollisionFood() {
        Cell snakeHead = snake.get(0);
        if (this.isCollision(snakeHead, food)) {
            // Grow the snake if there is a collision with a food item
            Cell bodySegment = new Cell(food.getX(), food.getY());
            snake.add(bodySegment);
            currentScore++;
            return true;
        } else {
            return false;
        }
    }

    public boolean isCollisionWall() {
        // Check for collision betwee the snake head and the game wall
        Cell snakeHead = snake.get(0);
        if (snakeHead.getX() == wallTop || snakeHead.getX() == wallBottom || snakeHead.getY() == wallLeft
                || snakeHead.getY() == wallRight) {
            gameOver = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean isCollisionBody() {
        // Check for collision betwee the snake head and body
        Cell snakeHead = snake.get(0);
        for (int i = snake.size() - 1; i > 1; i--) {
            Cell bodySegment = snake.get(i);
            if (this.isCollision(snakeHead, bodySegment)) {
                gameOver = true;
            }
        }
        return gameOver;
    }

    /**
     * Initialise the snake with the snake head at position 0 in the ArrayList
     * 
     * @return an ArrayList of Cells representing the snake body with the snake head
     *         in position 0
     */
    public ArrayList<Cell> initialiseSnake() {
        int x = randomNumberGenerator.nextInt((boardSize / cellSize) - 2) + 1;
        int y = randomNumberGenerator.nextInt((boardSize / cellSize) - 2) + 1;
        snake = new ArrayList<Cell>();
        Cell snakeHead = new Cell(x, y);
        snake.add(snakeHead);
        for (int i = 0; i < snakeLength; i++) {
            Cell bodySegment = new Cell(x, y + i);
            snake.add(bodySegment);
            System.out.println("initialiseSnake snake segment: " + bodySegment.getX() + ", " + bodySegment.getY());
        }

        System.out.println("Snake length in initialiseSnake is: " + snake.size());
        // Set the initial direction for the snake
        this.direction = 'U';
        currentScore = 0;
        return snake;
    }

    public Cell placeFood() {
        int x = randomNumberGenerator.nextInt((boardSize / cellSize) - 2) + 1;
        int y = randomNumberGenerator.nextInt((boardSize / cellSize) - 2) + 1;
        food = new Cell(x, y);
        return food;
    }

    public void storeGameTime(int gameTime) {
        this.currentSessionTime += gameTime;
        dataHandler.insertSessionTable(currentSessionTime);
        dataHandler.insertGameTable(gameTime, currentScore);
    }

    public boolean startNewGame() {
        this.initialiseSnake();
        this.placeFood();
        this.currentSessionTime = this.getSessionTime();
        // Players may only play for a specified period of time per day
        if (this.currentSessionTime > this.gameTimeAllowed) {
            this.dailyTimeUsed = true;
            this.gameOver = true;
        } 
        else {
            this.dailyTimeUsed = false;
        }
        return this.gameOver;
    }
}
