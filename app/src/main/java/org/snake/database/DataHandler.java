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
    private static final String CREATE_GAME_TABLE_SQL = "CREATE TABLE IF NOT EXISTS game_history ("
            + "rowid INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "timestamp TEXT NOT NULL, "
            + "duration INTEGER NOT NULL, "
            + "score INTEGER NOT NULL"
            + ");";

    // SQL statements for inserting and reading game data
    private static final String INSERT_GAME_SQL = "INSERT INTO game_history(timestamp, duration, score) VALUES(?, ?, ?)";
    private static final String READ_GAME_SQL = "SELECT * FROM game_history";
    private static final String READ_LEADERBOARD_SQL = "SELECT * FROM game_history ORDER BY score DESC LIMIT ?";
    private static final String ROW_ID_SQL = "SELECT last_insert_rowid()";

    /**
     * SQL statement to create the session table.
     * Stores the total duration of all games played during a session.
     */
    private static final String CREATE_SESSION_TABLE_SQL = "CREATE TABLE IF NOT EXISTS game_session ("
            + "date TEXT PRIMARY KEY, "
            + "duration INTEGER NOT NULL, "
            + "gamesplayed INTEGER NOT NULL "
            + ");";

    // SQL statements for inserting and reading session data
    private static final String INSERT_SESSION_SQL = "INSERT OR REPLACE INTO game_session(date, duration, gamesplayed) VALUES(?, ?, ?)";
    private static final String READ_SESSION_SQL = "SELECT * FROM game_session";
    private static final String READ_SESSION_BY_DATE_SQL = "SELECT * FROM game_session WHERE date = ?";

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
            ResultSet rs = stmtRead.executeQuery(READ_GAME_SQL);
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
     * @param topscorestodisplay The number of top scores to retrieve.
     * @return A JSON array containing the top scores from the leaderboard.
     */
    public String readLeaderboard(int topscorestodisplay) {
        JSONArray jsonArray = new JSONArray();
        try {
            conn = DriverManager.getConnection(connectionURL);
            PreparedStatement pstmtSelect = conn.prepareStatement(READ_LEADERBOARD_SQL);
            pstmtSelect.setInt(1, topscorestodisplay);
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
     * Creates the game history table in the database if it does not already exist.
     */
    public void createGameTable() {
        try {
            conn = DriverManager.getConnection(connectionURL);
            PreparedStatement pstmtCreate = conn.prepareStatement(CREATE_GAME_TABLE_SQL);
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
            PreparedStatement pstmtInsert = conn.prepareStatement(INSERT_GAME_SQL);
            PreparedStatement rowIdStmt = conn.prepareStatement(ROW_ID_SQL);
            
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
            ResultSet rs = stmtRead.executeQuery(READ_SESSION_SQL);
            
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("date", rs.getString("date"));
                jsonObject.put("duration", rs.getInt("duration"));
                jsonObject.put("gamesplayed", rs.getInt("gamesplayed"));
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
           PreparedStatement pstmtRead = conn.prepareStatement(READ_SESSION_BY_DATE_SQL);
           pstmtRead.setString(1, date);
           ResultSet rs = pstmtRead.executeQuery();

           while (rs.next()) {
               jsonObject.put("date", rs.getString("date"));
               jsonObject.put("duration", rs.getInt("duration"));
               jsonObject.put("gamesplayed", rs.getInt("gamesplayed"));
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
           PreparedStatement pstmtCreate = conn.prepareStatement(CREATE_SESSION_TABLE_SQL);
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
    * @param gamesPlayed Number of games played during this session.
    */
   public void insertSessionTable(int gameDuration, int gamesPlayed) { 
       try { 
           conn = DriverManager.getConnection(connectionURL); 
           PreparedStatement pstmtInsert = conn.prepareStatement(INSERT_SESSION_SQL); 

           LocalDateTime currentTime = LocalDateTime.now(); 
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
           String formattedDateTime = currentTime.format(formatter); 

           pstmtInsert.setString(1, formattedDateTime); 
           pstmtInsert.setInt(2, gameDuration); 
           pstmtInsert.setInt(3, gamesPlayed); 
           pstmtInsert.executeUpdate(); 

           pstmtInsert.close(); 
           conn.close(); 
       } catch (SQLException e) { 
           e.printStackTrace(); 
       } 
   }
}