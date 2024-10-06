package org.snake.view;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.snake.model.SnakegameModel;

/**
 * SnakegameView represents the graphical user interface (GUI) for the Snake
 * game.
 * It manages various panels such as the game board, leaderboard, and game
 * history.
 * Interacts with the SnakegameModel and updates the view components based on
 * the game state.
 */
public class SnakegameView extends JFrame {

    private SnakegameModel model;
    private JLabel scoreLabel;
    private JLabel gameTimerLabel;
    private JLabel sessionTimerLabel;
    private JButton newGameButton;
    private JButton menuButton;
    private GameboardPanel gameboardPanel;
    private JTable leaderboardTable;
    private DefaultTableModel leaderboardTableModel;
    private JTable gameHistoryTable;
    private DefaultTableModel gameHistoryTableModel;

    // Panels for different views in the application
    private JPanel mainPanel;
    private JPanel startPanel;
    private JPanel gamePanel;
    private JPanel scorePanel;
    private JPanel leaderboardPanel;
    private JPanel gameHistoryPanel;

    private CardLayout cardLayout;

    // Constants for panel names
    private static final String START = "Start";
    private static final String NEW_GAME = "New Game";
    private static final String MENU = "Menu";
    private static final String GAME_HISTORY = "Game History";
    private static final String GAME_LEADERBOARD = "Leaderboard";

    // Constants for labels
    private static final String SCORE_LABEL = "Score:";
    private static final String GAME_TIME_LABEL = "Game time:";
    private static final String SESSION_TIME_LABEL = "Session time:";
    private static final String LEADERBOARD_LABEL = "Leaderboard";
    private static final String HOW_TO_PLAY_LABEL = "<html><div style='text-align: center;'>"
            + "<h2>How to Play</h2>"
            + "Use arrow keys to move the snake.<br>"
            + "Eat food to grow bigger.<br>"
            + "Avoid hitting the walls or yourself.</div></html>";

    /**
     * Constructs a SnakegameView object and initializes the UI components.
     * It sets up the game model, window properties, and the main user interface.
     * 
     * @param model The SnakegameModel that contains the logic and state of the game.
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
     * Creates the main panel for managing different views (start, game, leaderboard, etc.).
     * This panel uses a CardLayout to switch between views based on user actions.
     */
    public void createMainPanel() {
        cardLayout = new CardLayout(); // Initialize CardLayout for panel switching
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(gamePanel, NEW_GAME); // Add game panel to main panel
        mainPanel.add(startPanel, START); // Add start panel to main panel
        cardLayout.show(mainPanel, START); // Show start panel by default
    }

    /**
     * Creates the game panel, which contains the game board, score, and timers.
     * This method initializes the game board and sets up the layout for the gameplay view.
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
     * Creates the score panel that contains the score and game/session timers.
     * This panel is displayed at the top of the game screen.
     */
    private void createScorePanel() {
        scorePanel = new JPanel(new GridLayout(1, 5, 10, 10)); // Create grid layout for score panel

        scoreLabel = new JLabel(SCORE_LABEL); 

        
        gameTimerLabel = new JLabel(GAME_TIME_LABEL);
        gameTimerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        sessionTimerLabel = new JLabel(SESSION_TIME_LABEL);
        sessionTimerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        newGameButton = new JButton(NEW_GAME); 
        newGameButton.setAlignmentX(SwingConstants.RIGHT);

        menuButton = new JButton(MENU);
        menuButton.setAlignmentX(SwingConstants.RIGHT);

        scorePanel.add(scoreLabel); 
        scorePanel.add(gameTimerLabel);
        scorePanel.add(sessionTimerLabel);
        scorePanel.add(newGameButton); 
        scorePanel.add(menuButton);

        newGameButton.addActionListener(e -> {
            gameboardPanel.startGame();
            model.setNewGame(true); 
        });

        menuButton.addActionListener(e -> {
            switchPanel(menuButton.getText()); 
        });
    }
    
