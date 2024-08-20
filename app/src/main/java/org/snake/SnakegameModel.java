package org.snake;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import org.snake.util.ConfigReader;

public class SnakegameModel {
    private static String configFilename = "snakegame.config";
    private String gameTitle;
    private int boardSize;
    private int numberofcolumns;
    private int cellSize;
    private Color boardColour;
    private Color boardGridColour;
    private boolean gameOver;
    // The snake, with the head in the first position
    private ArrayList<Cell> snake;
    private Cell food;
    private static final Random randomNumberGenerator = new Random();

    public SnakegameModel() {
        ConfigReader.readConfig(configFilename);
        this.gameTitle = ConfigReader.getProperty("gametitle");
        this.boardSize = Integer.parseInt(ConfigReader.getProperty("boardsize"));
        this.numberofcolumns = Integer.parseInt(ConfigReader.getProperty("numberofcolumns"));
        this.cellSize = this.boardSize / this.numberofcolumns;
        this.boardColour = Color.decode(ConfigReader.getProperty("boardcolour"));
        this.boardGridColour = Color.decode(ConfigReader.getProperty("boardgridcolour"));
        System.out.println("game title is: " + this.gameTitle);
        System.out.println("board size is: " + this.boardSize);
        this.gameOver = false;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getNumberOfColumns() {
        return numberofcolumns;
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

    public ArrayList<Cell> initialiseSnake() {
        int x = randomNumberGenerator.nextInt(boardSize/cellSize);
		int y = randomNumberGenerator.nextInt(boardSize/cellSize);
        Cell snakeHead = new Cell(x, y);
        snake = new ArrayList<Cell>();
        snake.add(snakeHead);
        return snake;
    }

    public Cell placeFood() {
        int x = randomNumberGenerator.nextInt(boardSize/cellSize);
		int y = randomNumberGenerator.nextInt(boardSize/cellSize);
        food = new Cell(x, y);
        return food;
	}

    public void newGame() {
        this.initialiseSnake();
        this.placeFood();
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
