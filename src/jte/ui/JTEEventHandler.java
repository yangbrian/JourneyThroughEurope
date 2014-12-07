package jte.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import jte.game.JTEGameStateManager;
import jte.game.components.CityNode;
import jte.game.components.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author
 */
public class JTEEventHandler {

    private JTEUI ui;
    private boolean moving;
    private boolean cardRemoved;
    public JTEGameStateManager gsm;

    public JTEEventHandler(JTEUI ui, JTEGameStateManager gsm) {
        this.ui = ui;
        moving = false;
        cardRemoved = false;
        this.gsm = gsm;
    }

    public void respondToNewGameRequest() {
        ui.changeView(JTEUI.JTEUIState.PLAYER_SELECT);
    }

    public void respondToGameStartRequest() {
        ui.changeView(JTEUI.JTEUIState.GAME_PLAY);
    }

    public void respondToCityClick(CityNode city, boolean flag) {

        cardRemoved = false; // remove card removed flag

        if (!moving && ui.getGsm().rolled()) {
            String currentCityName = gsm.getData().getCurrent().getCurrentCity();
            CityNode currentCity = gsm.getInfo().getCities().get(currentCityName);

            // check if valid move
            if ((flag) // true if taking flight, so ignore other conditions, since the check is already done
              || (currentCity.getRoads().contains(city) || (currentCity.getShips().contains(city) && gsm.waited())) // city is a neighbor
              && (!city.isOccupied() || gsm.getMovesLeft() > 1) // city is not occupied OR its not player's final move (can't stay in occupied city)
              && (city != gsm.getLastCity() || currentCity.getRoads().size() <= 1) // not an avoidable backtrack
              ) {

                moving = true;
                currentCity.setOccupied(false);
                city.setOccupied(true);
                gsm.waitAtPort(false);
                gsm.setLastCity(currentCity);

                ui.getGamePlayPane().getPortWaitButton().setText("Wait for Ship");

                PathTransition move = gsm.movePlayer(city);

                move.setOnFinished(event -> {
                    Player player = gsm.getData().getCurrent();
                    System.out.println("Landed on: " + city.getName());
                    ui.getGamePlayPane().setTranslate(city.getX() - 100, city.getY() - 125);
                    if ((player.getCards().contains(city.getName()) && !city.getName().equals(player.getHome()))
                      || (city.getName().equals(player.getHome()) && player.getCards().size() == 1)) { // reached destination
                        gsm.removeCard(city);
                        player.setMoves(0);
                        cardRemoved = true;

                    }

                    if (currentCity.getShips().contains(city)) { // only one move for sailing
                        player.setMoves(0);
                    }

                    if (gsm.hasMovesLeft()) {
                        ui.getGamePlayPane().setDiceLabel(gsm.getMovesLeft());
                        ui.displayCity(city);
                    } else if (gsm.getData().getCurrent().getsRepeat()) {
                        if (!city.getShips().isEmpty()) // if has port, then player has waited this turn
                            gsm.waitAtPort(true);
                        gsm.setLastCity(null);
                        ui.getGamePlayPane().stopCityAnimation();
                        gsm.repeatPlayer();
                    } else {
                        if (!city.getShips().isEmpty()) // if has port, then player has waited this turn
                            gsm.waitAtPort(true);
                        gsm.setLastCity(null);
                        ui.getGamePlayPane().stopCityAnimation();
                        if (!cardRemoved)
                            gsm.nextPlayer();
                    }
                    notMoving();

                    // Add move to game history
                    if (flag)
                        gsm.addToHistory(player.getName() + " flew from " + currentCityName + " to " + city.getName());
                    else
                        gsm.addToHistory(player.getName() + " moved from " + currentCityName + " to " + city.getName());
                    ui.getGamePlayPane().getMap().focusPlayer(player);
                });

            } else if (city == ui.getGsm().getLastCity() && currentCity.getRoads().size() > 1) {

                displayDeadEndError();

            } else if ((currentCity.getShips().contains(city) && !ui.getGsm().waited())) {
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Error");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(ui.getPrimaryStage());
                BorderPane aboutPane = new BorderPane();
                aboutPane.getStylesheets().add("file:data/jte.css");
                HBox optionPane = new HBox();
                Button okButton = new Button("Close");
                okButton.getStyleClass().add("dialog-button");

                optionPane.setSpacing(20.0);
                optionPane.setPadding(new Insets(20));
                optionPane.getChildren().add(okButton);

                VBox content = new VBox();
                content.setPadding(new Insets(20));
                content.setSpacing(20);

                Label description = new Label("You must wait at the port for one turn before traveling by sea.");
                description.setWrapText(true);
                description.setStyle("-fx-font-size: 1.2em");

                content.getChildren().add(description);

                aboutPane.setCenter(content);

                aboutPane.setBottom(optionPane);
                Scene scene = new Scene(aboutPane, 400, 150);
                dialogStage.setScene(scene);
                dialogStage.show();

                okButton.setOnAction(e -> dialogStage.close());
            }
        }
    }

