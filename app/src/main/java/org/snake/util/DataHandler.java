package org.snake.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataHandler {
    private Connection conn;
    private String connectionURL = "jdbc:sqlite:snakegame.db";

    /**
     * The Game Table. Stores details of each game played
     */
    private static String createGameTableSQL = "CREATE TABLE IF NOT EXISTS game_history ("
            + "rowid INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "timestamp TEXT NOT NULL, "
            + "duration INTEGER NOT NULL, "
            + "score INTEGER NOT NULL"
            + ");";

    // Insert into game table
    private static String insertGameSQL = "INSERT INTO game_history(timestamp, duration, score) VALUES(?, ?, ?)";

    // Read from game table
    private static String readGameSQL = "SELECT * FROM game_history";

    // Read the leaderboard from the game table
    private static String readLeaderboardSQL = "SELECT * FROM game_history ORDER BY score DESC";

    // Retrieve the rowId of the last inserted row
    private String rowIdSql = "SELECT last_insert_rowid()";

    /**
     * The Session Table. Stores the total duration of all games played during a session
     */
    private static String createSessionTableSQL = "CREATE TABLE IF NOT EXISTS game_session ("
            + "date TEXT PRIMARY KEY, "
            + "duration INTEGER NOT NULL "
            + ");";

    // Insert into session table
    private static String insertSessionSQL = "INSERT OR REPLACE INTO game_session(date, duration) VALUES(?, ?)";

    // Read from session table
    private static String readSessionSQL = "SELECT * FROM game_session";
    private static String readSessionByDateSQL = "SELECT * FROM game_session WHERE date = ?";

    public DataHandler() {
        createGameTable();
        createSessionTable();
    }

    public String readGameTable() {
        JSONArray jsonArray = new JSONArray();
        try {
            conn = DriverManager.getConnection(connectionURL);
            Statement stmtRead = conn.createStatement();
            ResultSet rs = stmtRead.executeQuery(readGameSQL);

            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timestamp", rs.getString("timestamp"));
                jsonObject.put("duration", rs.getInt("duration"));
                jsonObject.put("score", rs.getInt("score"));
                jsonArray.put(jsonObject);
            }

            stmtRead.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    public String readLeaderboard() {
        JSONArray jsonArray = new JSONArray();
        try {
            conn = DriverManager.getConnection(connectionURL);
            Statement stmtRead = conn.createStatement();
            ResultSet rs = stmtRead.executeQuery(readLeaderboardSQL);

            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timestamp", rs.getString("timestamp"));
                jsonObject.put("duration", rs.getInt("duration"));
                jsonObject.put("score", rs.getInt("score"));
                jsonArray.put(jsonObject);
            }

            stmtRead.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    public void createGameTable() {
        try {
            conn = DriverManager.getConnection(connectionURL);
            PreparedStatement pstmtCreate = conn.prepareStatement(createGameTableSQL);
            pstmtCreate.executeUpdate();
            pstmtCreate.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long insertGameTable(int gameDuration, int gameScore) {
        long lastInsertedRowId = 0;
        try {
            conn = DriverManager.getConnection(connectionURL);
            PreparedStatement pstmtInsert = conn.prepareStatement(insertGameSQL);
            PreparedStatement rowIdStmt = conn.prepareStatement(rowIdSql);
            LocalDateTime currentTime = LocalDateTime.now();
            // Define the format pattern
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Format the current date and time into a string
            String formattedDateTime = currentTime.format(formatter);
            pstmtInsert.setString(1, formattedDateTime);
            pstmtInsert.setInt(2, gameDuration);
            pstmtInsert.setInt(3, gameScore);
            pstmtInsert.executeUpdate();

            // Get the ID of the inserted row
            ResultSet rs = rowIdStmt.executeQuery();
            if (rs.next()) {
                lastInsertedRowId = rs.getLong(1);
            }
            pstmtInsert.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastInsertedRowId;
    }

    public String readSessionTable() {
        JSONArray jsonArray = new JSONArray();
        try {
            conn = DriverManager.getConnection(connectionURL);
            Statement stmtRead = conn.createStatement();
            ResultSet rs = stmtRead.executeQuery(readSessionSQL);

            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("date", rs.getString("date"));
                jsonObject.put("duration", rs.getInt("duration"));
                jsonArray.put(jsonObject);
            }

            stmtRead.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    public String readSessionTableByDate(String date) {
        JSONObject jsonObject = new JSONObject();
        try {
            conn = DriverManager.getConnection(connectionURL);
            PreparedStatement pstmtRead = conn.prepareStatement(readSessionByDateSQL);
            pstmtRead.setString(1, date);
            ResultSet rs = pstmtRead.executeQuery();

            while (rs.next()) {
                jsonObject.put("date", rs.getString("date"));
                jsonObject.put("duration", rs.getInt("duration"));
            }

            pstmtRead.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void createSessionTable() {
        try {
            conn = DriverManager.getConnection(connectionURL);
            PreparedStatement pstmtCreate = conn.prepareStatement(createSessionTableSQL);
            pstmtCreate.executeUpdate();
            pstmtCreate.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertSessionTable(int gameDuration) {
        try {
            conn = DriverManager.getConnection(connectionURL);
            PreparedStatement pstmtInsert = conn.prepareStatement(insertSessionSQL);
            LocalDateTime currentTime = LocalDateTime.now();
            // Define the format pattern
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDateTime = currentTime.format(formatter);
            pstmtInsert.setString(1, formattedDateTime);
            pstmtInsert.setInt(2, gameDuration);
            pstmtInsert.executeUpdate();
            pstmtInsert.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
