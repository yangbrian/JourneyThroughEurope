package jte.app;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The Main JavaFX application class
 * @author Brian Yang
 *         CSE 219 Fall 2014
 */
public class App extends Application{
    /**
     * Starts the JavaFX application
     * @param primaryStage the primary stage of the application
     */
    @Override
    public void start(Stage primaryStage) {
        String title = "Journey Through Europe";
        primaryStage.setTitle(title);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