    private void displayDeadEndError() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Error");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ui.getPrimaryStage());
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button("Close");
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label description = new Label("This isn't a deadend, so no backtracking!");
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 1.2em");

        content.getChildren().add(description);

        aboutPane.setCenter(content);

        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 400, 150);
        dialogStage.setScene(scene);
        dialogStage.show();

        okButton.setOnAction(e -> dialogStage.close());
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
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button("Return to Game");
        okButton.getStyleClass().add("dialog-button");

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
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button("Close");
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label aboutLabel = new Label("JTE Game History");
        aboutLabel.setStyle("-fx-font-size: 2.0em;");

        StringBuilder history = new StringBuilder();
        LinkedList<String> historyList = gsm.getHistory();

        for (String aHistoryList : historyList)
            history.append(aHistoryList).append("\n");

        Label description = new Label(history.toString());
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

    public void placeFlag(int player, boolean newGame) {
        ui.getGamePlayPane().placeFlags(player, newGame);
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
            boolean intersect = city.intersects(x + 15, y + 15, 100, 100);
            if (intersect) {
                if (city == ui.getGsm().getLastCity())
                    displayDeadEndError();
                else
                    return city;
            }
        }
        return null;
    }

    public boolean respondToPlayerDrag(Player player) {
        return ui.getGsm().getData().getCurrent() == player;
    }

    public void respondToBlankGameRequest(Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Error");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button("Close");
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label description = new Label("Starting a game with no players? That's no fun...");
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 1.2em");

        content.getChildren().add(description);

        aboutPane.setCenter(content);

        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 400, 150);
        dialogStage.setScene(scene);
        dialogStage.show();

        okButton.setOnAction(e -> dialogStage.close());
    }

    public void respondToPortRequest() {
        gsm.waitAtPort(true);
        gsm.addToHistory(gsm.getData().getCurrent().getName() + " waits for a ship.");
        if (gsm.getData().getCurrent().getsRepeat())
            gsm.repeatPlayer();
        else
            gsm.nextPlayer();
    }

    public void respondToPlayerWin(Player current) {
        gsm.setGameState(JTEGameStateManager.JTEGameState.GAME_OVER);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Error");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ui.getPrimaryStage());
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button("Close");
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label description = new Label(current.getName() + " wins!!!");
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 1.2em");

        content.getChildren().add(description);

        aboutPane.setCenter(content);

        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 400, 150);
        dialogStage.setScene(scene);
        dialogStage.show();

        okButton.setOnAction(e -> {
            ui.changeView(JTEUI.JTEUIState.SPLASH_SCREEN);
            dialogStage.close();
        });
    }

    public void respondToSaveRequest() {
        Label description;
        String title;
        try {
            gsm.saveGame();
            title = "Saved Game Successfully";
            description = new Label("Saved game! Select \"Load Game \" from the main menu to continue this game.");
        } catch (IOException e) {
            title = "Error Saving Game";
            description = new Label("Error saving game! The file might be in use or you don't have permission to write to it");
        }
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ui.getPrimaryStage());
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button("Close");
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        description.setWrapText(true);
        description.setStyle("-fx-font-size: 1.2em");

        content.getChildren().add(description);

        aboutPane.setCenter(content);

        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 400, 150);
        dialogStage.setScene(scene);
        dialogStage.show();

        okButton.setOnAction(event -> dialogStage.close());
    }

    public void respondToLoadRequest(JTEUI ui, Stage primaryStage) {
        try {
            ui.getGsm().loadGame();
            ui.changeView(JTEUI.JTEUIState.CONTINUE_PLAY);
        } catch (IOException e) {
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Error Loading Game");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            BorderPane aboutPane = new BorderPane();
            aboutPane.getStylesheets().add("file:data/jte.css");
            HBox optionPane = new HBox();
            Button okButton = new Button("Close");
            okButton.getStyleClass().add("dialog-button");

            optionPane.setSpacing(20.0);
            optionPane.setPadding(new Insets(20));
            optionPane.getChildren().add(okButton);

            VBox content = new VBox();
            content.setPadding(new Insets(20));
            content.setSpacing(20);

            Label description = new Label("Error loading game! The file might not exist or you don't have permission to access it.");
            description.setWrapText(true);
            description.setStyle("-fx-font-size: 1.2em");

            content.getChildren().add(description);

            aboutPane.setCenter(content);

            aboutPane.setBottom(optionPane);
            Scene scene = new Scene(aboutPane, 400, 150);
            dialogStage.setScene(scene);
            dialogStage.show();

            okButton.setOnAction(event -> dialogStage.close());
        }
    }

    public void respondToFlightRequest(JTEUI ui) {
        if (ui.getGamePlayPane().isFlight())
            ui.getGamePlayPane().switchToFlight(false);
        else
            ui.getGamePlayPane().switchToFlight(true);
    }

    public void respondToFlightCityClick(CityNode city) {
        String currentCityName = gsm.getData().getCurrent().getCurrentCity();
        CityNode currentCity = gsm.getInfo().getCities().get(currentCityName);

        int region = currentCity.getRegion();
        int destRegion = city.getRegion();

        // Adjacent Regions:
        // 1: 2, 4
        // 2: 1, 3
        // 3: 2, 4, 6
        // 4: 1, 3, 5
        // 5: 4, 6
        // 6: 3, 5

        if (region == destRegion) { // same region - 2 moves
            Player player = ui.getGsm().getCurrentPlayer();
            if (player.getMoves() >= 2) {
                player.takeFlight(2);
                ui.getGamePlayPane().switchToFlight(false);
                respondToCityClick(gsm.getInfo().getCities().get(city.getName()), true);
            } else {
                respondToInvalidFlightCityClick(false);
            }
        } else {
            boolean valid = false;
            switch (region) {
                case 1:
                    if (destRegion == 2 || destRegion == 4)
                        valid = true;
                    break;
                case 2:
                    if (destRegion == 1 || destRegion == 3)
                        valid = true;
                    break;
                case 3:
                    if (destRegion == 2 || destRegion == 4 || destRegion == 6)
                        valid = true;
                    break;
                case 4:
                    if (destRegion == 1 || destRegion == 3 || destRegion == 5)
                        valid = true;
                    break;
                case 5:
                    if (destRegion == 4 || destRegion == 6)
                        valid = true;
                    break;
                case 6:
                    if (destRegion == 3 || destRegion == 5)
                        valid = true;
                    break;
                default:
                    System.out.println("Not a flight region...");
            }
            if (valid) {
                Player player = ui.getGsm().getCurrentPlayer();
                if (player.getMoves() >= 4) {
                    player.takeFlight(4);
                    ui.getGamePlayPane().switchToFlight(false);
                    respondToCityClick(gsm.getInfo().getCities().get(city.getName()), true);
                } else {
                    respondToInvalidFlightCityClick(false);
                }
            } else {
                respondToInvalidFlightCityClick(true);
            }
        }
    }

    private void respondToInvalidFlightCityClick(boolean region) {
        String errorMsg;
        if (region)
            errorMsg = "Invalid flight destination. You can only fly to cities in the same region or in a region directly adjacent.";
        else
            errorMsg = "Insufficient moves to fly there. Flying within the same region requires 2 moves and flying to adjacent ones requires 4 moves.";
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Invalid Flight City!");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ui.getPrimaryStage());
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button("Close");
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label description = new Label(errorMsg);
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 1.2em");

        content.getChildren().add(description);

        aboutPane.setCenter(content);

        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 460, 150);
        dialogStage.setScene(scene);
        dialogStage.show();

        okButton.setOnAction(event -> dialogStage.close());
    }

    public void startComputerTurn() {
        gsm.rollDie(ui.getGamePlayPane().getDie());
        gsm.moveComputer();
    }
}
