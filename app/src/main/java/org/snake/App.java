package org.snake;

import org.snake.model.SnakegameModel;
import org.snake.view.SnakegameView;

/**
 * The entry point for the Snake game application.
 * This class is responsible for initializing the model and view,
 * and making the view visible.
 */
public class App {
    /**
     * The main method serves as the entry point of the application.
     * It initializes the Snake game model and view, and makes the view visible.
     * 
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {

        // Create instances of the model & view
        SnakegameModel model = new SnakegameModel();
        SnakegameView view = new SnakegameView(model);

        // Display the view
        view.setVisible(true);
    }
}
