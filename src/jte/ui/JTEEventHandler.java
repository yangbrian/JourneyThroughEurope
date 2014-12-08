package jte.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jte.files.PropertiesManager;
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

    public void respondToCityClick(CityNode city, boolean flight) {

        cardRemoved = false; // remove card removed flag

        if (!moving && ui.getGsm().rolled()) {
            String currentCityName = gsm.getData().getCurrent().getCurrentCity();
            CityNode currentCity = gsm.getInfo().getCities().get(currentCityName);

            // check if valid move
            if ((flight) // true if taking flight, so ignore other conditions, since the check is already done
              || (!gsm.getCurrentPlayer().isHuman()) // illegal moves check for computer players
              || (currentCity.getRoads().contains(city) || (currentCity.getShips().contains(city) && gsm.waited())) // city is a neighbor
              && (!city.isOccupied() || gsm.getMovesLeft() > 1) // city is not occupied OR its not player's final move (can't stay in occupied city)
              && (city != gsm.getLastCity() || currentCity.getRoads().size() <= 1) // not an avoidable backtrack
              ) {




                boolean moveReady = true;
                Player currentPlayer = gsm.getData().getCurrent();
                if (!currentPlayer.isHuman()) { // check for legal move
                    if (currentCity.getShips().contains(city)) {
                        if (!currentPlayer.isPortClear())
                            moveReady = false;
                    }
                    if (city.getRegion() == currentCity.getRegion() && flight) {
                        if (currentPlayer.getMoves() < 2)
                            moveReady = false;
                        else {
                            currentPlayer.setMoves(currentPlayer.getMoves() - 1);
                        }
                    } else if (city.getRegion() != currentCity.getRegion() && flight) {
                        if (currentPlayer.getMoves() < 4)
                            moveReady = false;
                        else {

                            currentPlayer.setMoves(currentPlayer.getMoves() - 3);
                        }
                    }
                }


                if (moveReady) {

                    moving = true;
                    currentCity.setOccupied(false);
                    city.setOccupied(true);
                    gsm.waitAtPort(false);
                    gsm.setLastCity(currentCity);

                    ui.getGamePlayPane().getPortWaitButton().setText(PropertiesManager.getValue("WAIT"));

                    PathTransition move = gsm.movePlayer(city);
                    move.setOnFinished(event -> {
                        Player player = gsm.getData().getCurrent();
                        System.out.println("Landed on: " + city.getName());
                        if ((player.getCards().contains(city.getName()) && !city.getName().equals(player.getHome()))
                          || (city.getName().equals(player.getHome()) && player.getCards().size() == 1)) { // reached destination
                            gsm.removeCard(city);
                            player.setMoves(0);
                            cardRemoved = true;

                        }

                        Timeline timeline = ui.getGamePlayPane().getMap().focusPlayer(player);
                        PauseTransition computerPause = new PauseTransition(Duration.millis(1000));

                        timeline.setOnFinished(e -> {
                            if (!player.isHuman())
                                computerPause.play();
                            boolean ship = false;
                            if (currentCity.getShips().contains(city)) { // only one move for sailing
                                ship = true;
                                player.setMoves(0);
                            }

                            System.out.println(player.getName() + " " + gsm.getMovesLeft() + " " + player.getMoves());
                            if (gsm.hasMovesLeft()) {
                                ui.getGamePlayPane().setDiceLabel(gsm.getMovesLeft());
                                ui.displayCity(city);

                            } else if (gsm.getData().getCurrent().getsRepeat()) {
                                if (!city.getShips().isEmpty()) // if has port, then player has waited this turn
                                    gsm.waitAtPort(true);
                                gsm.setLastCity(null);
                                ui.getGamePlayPane().stopCityAnimation();
                                if (player.isHuman())
                                    gsm.repeatPlayer();
                                else {
                                    computerPause.setOnFinished(pcFocus -> {
                                        gsm.repeatComputer();
                                    });
                                }
                            } else {
                                if (!city.getShips().isEmpty()) // if has port, then player has waited this turn
                                    gsm.waitAtPort(true);
                                gsm.setLastCity(null);
                                ui.getGamePlayPane().stopCityAnimation();
                                if (!cardRemoved)
                                    gsm.nextPlayer();
                            }
                            notMoving();

                            if (!player.isHuman() && !cardRemoved && !ship)
                                computerPause.setOnFinished(pcFocus -> continueComputerTurn());

                            int movesLeft = player.getMoves();
                            // Add move to game history
                            if (flight)
                                gsm.addToHistory(player.getName() + PropertiesManager.getValue("FLEWFROM") + currentCityName + PropertiesManager.getValue("TO") + city.getName() + PropertiesManager.getValue("WITH")+ movesLeft + PropertiesManager.getValue("MOVESLEFT"));
                            else if (ship) {
                                gsm.addToHistory(player.getName() + PropertiesManager.getValue("SAILEDFROM") + currentCityName + PropertiesManager.getValue("TO") + city.getName() + PropertiesManager.getValue("WITH")+ movesLeft + PropertiesManager.getValue("MOVESLEFT"));
                            } else {
                                gsm.addToHistory(player.getName() + PropertiesManager.getValue("MOVEDFROM") + currentCityName + PropertiesManager.getValue("TO") + city.getName() + PropertiesManager.getValue("WITH")+ movesLeft + PropertiesManager.getValue("MOVESLEFT"));
                            }
                            if (cardRemoved)
                                gsm.addToHistory(player.getName() + PropertiesManager.getValue("DESTINATION") + city.getName());


                        });


                    });
                } else {
                    gsm.addToHistory(currentPlayer.getName() + PropertiesManager.getValue("WAITEDAT") + currentCityName);
                    if (gsm.getData().getCurrent().getsRepeat()) {
                        if (!city.getShips().isEmpty()) // if has port, then player has waited this turn
                            gsm.waitAtPort(true);
                        gsm.setLastCity(null);
                        ui.getGamePlayPane().stopCityAnimation();
                        ui.getGamePlayPane().focusPlayer(currentPlayer).setOnFinished(pcFocus -> {
                            gsm.repeatComputer();
                        });
                    } else {
                        gsm.setLastCity(null);
                        ui.getGamePlayPane().stopCityAnimation();
                        gsm.nextPlayer();
                    }
                }

            } else if (city == ui.getGsm().getLastCity() && currentCity.getRoads().size() > 1) {

                displayDeadEndError();

            } else if ((currentCity.getShips().contains(city) && !ui.getGsm().waited())) {
                Stage dialogStage = new Stage();
                dialogStage.setTitle(PropertiesManager.getValue("GENERALERROR"));
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(ui.getPrimaryStage());
                BorderPane aboutPane = new BorderPane();
                aboutPane.getStylesheets().add("file:data/jte.css");
                HBox optionPane = new HBox();
                Button okButton = new Button(PropertiesManager.getValue("CLOSE"));
                okButton.getStyleClass().add("dialog-button");

                optionPane.setSpacing(20.0);
                optionPane.setPadding(new Insets(20));
                optionPane.getChildren().add(okButton);

                VBox content = new VBox();
                content.setPadding(new Insets(20));
                content.setSpacing(20);

                Label description = new Label(PropertiesManager.getValue("PORTERROR"));
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
        dialogStage.setTitle(PropertiesManager.getValue("GENERALERROR"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ui.getPrimaryStage());
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button(PropertiesManager.getValue("CLOSE"));
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label description = new Label(PropertiesManager.getValue("BACKTRACK"));
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
        dialogStage.setTitle(PropertiesManager.getValue("ABOUTTITLE"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button(PropertiesManager.getValue("RETURN"));
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label aboutLabel = new Label(PropertiesManager.getValue("ABOUTTITLE"));
        aboutLabel.setStyle("-fx-font-size: 2.0em;");

        Label version = new Label("Version 0.1");
        version.setStyle("-fx-font-size: 1.0em;");

        Label description = new Label(PropertiesManager.getValue("ABOUTDESC"));
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 1.2em");

        Label company = new Label(PropertiesManager.getValue("CONTACT"));
        company.setWrapText(true);
        company.setStyle("-fx-font-size: 1.2em");

        content.getChildren().addAll(aboutLabel, version, description, company);

        aboutPane.setCenter(content);


        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 550, 450);
        dialogStage.setScene(scene);
        dialogStage.show();

        okButton.setOnAction(e -> dialogStage.close());
    }

    public void respondToHistoryRequest(Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(PropertiesManager.getValue("HISTORYTITLE"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button(PropertiesManager.getValue("CLOSE"));
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label aboutLabel = new Label(PropertiesManager.getValue("HISTORYTITLE2"));
        aboutLabel.setStyle("-fx-font-size: 2.0em;");

        StringBuilder history = new StringBuilder();
        LinkedList<String> historyList = gsm.getHistory();

        for (String aHistoryList : historyList)
            history.append(aHistoryList).append("\n");

        ScrollPane historyPane = new ScrollPane();

        Label description = new Label(history.toString());
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 1.2em");

        historyPane.setContent(description);

        content.getChildren().addAll(aboutLabel, historyPane);

        aboutPane.setCenter(content);

        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 500, 650);
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

    public void startGame(boolean newGame) {
        ui.getGsm().startGame(newGame);
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
        Button okButton = new Button(PropertiesManager.getValue("CLOSE"));
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label description = new Label(PropertiesManager.getValue("NOPLAYERS"));
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
        gsm.addToHistory(gsm.getData().getCurrent().getName() + PropertiesManager.getValue("WAITSSHIP"));
        if (gsm.getData().getCurrent().getsRepeat())
            gsm.repeatPlayer();
        else
            gsm.nextPlayer();
    }

    public void respondToPlayerWin(Player current) {
        gsm.setGameState(JTEGameStateManager.JTEGameState.GAME_OVER);
        Stage dialogStage = new Stage();
        dialogStage.setTitle(current.getName() + " wins!");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ui.getPrimaryStage());
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");

        HBox optionPane = new HBox();
        optionPane.setSpacing(10);
        Button okButton = new Button(PropertiesManager.getValue("RETURNSPLASH"));
        okButton.getStyleClass().add("dialog-button");

        Button historyButton = new Button(PropertiesManager.getValue("HISTORY"));
        historyButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().addAll(okButton, historyButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label description = new Label(current.getName() + " wins!!!");
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 2.0em");
        content.getChildren().add(description);

        if (!current.isHuman()) {
            Label lose = new Label(current.getName() + PropertiesManager.getValue("COMPUTERPLAYER"));
            lose.setWrapText(true);
            lose.setStyle("-fx-font-size: 1.2em");

            content.getChildren().add(lose);
        }

        aboutPane.setCenter(content);

        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 500, 250);
        dialogStage.setScene(scene);
        dialogStage.show();

        okButton.setOnAction(e -> {
            ui.changeView(JTEUI.JTEUIState.SPLASH_SCREEN);
            dialogStage.close();
        });

        historyButton.setOnAction(e -> respondToHistoryRequest(dialogStage));
    }

    public void respondToSaveRequest() {
        Label description;
        String title;
        try {
            gsm.saveGame();
            title = PropertiesManager.getValue("SAVEGAMETITLE");
            description = new Label(PropertiesManager.getValue("SAVEGAME"));
        } catch (IOException e) {
            title = PropertiesManager.getValue("SAVEGAMEERRORTITLE");
            description = new Label(PropertiesManager.getValue("SAVEGAMEERROR"));
        }
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ui.getPrimaryStage());
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button(PropertiesManager.getValue("CLOSE"));
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
            dialogStage.setTitle(PropertiesManager.getValue("LOADERRORTITLE"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            BorderPane aboutPane = new BorderPane();
            aboutPane.getStylesheets().add("file:data/jte.css");
            HBox optionPane = new HBox();
            Button okButton = new Button(PropertiesManager.getValue("CLOSE"));
            okButton.getStyleClass().add("dialog-button");

            optionPane.setSpacing(20.0);
            optionPane.setPadding(new Insets(20));
            optionPane.getChildren().add(okButton);

            VBox content = new VBox();
            content.setPadding(new Insets(20));
            content.setSpacing(20);

            Label description = new Label(PropertiesManager.getValue("LOADERROR"));
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
            errorMsg = PropertiesManager.getValue("INVALIDFLIGHTREGION");
        else
            errorMsg = PropertiesManager.getValue("INVALIDFLIGHTMOVES");
        Stage dialogStage = new Stage();
        dialogStage.setTitle(PropertiesManager.getValue("INVALIDFLIGHT"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ui.getPrimaryStage());
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button(PropertiesManager.getValue("CLOSE"));
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

    public void continueComputerTurn() {
        gsm.moveComputer();
    }

    public void respondToCityInfoRequest(Stage primaryStage, String currentCity) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(PropertiesManager.getValue("ABOUTCITY") + currentCity);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane aboutPane = new BorderPane();
        aboutPane.getStylesheets().add("file:data/jte.css");
        HBox optionPane = new HBox();
        Button okButton = new Button(PropertiesManager.getValue("CLOSE"));
        okButton.getStyleClass().add("dialog-button");

        optionPane.setSpacing(20.0);
        optionPane.setPadding(new Insets(20));
        optionPane.getChildren().add(okButton);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        Label title = new Label(currentCity.replace('_', ' '));
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 2.0em");

        Label description = new Label(PropertiesManager.getDescription(currentCity));
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 1.2em");

        content.getChildren().addAll(title, description);

        aboutPane.setCenter(content);

        aboutPane.setBottom(optionPane);
        Scene scene = new Scene(aboutPane, 600, 300);
        dialogStage.setScene(scene);
        dialogStage.show();

        okButton.setOnAction(e -> dialogStage.close());
    }
}
