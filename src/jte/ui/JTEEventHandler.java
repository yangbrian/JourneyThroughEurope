package jte.ui;

import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jte.game.JTEGameStateManager;
import jte.game.components.CityNode;
import jte.game.components.Player;

import java.util.ArrayList;

/**
 * @author
 */
public class JTEEventHandler {

    private JTEUI ui;
    private boolean moving;

    public JTEEventHandler(JTEUI ui) {
        this.ui = ui;
        moving = false;
    }

    public void respondToNewGameRequest() {
        ui.changeView(JTEUI.JTEUIState.PLAYER_SELECT);
    }

    public void respondToGameStartRequest() {
        ui.changeView(JTEUI.JTEUIState.GAME_PLAY);
    }

    public void respondToCityClick(CityNode city) {

        if (!moving) {
            String currentCityName = ui.getGsm().getData().getCurrent().getCurrentCity();
            CityNode currentCity = ui.getGsm().getInfo().getCities().get(currentCityName);

            // check if valid move
            if (currentCity.getRoads().contains(city)) { // valid road
                moving = true;
                PathTransition move = ui.getGsm().movePlayer(city);

                move.setOnFinished(event -> {
                    if (ui.getGsm().hasMovesLeft()) {
                        ui.displayCity(city);
                    } else {
                        ui.getGsm().nextPlayer();
                    }
                    notMoving();
                });


            }
        }



    }

    public final void notMoving() {
        moving = false;
    }

    /**
     * This method responds to when the user requests to exit the application.
     *
     * @param primaryStage The window that the user has requested to close.
     */
    public void respondToExitRequest(Stage primaryStage) {
        System.exit(0);
    }

    public void respondToAboutRequest(Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("About Journey Through Europe");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane aboutPane = new BorderPane();
        HBox optionPane = new HBox();
        Button okButton = new Button("Return to Game");
        okButton.setStyle("-fx-font-size: 1.1em; -fx-background-color:#ecc323;-fx-background-radius: 30;");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label aboutLabel = new Label("About Journey Through Europe");
        aboutLabel.setStyle("-fx-font-size: 2.0em;");

        Label version = new Label("Version 0.1");
        version.setStyle("-fx-font-size: 1.0em;");

        Label description = new Label("Journey through Europe is a family board game published by Ravensburger. The board is a map of Europe with various major cities marked, for example, Athens, Amsterdam and London. The players are given a home city from which they will begin and are then dealt a number of cards with various other cities on them. They must plan a route between each of the cities in their hand of cards. On each turn they throw a die and move between the cities. The winner is the first player to visit each of their cities and then return to their home base.");
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 1.2em");

        Label company = new Label("This version of JTE is created by Debugging Enterprises, a company that exists for the purposes of CSE 219. By Brian Yang.");
        company.setWrapText(true);
        company.setStyle("-fx-font-size: 1.2em");

        content.getChildren().addAll(aboutLabel, version, description, company);

        aboutPane.setCenter(content);


        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 500, 450);
        dialogStage.setScene(scene);
        dialogStage.show();

        okButton.setOnAction(e -> dialogStage.close());
    }

    public void respondToHistoryRequest(Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Current Game History");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane aboutPane = new BorderPane();
        HBox optionPane = new HBox();
        Button okButton = new Button("Close");
        okButton.setStyle("-fx-font-size: 1.1em; -fx-background-color:#ecc323;-fx-background-radius: 30;");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label aboutLabel = new Label("JTE Game History");
        aboutLabel.setStyle("-fx-font-size: 2.0em;");

        Label description = new Label("History of moves would go here.");
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 1.2em");

        content.getChildren().addAll(aboutLabel, description);

        aboutPane.setCenter(content);

        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 500, 450);
        dialogStage.setScene(scene);
        dialogStage.show();

        okButton.setOnAction(e -> dialogStage.close());
    }

    public void placeFlag(int player) {
        ui.getGamePlayPane().placeFlags(player);
    }

    public void hoverOverCity(boolean hover) {
        if(hover)
            ui.getMainPane().getScene().setCursor(Cursor.HAND);
        else
            ui.getMainPane().getScene().setCursor(Cursor.DEFAULT);
    }

    public void startGame() {
        ui.getGsm().startGame();
    }

    public CityNode playerDrop(Player player, double x, double y) {
        ArrayList<CityNode> neighbors = ui.getGsm().getInfo().getCities().get(player.getCurrentCity()).getRoads();
        for (CityNode city : neighbors) {
            boolean intersect = city.intersects(x + 50, y + 50, 100, 100);
            if (intersect)
                return city;
        }
        return null;
    }

    public boolean respondToPlayerDrag(Player player) {
        return ui.getGsm().getData().getCurrent() == player;
    }
}
