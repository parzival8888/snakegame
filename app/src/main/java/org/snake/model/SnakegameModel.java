package org.snake.model;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import org.snake.database.DataHandler;
import org.snake.util.Cell;
import org.snake.util.ConfigReader;

/**
 * Represents the model for the Snake game, managing game state, configuration,
 * and interactions between game components such as the snake and food.
 */
public class SnakegameModel {

    private static final String CONFIG_FILENAME = "snakegame.config"; // Configuration file name
    private static final Random RANDOM = new Random(); // Random number generator for food placement
    private static final int OFFSET = 2;

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
    private Snake snake; 
    private Cell food; 

    /**
     * Default constructor that initializes a new SnakegameModel instance,
     * reading configuration settings and preparing for a new game.
     */
    public SnakegameModel() {
        initializeGameModel(new DataHandler());
    }

    /**
     * Constructor that allows for dependency injection of a DataHandler.
     *
     * @param dataHandler The DataHandler instance to be used for database
     *                    operations.
     */
    public SnakegameModel(DataHandler dataHandler) {
        initializeGameModel(dataHandler);
    }

    /**
     * Initialises the model using the appropriate data handler.
     *
     * @param dataHandler The DataHandler instance to be used for database
     *                    operations.
     */
    private void initializeGameModel(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.gameOver = false;
        readConfig();
    }
    
    /**
     * Reads configuration settings from a specified config file.
     * Initializes various properties such as title, size, colors, and snake length.
     */
    private void readConfig() {
        ConfigReader.readConfig(CONFIG_FILENAME);
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

        // Adjust board size to ensure it's divisible by number of columns.
        this.boardSize = this.numberOfColumns * this.cellSize;

        // Set wall boundaries based on board dimensions.
        wallTop = 0;
        wallBottom = this.numberOfColumns - 1;
        wallLeft = 0;
        wallRight = this.numberOfColumns - 1;
    }

    /**
     * Retrieves the title of the game.
     *
     * @return The title of the game.
     */
    public String getGameTitle() {
        return gameTitle;
    }

    /**
     * Retrieves the size of the game board.
     *
     * @return The size of the board in pixels.
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Retrieves the number of columns in the game board.
     *
     * @return The number of columns in the board.
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    /**
     * Retrieves the timer interval for updating game state.
     *
     * @return The timer interval in milliseconds.
     */
    public int getTimerInterval() {
        return timerInterval;
    }

    /**
     * Retrieves the size of each cell on the board.
     *
     * @return The size of each cell in pixels.
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Retrieves the background color of the game board.
     *
     * @return The Color object representing the board's background color.
     */
    public Color getBoardColour() {
        return boardColour;
    }

    /**
     * Retrieves the color used for grid lines on the board.
     *
     * @return The Color object representing grid line color.
     */
    public Color getBoardGridColour() {
        return boardGridColour;
    }

    /**
     * Retrieves the current location of food on the board.
     *
     * @return The Cell object representing food's coordinates on the board.
     */
    public Cell getFoodLocation() {
        return food;
    }

    /**
     * Retrieves an instance of Snake representing player's snake.
     *
     * @return The Snake object representing player's snake.
     */
    public Snake getSnake() {
        return snake;
    }

    /**
     * Retrieves current direction of snake's movement.
     *
     * @return A character indicating current direction ('U', 'D', 'L', 'R').
     */
    public char getDirection() {
        return direction;
    }

    /**
     * Sets a new direction for snake's movement based on user input or logic.
     *
     * @param direction A character indicating new direction ('U', 'D', 'L', 'R').
     */
    public void setDirection(char direction) {
        this.direction = direction;
    }

    /**
     * Checks if the game is currently over.
     *
     * @return A boolean indicating whether or not the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Checks if daily time allocation has been used up.
     *
     * @return A boolean indicating whether daily time has been used or not.
     */
    public boolean isTimeAllocationUsed() {
        return dailyTimeUsed;
    }

    /**
     * Sets whether a new game is starting and resets necessary parameters
     * accordingly.
     *
     * @param newGame A boolean indicating if a new game should start (true) or not
     *                (false).
     */
    public void setNewGame(boolean newGame) {
        this.newGame = newGame;
        gameOver = false;
    }

    /**
     * Checks if a new game is currently being started.
     *
     * @return A boolean indicating whether a new game is being started or not.
     */
    public boolean isNewGame    () {
        return newGame;
    }

    /**
     * Retrieves current score achieved by player during gameplay.
     *
     * @return An integer representing current score.
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Retrieves allowed gameplay time per day set by configuration settings.
     *
     * @return An integer representing allowed gameplay time in seconds per day.
     */
    public int getGameTimeAllowed() {
        return gameTimeAllowed;
    }

    /**
     * Gets total session time played during current session from database records.
     *
     * @return An integer representing total session time played in seconds.
     */
    public int getCurrentSessionTime() {
        return currentSessionTime;
    }