    /**
     * Creates the start panel with options for starting a new game, viewing the leaderboard, or game history.
     * This is the first screen the user sees when the game starts.
     */    
    public void createStartPanel() {
        // Create the panel for the game start, with appropriate menu options
        startPanel = new JPanel();
        // startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setLayout(new GridLayout(4, 1, 10, 10));
        startPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        // Add buttons to the startPanel
        JButton newGameButton = new JButton(NEW_GAME);
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton leaderboardButton = new JButton(GAME_LEADERBOARD);
        leaderboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton gameHistoryButton = new JButton(GAME_HISTORY);
        gameHistoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Big font for the buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        newGameButton.setFont(buttonFont);
        leaderboardButton.setFont(buttonFont);
        gameHistoryButton.setFont(buttonFont);
        newGameButton.setBackground(Color.GRAY);
        newGameButton.setForeground(Color.BLUE);
        newGameButton.setBorder(new BevelBorder(BevelBorder.RAISED));
        newGameButton.setOpaque(true);

        startPanel.add(newGameButton);
        startPanel.add(leaderboardButton);
        startPanel.add(gameHistoryButton);

        // Create a label for "How to Play" 
        JLabel howToPlayLabel = new JLabel(HOW_TO_PLAY_LABEL);
        howToPlayLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        howToPlayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        howToPlayLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
        startPanel.add(howToPlayLabel);

        // Add listeners to handle button clicks
        newGameButton.addActionListener(e -> switchPanel(newGameButton.getText()));
        leaderboardButton.addActionListener(e -> switchPanel(leaderboardButton.getText()));
        gameHistoryButton.addActionListener(e -> switchPanel(gameHistoryButton.getText()));
    }

    /**
     * Creates the leaderboard panel where the top scores are displayed.
     * It sets up the table for leaderboard data and includes a button to return to the menu.
     */
    private void createLeaderboardPanel() {
        // Create the panel for the leaderboard
        leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        JButton menuButton = new JButton(MENU);
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel headingLabel = new JLabel(LEADERBOARD_LABEL);
        headingLabel.setHorizontalAlignment(SwingConstants.LEFT);
        leaderboardPanel.add(headingLabel);
        menuButton.setHorizontalAlignment(SwingConstants.RIGHT);
        leaderboardPanel.add(menuButton);
        mainPanel.add(leaderboardPanel, GAME_LEADERBOARD);

        // Show the menu panel
        menuButton.addActionListener(e -> switchPanel(menuButton.getText()));
        leaderboardTable = new JTable();

        // Add the JTable to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);

        // Add the JScrollPane to the JPanel
        leaderboardPanel.add(scrollPane, BorderLayout.SOUTH);
        this.getLeaderboard();
    }

    /**
     * Fetches the leaderboard data from the model and updates the leaderboard table.
     * It extracts column names and data from the JSON response and fills the table.
     */
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

    /**
     * Creates the game history panel where the player's past game records are displayed.
     * It sets up a table to display the history and includes a button to return to the menu.
     */
    private void createGameHistoryPanel() {
        // Create the panel for the game history
        gameHistoryPanel = new JPanel();
        gameHistoryPanel.setLayout(new BoxLayout(gameHistoryPanel, BoxLayout.Y_AXIS));
        JButton menuButton = new JButton(MENU);
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel headingLabel = new JLabel(GAME_HISTORY);
        headingLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gameHistoryPanel.add(headingLabel);
        menuButton.setHorizontalAlignment(SwingConstants.RIGHT);
        gameHistoryPanel.add(menuButton);
        mainPanel.add(gameHistoryPanel, GAME_HISTORY);

        // Show the menu panel
        menuButton.addActionListener(e -> switchPanel(menuButton.getText()));

        // Create the JTable with data and column names
        gameHistoryTable = new JTable();

        // Add the JTable to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(gameHistoryTable);

        // Add the JScrollPane to the JPanel
        gameHistoryPanel.add(scrollPane, BorderLayout.SOUTH);
        this.getGAME_HISTORY();
    }

    /**
     * Fetches the game history data from the model and updates the game history table.
     * It extracts column names and data from the JSON response and fills the table.
     */
    private void getGAME_HISTORY() {
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

    /**
     * Switches the displayed panel based on the button text (view name).
     * This method controls navigation between different views like the start menu, game, leaderboard, etc.
     * 
     * @param text The name of the panel to switch to (e.g., "New Game", "Menu", "Leaderboard").
     */
    private void switchPanel(String text) {
        if (text == NEW_GAME) {
            cardLayout.show(mainPanel, NEW_GAME);
            // createGamePanel();
            // mainPanel.add(gamePanel, newGame);
        } else if (text == GAME_LEADERBOARD) {
            cardLayout.show(mainPanel, GAME_LEADERBOARD);
            this.getLeaderboard();
        } else if (text == GAME_HISTORY) {
            cardLayout.show(mainPanel, GAME_HISTORY);
            this.getGAME_HISTORY();
        } else
            cardLayout.show(mainPanel, START);
    }
    
    /**
     * Adds a listener for the buttons used in the game (e.g., New Game, Menu).
     * The listener responds to user input and triggers the associated actions.
     * 
     * @param listenForButton The ActionListener to be added to the buttons.
     */
    public void addButtonListener(ActionListener listenForButton) {
        if (!(newGameButton == null))
            newGameButton.addActionListener(listenForButton);
    }
}
