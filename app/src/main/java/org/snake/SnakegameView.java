package org.snake;

import javax.swing.*;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SnakegameView extends JFrame {
    private JTextField inputField;
    private JButton addButton;
    private JLabel resultLabel;

    public SnakegameView(SnakegameModel model) {
        System.out.println("Board size will be set to: " + model.getBoardSize());
        setTitle(model.getGameTitle());
        setSize(model.getBoardSize(), model.getBoardSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inputField = new JTextField(10);
        addButton = new JButton("Add");
        resultLabel = new JLabel("Result: 0");

        setLayout(new FlowLayout());
        add(new JLabel("Enter a number:"));
        add(inputField);
        add(addButton);
        add(resultLabel);
    }

    public int getInputValue() {
        try {
            return Integer.parseInt(inputField.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setResult(int result) {
        resultLabel.setText("Result: " + result);
    }

    public JButton getAddButton() {
        return addButton;
    }

    public void addModelListener(PropertyChangeListener listener) {
        addButton.addActionListener(e -> listener.propertyChange(new PropertyChangeEvent(this, "add", null, getInputValue())));
    }
}
