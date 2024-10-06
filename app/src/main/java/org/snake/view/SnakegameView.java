package org.snake.view;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.snake.model.SnakegameModel;
/**
 * SnakegameView is the main user interface for the Snake game.
 * It manages different panels including the game board, leaderboard, and game history.
 */
public class SnakegameView extends JFrame {

    private SnakegameModel model; // The model that contains game logic and state
    private JLabel scoreLabel; // Label to display the current score
    private JLabel gameTimerLabel; // Label to display the current game time
    private JLabel sessionTimerLabel; // Label to display session time
    private JButton buttonNewGame; // Button to start a new game
    private JButton buttonMenu; // Button to return to the menu
    private GameboardPanel gameboardPanel; // Panel that displays the game board
    private JTable leaderboardTable; // Table for displaying the leaderboard
    private DefaultTableModel leaderboardTableModel; // Model for the leaderboard table
    private JTable gameHistoryTable; // Table for displaying game history
    private DefaultTableModel gameHistoryTableModel; // Model for the game history table

    // Panels for different views in the application
    private JPanel mainPanel; // Main panel managing other panels
    private JPanel startPanel; // Panel displayed at the start of the app
    private JPanel gamePanel; // Panel containing the game board and score/timer
    private JPanel scorePanel; // Panel displaying score and timer
    private JPanel leaderboardPanel; // Panel displaying leaderboard
    private JPanel gameHistoryPanel; // Panel displaying game history

    private CardLayout cardLayout; // Layout manager for switching between panels

    // Constants for panel names
    private static String start = "Start";
    private static String newGame = "New Game";
    private static String menu = "Menu";
    private static String gameHistory = "Game History";
    private static String gameLeaderboard = "Leaderboard";

