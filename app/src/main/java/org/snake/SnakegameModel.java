package org.snake;

import org.snake.util.ConfigReader;

public class SnakegameModel {
    private static String configFilename = "snakegame.config";
    private String gameTitle;
    private int boardSize;
    private boolean gameOver;

    public SnakegameModel() {
        ConfigReader.readConfig(configFilename);
        this.gameTitle = ConfigReader.getProperty("gametitle");
        this.boardSize = Integer.parseInt(ConfigReader.getProperty("boardsize"));
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

    public boolean isGameOver() {
        return gameOver;
    }
}
