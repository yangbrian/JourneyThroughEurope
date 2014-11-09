package jte.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

/**
 * @author Brian Yang
 */
public class JTEGameSetupUI extends FlowPane {

    public enum JTEGameState {
        SPLASH_SCREEN, PLAYER_SELECT, GAME_IN_PROGRESS, GAME_OVER
    }

    private HBox toolbar;

    public JTEGameSetupUI() {
        toolbar = new HBox();
        Label playerSelect = new Label("Select the number of players: ");
        ComboBox playerCount = new ComboBox();
        playerCount.getItems().addAll(
                "1", "2", "3", "4", "5", "6"
        );

        playerCount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            createSelections(Integer.parseInt(newValue.toString()));
        });


        toolbar.getChildren().addAll(playerSelect, playerCount);
        this.getChildren().add(toolbar);
    }

    public void createSelections(int players) {
        for (int i = 0; i < players; i++) {
            BorderPane playerSelect = new BorderPane();
            playerSelect.setPrefSize(150, 150);

            Label newPlayer = new Label("New Player " + i);
            playerSelect.setTop(newPlayer);
            this.getChildren().add(playerSelect);
        }
    }
}
