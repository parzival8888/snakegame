package org.snake;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class SnakegameModelTest {
    @Test public void modelConfigReader() {
        SnakegameModel classUnderTest = new SnakegameModel();
        assertNotNull("model should read configuration file when instantiated", classUnderTest.getGameTitle());
        assertTrue( classUnderTest.getBoardSize() > 0, "configuration file should contain boardsize > 0");
        assertFalse(classUnderTest.isGameOver(), "gameover should be set to false");
    }
}
