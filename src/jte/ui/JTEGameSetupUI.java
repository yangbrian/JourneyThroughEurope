package jte.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

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
        this.setStyle("-fx-background-color:#81b5dd; -fx-font-size: 1.2em");

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

        startGame = new Button("GO!!");
        startGame.setStyle("-fx-background-color:#004ba7");
        startGame.setTextFill(Color.WHITE);

        this.setPadding(new Insets(20));

        toolbar.setSpacing(10);
        toolbar.setPadding(new Insets(0, 0, 10, 0));

        toolbar.getChildren().addAll(playerSelect, playerCount, startGame);
        toolbar.setPrefSize(1280, 50);

        this.getChildren().add(toolbar);
    }

    public void createSelections(int oldPlayers, int players) {
        // add in new ones without replacing the current ones
        if (oldPlayers < players) {
            for (int i = oldPlayers; i < players; i++) {
                BorderPane playerSelect = new BorderPane();
                playerSelect.setPrefSize(400, 350);
                playerSelect.setStyle("-fx-background-color: #004ba7; -fx-background-radius: 10px; -fx-background-insets: 10px");
                playerSelect.setPadding(new Insets(20));

                Label newPlayer = new Label("New Player " + (i + 1));
                newPlayer.setTextFill(Color.WHITE);
                playerSelect.setTop(newPlayer);

                VBox playerOptions = new VBox();
                playerOptions.setSpacing(25);
                playerOptions.setPadding(new Insets(15));

                ToggleGroup playerType = new ToggleGroup();

                RadioButton human = new RadioButton();
                human.setText("Human");
                human.setTextFill(Color.WHITE);
                human.setToggleGroup(playerType);

                RadioButton computer = new RadioButton();
                computer.setText("Computer");
                computer.setTextFill(Color.WHITE);
                computer.setToggleGroup(playerType);

                TextField name = new TextField();
                name.setPrefColumnCount(50);
                name.setPromptText("Player name");

                playerOptions.getChildren().addAll(human, computer, name);

                playerSelect.setCenter(playerOptions);

                ImageView playerPiece = new ImageView(new Image("file:images/flag_"+ (i+1) + ".png"));
                playerSelect.setRight(playerPiece);

                this.getChildren().add(playerSelect);
            }
        }
        else { // remove excess player slots from the end
            for (int i = this.getChildren().size() - 1; i > players; i--) {
                System.out.println(i);
                this.getChildren().remove(i);
            }
        }
    }

    public Button getStartGameButton() {
        return startGame;
    }
}
