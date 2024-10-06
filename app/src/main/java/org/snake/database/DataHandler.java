package org.snake.database;
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
/**
 * Handles database operations for the Snake game, including managing game history
 * and session data. This class provides methods to create tables, insert records,
 * and read data from the SQLite database.
 */
public class DataHandler {

    private Connection conn;
    private String connectionURL = "jdbc:sqlite:snakegame.db"; 

    /**
     * SQL statement to create the game history table.
     * Stores details of each game played.
     */
    private static String createGameTableSQL = "CREATE TABLE IF NOT EXISTS game_history ("
            + "rowid INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "timestamp TEXT NOT NULL, "
            + "duration INTEGER NOT NULL, "
            + "score INTEGER NOT NULL"
            + ");";

    // SQL statements for inserting and reading game data
    private static String insertGameSQL = "INSERT INTO game_history(timestamp, duration, score) VALUES(?, ?, ?)";
    private static String readGameSQL = "SELECT * FROM game_history";
    private static String readLeaderboardSQL = "SELECT * FROM game_history ORDER BY score DESC LIMIT ?";
    private static String readTotalDurationSQL = "SELECT strftime('%Y-%m-%d', timestamp) AS date, sum(duration) AS total_duration FROM game_history GROUP BY strftime('%Y-%m-%d', timestamp);";
    private String rowIdSql = "SELECT last_insert_rowid()";

    /**
     * SQL statement to create the session table.
     * Stores the total duration of all games played during a session.
     */
    private static String createSessionTableSQL = "CREATE TABLE IF NOT EXISTS game_session ("
            + "date TEXT PRIMARY KEY, "
            + "duration INTEGER NOT NULL "
            + ");";

    // SQL statements for inserting and reading session data
    private static String insertSessionSQL = "INSERT OR REPLACE INTO game_session(date, duration) VALUES(?, ?)";
    private static String readSessionSQL = "SELECT * FROM game_session";
    private static String readSessionByDateSQL = "SELECT * FROM game_session WHERE date = ?";

    /**
     * Initializes the DataHandler by creating necessary tables in the database.
     */
    public DataHandler() {
        createGameTable();
        createSessionTable();
    }

    /**
     * Reads all entries from the game history table and returns them as a JSON array.
     *
     * @return A JSON array containing all game records.
     */
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

    /**
     * Reads the leaderboard from the game history table, returning the top scores as a JSON array.
     *
     * @param topSomething The number of top scores to retrieve.
     * @return A JSON array containing the top scores from the leaderboard.
     */
    public String readLeaderboard(int topSomething) {
        JSONArray jsonArray = new JSONArray();
        try {
            conn = DriverManager.getConnection(connectionURL);
            PreparedStatement pstmtSelect = conn.prepareStatement(readLeaderboardSQL);
            pstmtSelect.setInt(1, topSomething);
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timestamp", rs.getString("timestamp"));
                jsonObject.put("duration", rs.getInt("duration"));
                jsonObject.put("score", rs.getInt("score"));
                jsonArray.put(jsonObject);
            }
            pstmtSelect.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    /**
     * Reads total duration played by date from the game history table and returns it as a JSON array.
     *
     * @return A JSON array containing total durations grouped by date.
     */
    public String readDurationPlayed() {
        JSONArray jsonArray = new JSONArray();
        try {
            conn = DriverManager.getConnection(connectionURL);
            Statement stmtSelect = conn.createStatement();
            ResultSet rs = stmtSelect.executeQuery(readTotalDurationSQL);
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("date", rs.getString("date"));
                jsonObject.put("total_duration", rs.getInt("total_duration"));
                jsonArray.put(jsonObject);
            }
            stmtSelect.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    /**
     * Creates the game history table in the database if it does not already exist.
     */
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

    /**
     * Inserts a new record into the game history table with the specified duration and score.
     *
     * @param gameDuration The duration of the game played.
     * @param gameScore The score achieved in that game.
     * @return The ID of the last inserted row.
     */
    public long insertGameTable(int gameDuration, int gameScore) {
        long lastInsertedRowId = 0;
        try {
            conn = DriverManager.getConnection(connectionURL);
            PreparedStatement pstmtInsert = conn.prepareStatement(insertGameSQL);
            PreparedStatement rowIdStmt = conn.prepareStatement(rowIdSql);
            
            LocalDateTime currentTime = LocalDateTime.now(); // Get current time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Define format
            String formattedDateTime = currentTime.format(formatter); // Format date
            
            pstmtInsert.setString(1, formattedDateTime);
            pstmtInsert.setInt(2, gameDuration);
            pstmtInsert.setInt(3, gameScore);
            
            pstmtInsert.executeUpdate(); // Execute insert
            
            ResultSet rs = rowIdStmt.executeQuery(); // Get last row ID
            if (rs.next()) {
                lastInsertedRowId = rs.getLong(1); // Retrieve ID
            }
            
            pstmtInsert.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastInsertedRowId;
    }

    /**
     * Reads all entries from the session table and returns them as a JSON array.
     *
     * @return A JSON array containing all session records.
     */
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

    /**
     * Reads session data for a specific date and returns it as a JSON object.
     *
     * @param date The date for which to retrieve session data.
     * @return A JSON object containing session data for that date.
     */
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

   /**
    * Creates the session table in the database if it does not already exist.
    */
   public void createSessionTable() {
       try {
           conn = DriverManager.getConnection(connectionURL);
           PreparedStatement pstmtCreate = conn.prepareStatement(createSessionTableSQL);
           pstmtCreate.executeUpdate(); // Execute creation
           pstmtCreate.close(); 
           conn.close(); 
       } catch (SQLException e) { 
           e.printStackTrace(); 
       }
   }

   /**
    * Inserts or updates session data with total duration for today into the session table.
    *
    * @param gameDuration The total duration of games played during this session.
    */
   public void insertSessionTable(int gameDuration) { 
       try { 
           conn = DriverManager.getConnection(connectionURL); 
           PreparedStatement pstmtInsert = conn.prepareStatement(insertSessionSQL); 

           LocalDateTime currentTime = LocalDateTime.now(); 
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