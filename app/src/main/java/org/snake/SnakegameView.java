package org.snake;

import javax.swing.*;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SnakegameView extends JFrame {
    private JLabel scoreLabel;
    private JLabel timerLabel;

    public SnakegameView(SnakegameModel model) {
        System.out.println("Board size will be set to: " + model.getBoardSize());
        setTitle(model.getGameTitle());
        setSize(model.getBoardSize(), model.getBoardSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        scoreLabel = new JLabel("Score:");
        timerLabel = new JLabel("Timer:");

        setLayout(new FlowLayout());
        add(scoreLabel);
        add(timerLabel);
    }

    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }
    
    public void setTimer(int timer) {
        timerLabel.setText("Timer: " + timer);
    }
}
