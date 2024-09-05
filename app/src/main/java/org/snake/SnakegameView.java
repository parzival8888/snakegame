package org.snake;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SnakegameView extends JFrame {
    private SnakegameModel model;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JButton buttonNewGame;
    private GameboardPanel gameboardPanel;

    // The main panel manages the other panels and switches between the game panel,
    // start panel, etc.
    private JPanel mainPanel;
    // The panel displayed when starting the app. It contains the menu options for
    // new game, leaderboard and history
    private JPanel startPanel;
    // The game panel, which contains two sub-panels: the board panel holding the
    // game board, and the score panel containing the score and the timer
    private JPanel gamePanel;
    // The game board panel
    // private JPanel boardPanel;
    // The panel containing the score and timer, displayed during gameplay
    private JPanel scorePanel;
    // The panel containing the leaderboard
    private JPanel leaderboardPanel;

    private CardLayout cardLayout;
    private static String start = "Start";
    private static String newGame = "New Game";
    private static String gameHistory = "Game History";
    private static String gameLeaderboard = "Leaderboard";

    // The snake, with the head in the first position
    ArrayList<Cell> snake;

    public SnakegameView(SnakegameModel model) {
        this.model = model;
        System.out.println("Board size will be set to: " + model.getBoardSize());
        setTitle(model.getGameTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        createStartPanel();
        createGamePanel();
        createMainPanel();
        // createLeaderboardPanel();
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
        gameboardPanel = new GameboardPanel(model, scoreLabel);
        model.setNewGame(true);

        // Add the game board panel and the game score / timer panel to the main game
        // panel
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(scorePanel, BorderLayout.NORTH);
        gamePanel.add(gameboardPanel, BorderLayout.CENTER);
    }

    private void createScorePanel() {
        // Create the panel for the game score and game timer
        scorePanel = new JPanel(new GridLayout(1, 3, 10, 10)); // 1 row, 3 columns, 10px gap

        scoreLabel = new JLabel("Score:");
        scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
        timerLabel = new JLabel("Timer:");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        buttonNewGame = new JButton(newGame);
        buttonNewGame.setAlignmentX(SwingConstants.RIGHT);

        scorePanel.add(scoreLabel);
        scorePanel.add(timerLabel);
        scorePanel.add(buttonNewGame);

        // Add listeners to handle button clicks
        buttonNewGame.addActionListener(e -> {
            // Create the panel for the game board
            gameboardPanel.startGame();
            model.setNewGame(true);
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
    }

    private void createLeaderboardPanel() {
        // Create the panel for the leaderboard
        leaderboardPanel = new JPanel();

        JLabel headingLabel = new JLabel("Leaderboard:");
        headingLabel.setHorizontalAlignment(SwingConstants.LEFT);
        leaderboardPanel.add(headingLabel);
        mainPanel.add(leaderboardPanel, gameLeaderboard);
    }

    private void switchPanel(String text) {
        if (text == newGame) {
            cardLayout.show(mainPanel, newGame);
            //createGamePanel();
            //mainPanel.add(gamePanel, newGame);
        } else if (text == gameLeaderboard)
            cardLayout.show(mainPanel, gameLeaderboard);
        else if (text == gameHistory)
            cardLayout.show(mainPanel, gameHistory);
        else
            cardLayout.show(mainPanel, start);
    }

    public void addButtonListener(ActionListener listenForButton) {
        if (!(buttonNewGame == null))
            buttonNewGame.addActionListener(listenForButton);
    }

    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void setTimer(int timer) {
        timerLabel.setText("Timer: " + timer);
    }

}
