package org.snake;

import javax.swing.*;

import java.awt.*;

public class SnakegameView extends JFrame {
    private SnakegameModel model;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private int cellSize;
    private JPanel mainPanel;
    private JPanel gamePanel;
    private JPanel boardPanel;
    private JPanel scorePanel;
    private JPanel startPanel;
    private JPanel leaderboardPanel;
    private CardLayout cardLayout;
    private static String start = "Start";
    private static String newGame = "New Game";
    private static String gameHistory = "Game History";
    private static String gameLeaderboard = "Leaderboard";

    public SnakegameView(SnakegameModel model) {
        this.model = model;
        System.out.println("Board size will be set to: " + model.getBoardSize());
        setTitle(model.getGameTitle());
        setSize(model.getBoardSize(), model.getBoardSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        cellSize = model.getBoardSize() / model.getNumberOfColumns();
        createStartPanel();
        createGamePanel();
        createMainPanel();
        createLeaderboardPanel();
        add(mainPanel);
        this.setVisible(true);
    }

    private void createMainPanel() {
        // Create the main panel for the game. This panel will contain panels for game, leaderboard and game history
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(gamePanel, newGame);
        mainPanel.add(startPanel, start);
        cardLayout.show(mainPanel, start);
    }


    private void createGamePanel() {

        createScorePanel();

        // Create the panel for the game board
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphic) {
                super.paintComponent(graphic);
                // Draw the game grid here
                graphic.setColor(model.getBoardColour());
                graphic.fillRect(0, 0, model.getBoardSize(), model.getBoardSize());
                // Example snake piece
                // graphic.setColor(Color.GREEN);
                // graphic.fillRect(50, 50, 10, 10);

                // Draw a grid pattern to allow the player to see each of the cells on the board
                // Draw vertical lines
                graphic.setColor(model.getBoardGridColour());
                for (int i = 0; i <= model.getNumberOfColumns(); i++) {
                    int x = i * cellSize;
                    graphic.drawLine(x, 0, x, model.getBoardSize());
                }

                // Draw horizontal lines
                for (int i = 0; i <= model.getNumberOfColumns(); i++) {
                    int y = i * cellSize;
                    graphic.drawLine(0, y, model.getBoardSize(), y);
                }
            }
        };
        // Create the panel for the game score and game timer
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(scorePanel, BorderLayout.NORTH);
        gamePanel.add(boardPanel, BorderLayout.CENTER);
    }

    private void createScorePanel() {
        // Create the panel for the game score and game timer
        scorePanel = new JPanel(new GridLayout(1, 2, 10, 10)); // 2 rows, 2 columns, 10px gap

        scoreLabel = new JLabel("Score:");
        scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
        timerLabel = new JLabel("Timer:");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        scorePanel.add(scoreLabel);
        scorePanel.add(timerLabel);
    }

    private void createStartPanel() {
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
        if (text == newGame)
            cardLayout.show(mainPanel, newGame);
        else if (text == gameLeaderboard)
            cardLayout.show(mainPanel, gameLeaderboard);
        else if (text == gameHistory)
            cardLayout.show(mainPanel, gameHistory);
        else 
            cardLayout.show(mainPanel, start);
    }

    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void setTimer(int timer) {
        timerLabel.setText("Timer: " + timer);
    }
}
