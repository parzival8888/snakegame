package org.snake.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.snake.model.SnakegameModel;
import org.snake.util.Cell;

public class SnakegameView extends JFrame {
    private SnakegameModel model;
    private JLabel scoreLabel;
    private JLabel gameTimerLabel;
    private JLabel sessionTimerLabel;
    private JButton buttonNewGame;
    private JButton buttonMenu;
    private GameboardPanel gameboardPanel;
    private JTable leaderboardTable;
    private DefaultTableModel leaderboardTableModel;
    private JTable gameHistoryTable;
    private DefaultTableModel gameHistoryTableModel;

    // The main panel manages the other panels and switches between the game panel,
    // start panel, etc.
    private JPanel mainPanel;
    // The panel displayed when starting the app. It contains the menu options for
    // new game, leaderboard and history
    private JPanel startPanel;
    // The game panel, which contains two sub-panels: the board panel holding the
    // game board, and the score panel containing the score and the timer
    private JPanel gamePanel;
    // The panel containing the score and timer, displayed during gameplay
    private JPanel scorePanel;
    // The panel containing the leaderboard
    private JPanel leaderboardPanel;
    // The panel containing the game history
    private JPanel gameHistoryPanel;

    private CardLayout cardLayout;
    private static String start = "Start";
    private static String newGame = "New Game";
    private static String menu = "Menu";
    private static String gameHistory = "Game History";
    private static String gameLeaderboard = "Leaderboard";

    // The snake, with the head in the first position
    ArrayList<Cell> snake;

    public SnakegameView(SnakegameModel model) {
        this.model = model;
        setTitle(model.getGameTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        createStartPanel();
        createGamePanel();
        createMainPanel();
        createLeaderboardPanel();
        createGameHistoryPanel();
        add(mainPanel);
        // The size of the frame will be the size of the game board plus
        // a little extra (10%) to cater for the score and timer bar
        int height = model.getBoardSize() + (model.getBoardSize() * 10 / 100);
        setSize(model.getBoardSize(), height);
        this.setVisible(true);
    }

    public void createMainPanel() {
        // Create the main panel for the game. This panel will contain panels for game,
        // leaderboard and game history
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(gamePanel, newGame);
        mainPanel.add(startPanel, start);
        cardLayout.show(mainPanel, start);
    }

    public void createGamePanel() {

        // Create the panel with the game score and timer
        createScorePanel();

        // Create the panel for the game board
        gameboardPanel = new GameboardPanel(model, scoreLabel, gameTimerLabel, sessionTimerLabel);
        model.setNewGame(true);

        // Add the game board panel and the game score / timer panel to the main game
        // panel
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(scorePanel, BorderLayout.NORTH);
        gamePanel.add(gameboardPanel, BorderLayout.CENTER);
    }

    private void createScorePanel() {
        // Create the panel for the game score and game timer
        scorePanel = new JPanel(new GridLayout(1, 5, 10, 10)); // 1 row, 3 columns, 10px gap

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

        // Start a new game
        buttonNewGame.addActionListener(e -> {
            gameboardPanel.startGame();
            model.setNewGame(true);
        });

        // Show the menu panel
        buttonMenu.addActionListener(e -> {
            switchPanel(buttonMenu.getText());
        });
    }

    public void createStartPanel() {
        // Create the panel for the game start, with appropriate menu options
        startPanel = new JPanel();
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add buttons to the startPanel
        JButton buttonNewGame = new JButton(newGame);
        buttonNewGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton buttonLeaderboard = new JButton(gameLeaderboard);
        buttonLeaderboard.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton buttonGameHistory = new JButton(gameHistory);
        buttonGameHistory.setAlignmentX(Component.CENTER_ALIGNMENT);
        startPanel.add(buttonNewGame);
        startPanel.add(Box.createVerticalStrut(10)); // Add space between buttons
        startPanel.add(buttonLeaderboard);
        startPanel.add(Box.createVerticalStrut(10));
        startPanel.add(buttonGameHistory);
        startPanel.add(Box.createVerticalStrut(10));

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