    /**
     * Constructs a SnakegameView with the specified model.
     *
     * @param model The SnakegameModel that contains the game's logic.
     */
    public SnakegameView(SnakegameModel model) {
        this.model = model;
        setTitle(model.getGameTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createStartPanel(); // Initialize start panel
        createGamePanel(); // Initialize game panel
        createMainPanel(); // Initialize main panel to switch between views
        createLeaderboardPanel(); // Initialize leaderboard panel
        createGameHistoryPanel(); // Initialize game history panel

        add(mainPanel); // Add main panel to frame

        int height = model.getBoardSize() + (model.getBoardSize() * 10 / 100); // Set frame size
        setSize(model.getBoardSize(), height);
        this.setVisible(true); // Make frame visible
    }

    /**
     * Creates the main panel that manages different views of the application.
     */
    public void createMainPanel() {
        cardLayout = new CardLayout(); // Initialize CardLayout for panel switching
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(gamePanel, newGame); // Add game panel to main panel
        mainPanel.add(startPanel, start); // Add start panel to main panel
        cardLayout.show(mainPanel, start); // Show start panel by default
    }

    /**
     * Creates the game panel which contains the score and timer components.
     */
    public void createGamePanel() {
        createScorePanel(); // Create score and timer panel

        // Create and initialize the game board panel
        gameboardPanel = new GameboardPanel(model, scoreLabel, gameTimerLabel, sessionTimerLabel);
        model.setNewGame(true);

        // Set up layout for the game panel and add components
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(scorePanel, BorderLayout.NORTH);
        gamePanel.add(gameboardPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a panel for displaying the score and timer during gameplay.
     */
    private void createScorePanel() {
        scorePanel = new JPanel(new GridLayout(1, 5, 10, 10)); // Create grid layout for score panel

        scoreLabel = new JLabel("Score:"); 
        scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        gameTimerLabel = new JLabel("Game time:");
        gameTimerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        sessionTimerLabel = new JLabel("Session time:");
        sessionTimerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        buttonNewGame = new JButton(newGame); 
        buttonNewGame.setAlignmentX(SwingConstants.RIGHT);

        buttonMenu = new JButton(menu);
        buttonMenu.setAlignmentX(SwingConstants.RIGHT);

        scorePanel.add(scoreLabel); 
        scorePanel.add(gameTimerLabel);
        scorePanel.add(sessionTimerLabel);
        scorePanel.add(buttonNewGame); 
        scorePanel.add(buttonMenu);

        buttonNewGame.addActionListener(e -> {
            gameboardPanel.startGame(); 
            model.setNewGame(true); 
        });

        buttonMenu.addActionListener(e -> {
            switchPanel(buttonMenu.getText()); 
        });
    }
    /**
     * Creates a start panel with options for starting a new game or viewing leaderboards.
     */


    public void createStartPanel() {
        // Create the panel for the game start, with appropriate menu options
        startPanel = new JPanel();
        // startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setLayout(new GridLayout(4, 1, 10, 10));
        startPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        // Add buttons to the startPanel
        JButton buttonNewGame = new JButton(newGame);
        buttonNewGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton buttonLeaderboard = new JButton(gameLeaderboard);
        buttonLeaderboard.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton buttonGameHistory = new JButton(gameHistory);
        buttonGameHistory.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Big font for the buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        buttonNewGame.setFont(buttonFont);
        buttonLeaderboard.setFont(buttonFont);
        buttonGameHistory.setFont(buttonFont);
        buttonNewGame.setBackground(Color.GRAY);
        buttonNewGame.setForeground(Color.BLUE);
        buttonNewGame.setBorder(new BevelBorder(BevelBorder.RAISED));
        buttonNewGame.setOpaque(true);

        startPanel.add(buttonNewGame);
        startPanel.add(buttonLeaderboard);
        startPanel.add(buttonGameHistory);

        // Create a label for "How to Play" 
        JLabel howToPlayLabel = new JLabel("<html><div style='text-align: center;'>"
                + "<h2>How to Play</h2>"
                + "Use arrow keys to move the snake.<br>"
                + "Eat food to grow bigger.<br>"
                + "Avoid hitting the walls or yourself.</div></html>");
        howToPlayLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        howToPlayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        howToPlayLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
        startPanel.add(howToPlayLabel);

        // Add listeners to handle button clicks
        buttonNewGame.addActionListener(e -> switchPanel(buttonNewGame.getText()));
        buttonLeaderboard.addActionListener(e -> switchPanel(buttonLeaderboard.getText()));
        buttonGameHistory.addActionListener(e -> switchPanel(buttonGameHistory.getText()));
    }

    private void createLeaderboardPanel() {
        // Create the panel for the leaderboard
        leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        JButton buttonMenu = new JButton(menu);
        buttonMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel headingLabel = new JLabel("Leaderboard");
        headingLabel.setHorizontalAlignment(SwingConstants.LEFT);
        leaderboardPanel.add(headingLabel);
        buttonMenu.setHorizontalAlignment(SwingConstants.RIGHT);
        leaderboardPanel.add(buttonMenu);
        mainPanel.add(leaderboardPanel, gameLeaderboard);

        // Show the menu panel
        buttonMenu.addActionListener(e -> {
            switchPanel(buttonMenu.getText());
        });
        leaderboardTable = new JTable();

        // Add the JTable to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);

        // Add the JScrollPane to the JPanel
        leaderboardPanel.add(scrollPane, BorderLayout.SOUTH);
        this.getLeaderboard();
    }

    private void getLeaderboard() {
        // Get the leaderboard from the model
        JSONArray leaderBoard = model.getLeaderboard();

        if (leaderBoard.length() > 0) {
            // Extract column names
            String[] columnNames = JSONObject.getNames(leaderBoard.getJSONObject(0));

            // Create data array for JTable
            Object[][] data = new Object[leaderBoard.length()][columnNames.length];

            // Loop through the JSONArray and extract the values
            for (int i = 0; i < leaderBoard.length(); i++) {
                JSONObject obj = leaderBoard.getJSONObject(i);
                for (int j = 0; j < columnNames.length; j++) {
                    data[i][j] = obj.get(columnNames[j]);
                }
            }

            // Update the JTable with data and column names
            if (leaderboardTableModel == null) {
                leaderboardTableModel = new DefaultTableModel(data, columnNames);
                leaderboardTable.setModel(leaderboardTableModel);
            } else {
                leaderboardTableModel.setDataVector(data, columnNames);
                leaderboardTableModel.fireTableDataChanged();
            }
        }
    }

    private void createGameHistoryPanel() {
        // Create the panel for the game history
        gameHistoryPanel = new JPanel();
        gameHistoryPanel.setLayout(new BoxLayout(gameHistoryPanel, BoxLayout.Y_AXIS));
        JButton buttonMenu = new JButton(menu);
        buttonMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel headingLabel = new JLabel("game history");
        headingLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gameHistoryPanel.add(headingLabel);
        buttonMenu.setHorizontalAlignment(SwingConstants.RIGHT);
        gameHistoryPanel.add(buttonMenu);
        mainPanel.add(gameHistoryPanel, gameHistory);

        // Show the menu panel
        buttonMenu.addActionListener(e -> {
            switchPanel(buttonMenu.getText());
        });

        // Create the JTable with data and column names
        gameHistoryTable = new JTable();

        // Add the JTable to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(gameHistoryTable);

        // Add the JScrollPane to the JPanel
        gameHistoryPanel.add(scrollPane, BorderLayout.SOUTH);
        this.getGameHistory();
    }

    private void getGameHistory() {
        // Get the leaderboard from the model
        JSONArray gameHistory = model.getGameHistory();

        if (gameHistory.length() > 0) {
            // Extract column names
            String[] columnNames = JSONObject.getNames(gameHistory.getJSONObject(0));

            // Create data array for JTable
            Object[][] data = new Object[gameHistory.length()][columnNames.length];

            // Loop through the JSONArray and extract the values
            for (int i = 0; i < gameHistory.length(); i++) {
                JSONObject obj = gameHistory.getJSONObject(i);
                for (int j = 0; j < columnNames.length; j++) {
                    data[i][j] = obj.get(columnNames[j]);
                }
            }

            // Update the JTable with data and column names
            if (gameHistoryTableModel == null) {
                gameHistoryTableModel = new DefaultTableModel(data, columnNames);
                gameHistoryTable.setModel(gameHistoryTableModel);
            } else {
                gameHistoryTableModel.setDataVector(data, columnNames);
                gameHistoryTableModel.fireTableDataChanged();
            }
        }
    }

    private void switchPanel(String text) {
        if (text == newGame) {
            cardLayout.show(mainPanel, newGame);
            // createGamePanel();
            // mainPanel.add(gamePanel, newGame);
        } else if (text == gameLeaderboard) {
            cardLayout.show(mainPanel, gameLeaderboard);
            this.getLeaderboard();
        } else if (text == gameHistory) {
            cardLayout.show(mainPanel, gameHistory);
            this.getGameHistory();
        } else
            cardLayout.show(mainPanel, start);
    }

    public void addButtonListener(ActionListener listenForButton) {
        if (!(buttonNewGame == null))
            buttonNewGame.addActionListener(listenForButton);
    }
}
