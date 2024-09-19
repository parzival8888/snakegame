package org.snake.database;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataHandlerTest {

    private DataHandler dataHandler;

    @BeforeEach
    public void setUp() {
        dataHandler = new DataHandler();
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Clean up database after each test
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:snakegame.db");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS game_history");
            stmt.execute("DROP TABLE IF EXISTS game_session");
        }
    }

    @Test
    public void testCreateGameTable() {
        dataHandler.createGameTable();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:snakegame.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='game_history'")) {
            assertTrue(rs.next(), "Game table should be created");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testInsertGameTable() {
        dataHandler.createGameTable();
        long rowId = dataHandler.insertGameTable(120, 500);

        assertTrue(rowId > 0, "RowId should be greater than 0 after insert");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:snakegame.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM game_history WHERE rowid = " + rowId)) {
            assertTrue(rs.next(), "Inserted game record should be retrievable");
            assertEquals(120, rs.getInt("duration"), "Duration should match the inserted value");
            assertEquals(500, rs.getInt("score"), "Score should match the inserted value");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testReadGameTable() {
        dataHandler.createGameTable();
        dataHandler.insertGameTable(120, 500);
        String jsonData = dataHandler.readGameTable();

        assertNotNull(jsonData, "Returned JSON data should not be null");
        assertTrue(jsonData.contains("120"), "JSON data should contain inserted duration");
        assertTrue(jsonData.contains("500"), "JSON data should contain inserted score");
    }

    @Test
    public void testCreateSessionTable() {
        dataHandler.createSessionTable();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:snakegame.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='game_session'")) {
            assertTrue(rs.next(), "Session table should be created");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testInsertSessionTable() {
        dataHandler.createSessionTable();
        dataHandler.insertSessionTable(3600);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:snakegame.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM game_session")) {
            assertTrue(rs.next(), "Inserted session record should be retrievable");
            assertEquals(3600, rs.getInt("duration"), "Duration should match the inserted value");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testReadSessionTable() {
        dataHandler.createSessionTable();
        dataHandler.insertSessionTable(3600);
        String jsonData = dataHandler.readSessionTable();

        assertNotNull(jsonData, "Returned JSON data should not be null");
        assertTrue(jsonData.contains("3600"), "JSON data should contain inserted duration");
    }

    @Test
    public void testReadLeaderboard() {
        dataHandler.createGameTable();
        dataHandler.insertGameTable(120, 500);
        dataHandler.insertGameTable(150, 1000);
        String leaderboardData = dataHandler.readLeaderboard(5);

        assertNotNull(leaderboardData, "Returned JSON data should not be null");
        assertTrue(leaderboardData.contains("1000"), "Leaderboard should contain the highest score");
    }

    @Test
    public void testReadSessionTableByDate() {
        dataHandler.createSessionTable();
        dataHandler.insertSessionTable(7200);  // Insert for today
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = now.format(formatter);
        String sessionData = dataHandler.readSessionTableByDate(today);

        assertNotNull(sessionData, "Returned JSON data should not be null");
        assertTrue(sessionData.contains("7200"), "JSON data should contain inserted duration for today");
    }
}
