package org.snake.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.awt.Color;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.snake.database.DataHandler;
import org.snake.util.Cell;
import org.snake.util.ConfigReader;

class SnakegameModelTest {

    private SnakegameModel model;
    private DataHandler dataHandlerMock;

    @BeforeEach
    void setUp() {
        // Mock ConfigReader and DataHandler
        try (MockedStatic<ConfigReader> mockedConfigReader = Mockito.mockStatic(ConfigReader.class)) {
            // Mock the static method readConfig() from ConfigReader
            mockedConfigReader.when(() -> ConfigReader.readConfig(Mockito.anyString()))
                    .thenAnswer(invocation -> {
                        // You can mock internal ConfigReader logic here
                        return null;
                    });
            when(ConfigReader.getProperty("gametitle")).thenReturn("Snake Game");
            when(ConfigReader.getProperty("boardsize")).thenReturn("600");
            when(ConfigReader.getProperty("numberofcolumns")).thenReturn("20");
            when(ConfigReader.getProperty("snakelength")).thenReturn("3");
            when(ConfigReader.getProperty("boardcolour")).thenReturn("#FFFFFF");
            when(ConfigReader.getProperty("boardgridcolour")).thenReturn("#000000");
            when(ConfigReader.getProperty("timerinterval")).thenReturn("1000");
            when(ConfigReader.getProperty("startdirection")).thenReturn("U");
            when(ConfigReader.getProperty("gametimeallowed")).thenReturn("60");
            when(ConfigReader.getProperty("topsomething")).thenReturn("6");

            // Mock DataHandler
            dataHandlerMock = mock(DataHandler.class);
            model = new SnakegameModel(dataHandlerMock);
        }
    }

    @Test
    void testReadConfig() {
        assertEquals("Snake Game", model.getGameTitle());
        assertEquals(600, model.getBoardSize());
        assertEquals(20, model.getNumberOfColumns());
        assertEquals(1000, model.getTimerInterval());
        assertEquals('U', model.getDirection());
        assertEquals(60, model.getGameTimeAllowed());
        assertEquals(Color.decode("#FFFFFF"), model.getBoardColour());
        assertEquals(Color.decode("#000000"), model.getBoardGridColour());
    }

    @Test
    void testInitialiseSnake() {
        model.initialiseSnake();
        Snake snake = model.getSnake();
        assertEquals(4, snake.getSnakeLength()); // Snake length (1 head + 3 body)
        assertNotNull(snake.getSnakeHead()); // Snake head is present
    }

    @Test
    void testPlaceFood() {
        Cell food = model.placeFood();
        assertNotNull(food);
        assertTrue(food.getX() > 0 && food.getY() > 0);
    }

    @Test
    void testIsCollision() {
        Cell cell1 = new Cell(1, 1);
        Cell cell2 = new Cell(1, 1);
        Cell cell3 = new Cell(2, 2);
        assertTrue(model.isCollision(cell1, cell2));
        assertFalse(model.isCollision(cell1, cell3));
    }

    /**
     * It isn't possible to test this fully as the position of the snake and food
     * are random. To test it we allow the SnakegameModel to place snake and food
     * randomly, then we change the location before calling the isCollisionFood()
     * method.
     */
    @Test
    void testIsCollisionFood() {
        model.initialiseSnake();
        Snake snake = model.getSnake();
        Cell snakeHead = snake.getSnakeHead();
        snakeHead.setX(5);
        snakeHead.setY(9);

        Cell food = model.placeFood();
        food.setX(3);
        food.setY(7);
        assertFalse(model.isCollisionFood());
        assertEquals(0, model.getCurrentScore());

        food.setX(5);
        food.setY(9);
        assertTrue(model.isCollisionFood());
        assertEquals(1, model.getCurrentScore());
    }

    @Test
    void testIsCollisionWall() {
        model.initialiseSnake();
        Snake snake = model.getSnake();
        Cell snakeHead = snake.getSnakeHead();
        snakeHead.setX(0);
        snakeHead.setY(5);
        assertTrue(model.isCollisionWall());
        assertTrue(model.isGameOver());
        snakeHead.setX(3);
        snakeHead.setY(0);
        assertTrue(model.isCollisionWall());
        assertTrue(model.isGameOver());
    }

    @Test
    void testIsCollisionBody() {
        model.initialiseSnake();
        Snake snake = model.getSnake();
        Cell snakeHead = snake.getSnakeHead();
        snake.addBodySegment(new Cell(snakeHead.getX(), snakeHead.getY()));// Body colliding with head
        assertTrue(model.isCollisionBody());
        assertTrue(model.isGameOver());
    }

    @Test
    void testStoreGameTime() { 
        model.storeGameTime(30);
        verify(dataHandlerMock).insertSessionTable(anyInt());
        verify(dataHandlerMock).insertGameTable(anyInt(), anyInt());
    }

    @Test
    void testStartNewGame() {
        when(dataHandlerMock.readSessionTableByDate(anyString())).thenReturn("{}");
        model.startNewGame();
        assertFalse(model.isGameOver());
        assertFalse(model.isTimeAllocationUsed());
    }

    @Test
    void testGetSessionTime() {
        when(dataHandlerMock.readSessionTableByDate(anyString())).thenReturn("{}");
        assertEquals(0, model.getSessionTime());

        JSONObject sessionInfo = new JSONObject();
        sessionInfo.put("duration", 30);
        when(dataHandlerMock.readSessionTableByDate(anyString())).thenReturn(sessionInfo.toString());
        assertEquals(30, model.getSessionTime());
    }

    @Test
    void testGetLeaderboard() {
        when(dataHandlerMock.readLeaderboard(5)).thenReturn(new JSONArray().toString());
        System.out.println("In testGetLeaderboard");
        JSONArray leaderboard = model.getLeaderboard();
        System.out.println("In testGetLeaderboard, leaderboard is: " + leaderboard);
        if (leaderboard.length() > 0)
            assertNotNull(leaderboard);
    }
}
