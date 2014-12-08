package jte.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jte.files.PropertiesManager;
import jte.ui.JTEUI;

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

        JTEUI root = new JTEUI(primaryStage);

        String title = PropertiesManager.getValue("TITLE");
        primaryStage.setTitle(title);

        Scene scene = new Scene(root.getMainPane(), 1280, 861);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