    /**
     * Calculates and retrieves total session time played today from database
     * records,
     * returning it as an integer value. If no records exist, returns zero.
     *
     * @return An integer representing total session time played today in seconds.
     */
    public int getSessionTime() {
        int sessionTime;
        LocalDateTime currentTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDateTime = currentTime.format(formatter);

        JSONObject sessionInfo = new JSONObject(dataHandler.readSessionTableByDate(formattedDateTime));

        if (sessionInfo.isEmpty())
            sessionTime = 0;
        else
            sessionTime = sessionInfo.getInt("duration");

        return sessionTime;
    }

    /**
     * Retrieves leaderboard data from DataHandler, returning it as a JSON array.
     *
     * @return A JSONArray containing leaderboard entries with scores and
     *         timestamps.
     */
    public JSONArray getLeaderboard() {
        String sLeaderboard = dataHandler.readLeaderboard(topSomething);
        JSONArray leaderboard;

        if (sLeaderboard == null)
            leaderboard = new JSONArray();
        else
            leaderboard = new JSONArray(sLeaderboard);

        return leaderboard;
    }

    /**
     * Retrieves historical gameplay data from DataHandler, returning it as a JSON
     * array.
     *
     * @return A JSONArray containing historical gameplay entries with durations.
     */
    public JSONArray getGameHistory() {
        JSONArray gameHistory = new JSONArray(dataHandler.readDurationPlayed());
        return gameHistory;
    }

    /**
     * Checks whether two Cells have collided. This could be when
     * snake head collides with food.
     *
     * A collision occurs when both Cells' 'x' and 'y' coordinates are identical.
     *
     * @param cell1 - The first Cell to check
     * @param cell2 - The second Cell to check
     * @return A boolean indicating whether both Cells refer to same location
     */
    public boolean isCollision(Cell cell1, Cell cell2) {
        if (cell1.getX() == cell2.getX() && cell1.getY() == cell2.getY()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if there is a collision between snake head and food. If so,
     * grows snake by adding body segment at food's location and increments score.
     *
     * @return A boolean indicating whether there was a collision with food.
     */
    public boolean isCollisionFood() {
        if (this.isCollision(snake.getSnakeHead(), food)) {
            Cell bodySegment = new Cell(food.getX(), food.getY());
            snake.addBodySegment(bodySegment);
            currentScore++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks for collision between snake head and walls defined by boundaries.
     *
     * If collision occurs, sets gameOver status to true.
     *
     * @return A boolean indicating whether there was a collision with walls.
     */
    public boolean isCollisionWall() {
        Cell snakeHead = snake.getSnakeHead();

        if (snakeHead.getX() == wallTop || snakeHead.getX() == wallBottom ||
                snakeHead.getY() == wallLeft || snakeHead.getY() == wallRight) {
            gameOver = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks for collision between snake head and its own body segments.
     *
     * If collision occurs, sets gameOver status to true.
     *
     * @return A boolean indicating whether there was a collision with body
     *         segments.
     */
    public boolean isCollisionBody() {
        for (int i = snake.getSnakeLength() - 1; i > 1; i--) {
            Cell bodySegment = snake.getBodySegment(i);

            if (this.isCollision(snake.getSnakeHead(), bodySegment)) {
                gameOver = true;
                return true;
            }
        }

        return false;
    }

    /**
     * Initializes a new Snake instance with random starting position within valid
     * bounds.
     * Sets initial direction and resets score.
     */
    public void initialiseSnake() {
        int x = RANDOM.nextInt((boardSize / cellSize) - 2) + 1;
        int y = RANDOM.nextInt((boardSize / cellSize) - 2) + 1;

        snake = new Snake(x, y, this.snakeLength);
        this.direction = 'U';
        currentScore = 0;
    }

    /**
     * Places food at a random location on the board within valid boundaries,
     * ensuring it does not appear on outermost cells.
     *
     * @return Cell representing newly placed food location.
     */
    public Cell placeFood() {
        int x = RANDOM.nextInt((boardSize / cellSize) - OFFSET) + 1;
        int y = RANDOM.nextInt((boardSize / cellSize) - OFFSET) + 1;

        food = new Cell(x, y);
        return food;
    }

    /**
     * Stores total gameplay time for current session into database,
     * updating both session duration and individual game's history.
     *
     * @param gameTime Duration played during current session in seconds.
     */
    public void storeGameTime(int gameTime) {
        this.currentSessionTime += gameTime;
        dataHandler.insertSessionTable(currentSessionTime);
        dataHandler.insertGameTable(gameTime, currentScore);
    }

/**
* Starts a new gameplay session by initializing components like snake and food,
* checking against daily time limits set by configuration settings.     
*
* If daily limit exceeded, sets flags accordingly to indicate end of playtime.     
*
* @return A boolean indicating whether gameplay has ended due to exceeding daily limits.     
*/     
public boolean startNewGame() {     
      this.initialiseSnake();     
      
      this.placeFood();     
      
      this.currentSessionTime = this.getSessionTime();     

      if (this.currentSessionTime > this.gameTimeAllowed) {     
          this.dailyTimeUsed = true;     
          this.gameOver = true;     
          return true;     
       } else {     
           this.dailyTimeUsed = false;
           return false;
       }
    }
}