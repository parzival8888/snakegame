/*
 * This source file was generated by the Gradle 'init' task
 */
package org.snake;

public class App {
    public static void main(String[] args) {

        // Create instances of the model, view, and controller
        SnakegameModel model = new SnakegameModel();
        SnakegameView view = new SnakegameView(model);

        // Display the view
        view.setVisible(true);
    }
}
