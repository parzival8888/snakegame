package org.snake;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    void testMain() {
        // Testing that the main method runs without throwing any exceptions
        assertDoesNotThrow(() -> App.main(new String[] {}));
    }
}
