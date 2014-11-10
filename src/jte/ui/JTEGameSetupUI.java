package jte.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * @author Brian Yang
 */
public class JTEGameSetupUI extends FlowPane {

    public enum JTEGameState {
        SPLASH_SCREEN, PLAYER_SELECT, GAME_IN_PROGRESS, GAME_OVER
    }

    private Button startGame;

    public JTEGameSetupUI() {
        HBox toolbar = new HBox();
        Label playerSelect = new Label("Select the number of players: ");
        ComboBox playerCount = new ComboBox();
        playerCount.getItems().addAll(
                "1", "2", "3", "4", "5", "6"
        );

        playerCount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null)
                createSelections(0, Integer.parseInt(newValue.toString()));
            else
                createSelections(Integer.parseInt(oldValue.toString()), Integer.parseInt(newValue.toString()));
        });

        startGame = new Button("Start Game");

        this.setPadding(new Insets(20));

        toolbar.setSpacing(10);
        toolbar.setPadding(new Insets(0, 0, 10, 0));

        toolbar.getChildren().addAll(playerSelect, playerCount, startGame);
        toolbar.setPrefSize(1280, 50);

        this.getChildren().add(toolbar);
    }

    public void createSelections(int oldPlayers, int players) {
        if (oldPlayers < players) { // add in new ones without replacing the current ones
            for (int i = oldPlayers; i < players; i++) {
                BorderPane playerSelect = new BorderPane();
                playerSelect.setPrefSize(400, 350);

                Label newPlayer = new Label("New Player " + (i+1));
                playerSelect.setTop(newPlayer);
                this.getChildren().add(playerSelect);
            }
        } else { // remove excess player slots from the end
            for (int i = this.getChildren().size() - 1; i > players; i--) {
                System.out.println(i);
                this.getChildren().remove(i);
            }
        }
//        for (int i = 0; i < players; i++) {
//            BorderPane playerSelect = new BorderPane();
//            playerSelect.setPrefSize(400, 350);
//
//            Label newPlayer = new Label("New Player " + (i+1));
//            playerSelect.setTop(newPlayer);
//            this.getChildren().add(playerSelect);
//        }
    }

    public Button getStartGameButton() {
        return startGame;
    }
}
