package jte.ui;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jte.game.JTEGameStateManager;
import jte.game.components.CityNode;

/**
 * Main JTE User Interface
 * @author Brian Yang
 */
public class JTEUI {


    public enum JTEUIState {
        SPLASH_SCREEN,
        PLAYER_SELECT,
        GAME_PLAY
    }
    private BorderPane mainPane;
    private Stage primaryStage;

    /** Handles all types of events, including button clicks */
    private JTEEventHandler eventHandler;

    /** UI types */
    private JTEGamePlayUI gamePlayPane;
    private JTEGameSetupUI setupPane;
    private JTESplashUI splashScreen;

    /** manages the state of the JTE game */
    private JTEGameStateManager gsm;

    public JTEUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.mainPane = new BorderPane();
        this.eventHandler = new JTEEventHandler(this);
        this.gsm = new JTEGameStateManager(this);
        initSplashScreen();
        initSetupPane();
        initGamePlayScreen();
    }

    public void initSplashScreen() {
        splashScreen = new JTESplashUI();
        splashScreen.getNewGameButton().setOnAction(event -> {
            eventHandler.respondToNewGameRequest();
        });
        splashScreen.getAboutButton().setOnAction(event -> {
            eventHandler.respondToAboutRequest(primaryStage);
        });
        splashScreen.getQuitButton().setOnAction(event -> {
            eventHandler.respondToExitRequest(primaryStage);
        });

        mainPane.setCenter(splashScreen);
    }

    public void initSetupPane() {
        setupPane = new JTEGameSetupUI();
        setupPane.getStartGameButton().setOnAction(event -> {
            eventHandler.respondToGameStartRequest();
        });
    }

    public void initGamePlayScreen() {
        gamePlayPane = new JTEGamePlayUI(this, gsm.getInfo());
    }

    public void initMap() {

    }

    public void initCardToolbar() {

    }

    public void initSidebar() {

    }

    public void initGamePlay() {

    }

    public void changeView(JTEUIState view) {
        switch (view) {
            case SPLASH_SCREEN:
                break;
            case PLAYER_SELECT:
                System.out.println("Player select");
                mainPane.setCenter(setupPane);
                break;
            case GAME_PLAY:
                System.out.println("Game Start");
                mainPane.setCenter(gamePlayPane);
                break;
            default:
        }
    }

    public BorderPane getMainPane() {
        return mainPane;
    }

    /**
     * Method used for the purposes of testing for HW 5
     * @param city city that got clicked on
     */
    public void displayCity(CityNode city) {
        gamePlayPane.displayCity(city);
    }

    public JTEEventHandler getEventHandler() {
        return eventHandler;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public JTEGameStateManager getGsm() {
        return gsm;
    }
}
