package jte.ui;

import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jte.game.JTEGameStateManager;

/**
 * Main JTE User Interface
 * @author Brian Yang
 */
public class JTEUI extends StackPane {
    private Stage primaryStage;
    private StackPane mainPane;

    /** Handles all types of events, including button clicks */
    private JTEEventHandler eventHandler;

    /** UI types */
    private JTEGamePlayUI gamePlayPane;
    private FlowPane setupPane;
    private JTESplashUI splashScreen;

    /** manages the state of the JTE game */
    private JTEGameStateManager gsm;

    public JTEUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
        splashScreen = new JTESplashUI();
        this.getChildren().add(splashScreen);
    }


    public void initMainPane() {

    }

    public void initSplashScreen() {

    }

    public void initGamePlayScreen() {

    }

    public void initMap() {

    }

    public void initCardToolbar() {

    }

    public void initSidebar() {

    }

    public void initGamePlay() {

    }

    public void changeView() {

    }
}
