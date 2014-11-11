package jte.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jte.game.components.CityNode;

/**
 * @author
 */
public class JTEEventHandler {

    private JTEUI ui;

    public JTEEventHandler(JTEUI ui) {
        this.ui = ui;
    }

    public void respondToNewGameRequest() {
        ui.changeView(JTEUI.JTEUIState.PLAYER_SELECT);
    }

    public void respondToGameStartRequest() {
        ui.changeView(JTEUI.JTEUIState.GAME_PLAY);
    }

    public void respondToCityClick(CityNode city) {
        ui.displayCity(city);
    }

    /**
     * This method responds to when the user requests to exit the application.
     *
     * @param primaryStage The window that the user has requested to close.
     */
    public void respondToExitRequest(Stage primaryStage) {
        String options[] = new String[]{"Yes", "No"};

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane exitPane = new BorderPane();
        HBox optionPane = new HBox();
        Button yesButton = new Button(options[0]);
        Button noButton = new Button(options[1]);
        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().addAll(yesButton, noButton);
        Label exitLabel = new Label("Are you sure you want to quit?");
        exitPane.setCenter(exitLabel);
        exitPane.setBottom(optionPane);
        Scene scene = new Scene(exitPane, 200, 150);
        dialogStage.setScene(scene);
        dialogStage.show();
        // WHAT'S THE USER'S DECISION?
        yesButton.setOnAction(e -> {
            // YES, LET'S EXIT
            System.exit(0);
        });
        noButton.setOnAction(e -> {
            dialogStage.close();
        });
    }
}
