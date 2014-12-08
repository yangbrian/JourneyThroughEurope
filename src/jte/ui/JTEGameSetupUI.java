package jte.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import jte.files.PropertiesManager;
import jte.game.components.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Yang
 */
public class JTEGameSetupUI extends FlowPane {

    private Button startGame;
    private ArrayList<Player> players;

    public JTEGameSetupUI() {
        this.setStyle("-fx-background-color:#81b5dd; -fx-font-size: 1.2em");

        players = new ArrayList<>();

        HBox toolbar = new HBox();
        Label playerSelect = new Label(PropertiesManager.getValue("SELECT"));
        ComboBox<Integer> playerCount = new ComboBox<>();
        playerCount.getItems().addAll(
                1, 2, 3, 4, 5, 6
        );

        playerCount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null)
                createSelections(0, newValue);
            else
                createSelections(oldValue, newValue);
        });

        startGame = new Button(PropertiesManager.getValue("GO"));
        startGame.getStyleClass().add("start-game-button");
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

                this.players.add(i, new Player(PropertiesManager.getValue("PLAYER") + (i + 1), true, i));
                BorderPane playerSelect = new BorderPane();
                playerSelect.setPrefSize(400, 350);
                playerSelect.setStyle("-fx-background-color: #004ba7; -fx-background-radius: 10px; -fx-background-insets: 10px");
                playerSelect.setPadding(new Insets(20));

                Label newPlayer = new Label(PropertiesManager.getValue("NEWPLAYER") + (i + 1));
                newPlayer.setTextFill(Color.WHITE);
                playerSelect.setTop(newPlayer);

                VBox playerOptions = new VBox();
                playerOptions.setSpacing(25);
                playerOptions.setPadding(new Insets(15));

                ToggleGroup playerType = new ToggleGroup();

                RadioButton human = new RadioButton();
                human.setText(PropertiesManager.getValue("HUMAN"));
                human.setUserData(PropertiesManager.getValue("HUMAN"));
                human.setTextFill(Color.WHITE);
                human.setSelected(true);
                human.setToggleGroup(playerType);

                RadioButton computer = new RadioButton();
                computer.setText(PropertiesManager.getValue("COMPUTER"));
                computer.setUserData(PropertiesManager.getValue("COMPUTER"));
                computer.setTextFill(Color.WHITE);
                computer.setToggleGroup(playerType);

                final int finalI = i;
                playerType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                    if (playerType.getSelectedToggle().getUserData().toString().equals(PropertiesManager.getValue("COMPUTER")))
                        this.players.get(finalI).robotize(true);
                    else
                        this.players.get(finalI).robotize(false);
                });

                TextField name = new TextField();
                name.setPrefColumnCount(50);
                name.setPromptText(PropertiesManager.getValue("PLAYERNAME"));

                name.textProperty().addListener((observable, oldValue, newValue) -> {
                    if(!newValue.equals(PropertiesManager.getValue("PLAYERNAME")))
                        this.players.get(finalI).setName(newValue);
                    else
                        this.players.get(finalI).setName(PropertiesManager.getValue("PLAYER") + (finalI + 1));
                });

                playerOptions.getChildren().addAll(human, computer, name);

                playerSelect.setCenter(playerOptions);

                ImageView playerPiece = new ImageView(new Image("file:images/flag_"+ (i+1) + ".png"));
                playerSelect.setRight(playerPiece);

                this.getChildren().add(playerSelect);
            }
        }
        else { // remove excess player slots from the end
            for (int i = this.getChildren().size() - 1; i > players; i--) {
                this.players.remove(i - 1);
                System.out.println(i);
                this.getChildren().remove(i);
            }
        }
    }

    public Button getStartGameButton() {
        return startGame;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
