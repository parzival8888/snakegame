package org.snake;

import org.junit.Test;

import static org.junit.Assert.*;

public class SnakegameModelTest {
    @Test public void modelConfigReader() {
        SnakegameModel classUnderTest = new SnakegameModel();
        assertNotNull("model should read configuration file when instantiated", classUnderTest.getGameTitle());
        assertTrue("configuration file should contain boardsize > 0", classUnderTest.getBoardSize() > 0);
        assertFalse("gameover should be set to false", classUnderTest.isGameOver());
    }
}
