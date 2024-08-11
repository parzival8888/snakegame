package org.snake;

import java.awt.Color;

import org.snake.util.ConfigReader;

public class SnakegameModel {
    private static String configFilename = "snakegame.config";
    private String gameTitle;
    private int boardSize;
    private int numberofcolumns;
    private Color boardColour;
    private Color boardGridColour;
    private boolean gameOver;

    public SnakegameModel() {
        ConfigReader.readConfig(configFilename);
        this.gameTitle = ConfigReader.getProperty("gametitle");
        this.boardSize = Integer.parseInt(ConfigReader.getProperty("boardsize"));
        this.numberofcolumns = Integer.parseInt(ConfigReader.getProperty("numberofcolumns"));
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

    public Color getBoardColour() {
        return boardColour;
    }

    public Color getBoardGridColour() {
        return boardGridColour;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
