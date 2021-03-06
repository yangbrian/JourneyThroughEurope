package jte.ui;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jte.files.PropertiesManager;
import jte.game.JTEGameData;
import jte.game.JTEGameInfo;
import jte.game.JTEGameStateManager;
import jte.game.components.CityNode;
import jte.game.components.Player;
import jte.ui.components.Dice;
import jte.ui.components.FlightPlan;
import jte.ui.components.Map;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Brian Yang
 */
public class JTEGamePlayUI extends BorderPane {

    private Pane[] cardToolbar;
    private VBox playerSidebar;
    private Map map;
    private JTEUI ui;

    private Dice dice;

    private ArrayList<FadeTransition> neighborAnimation;

    private Label rollDiceLabel;
    public static final String ROLL_DICE = PropertiesManager.getValue("ROLLDICE");

    private Button portWait;
    private Button takeFlight;

    private JTEGameInfo info;
    private JTEGameStateManager gsm;
    private JTEGameData currentGame;

    private FlightPlan flightPlan;
    private Stage flightStage;

    private boolean flight;
    private Button save;

    ArrayList<Line> neighborLines;

    public JTEGamePlayUI(JTEUI ui) {

        initPlayerSidebar();
        this.ui = ui;
        this.gsm = ui.getGsm();
        this.info = gsm.getInfo();
        this.flight = false;

        this.setWidth(1280);
        initMap();
        initFlightPlan();

        neighborAnimation = new ArrayList<>();
        neighborLines = new ArrayList<>();
    }

    public void drawCards(boolean newGame) {
        initCardToolbar();
        this.setLeft(cardToolbar[0]);
        // draw cards
        if (newGame)
            gsm.drawCards();

        this.currentGame = gsm.getData();

        LinkedList<Transition> animations = new LinkedList<>();
        for (int i = 0; i < currentGame.getPlayers().size(); i++) {
            int YOffset = 0;
            ArrayList<String> cards = currentGame.getPlayer(i).getCards();
            boolean first = true;
            for (String c : cards) {
                System.out.println("CARD: " + i + "file:images/cards/" + c + ".jpg");
                ImageView cardImage = new ImageView(new Image("file:images/cards/" + c + ".jpg", 295, 419, true, true));
                cardImage.setUserData(c);

                cardImage.setOpacity(0);
                if (!first) // bottom most card doesn't have shadow
                    cardImage.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 10, 0, 0, 0);");

                cardToolbar[i].getChildren().add(cardImage);

                FadeTransition displayImage = new FadeTransition(Duration.millis(100), cardImage);
                displayImage.setFromValue(0);
                displayImage.setToValue(1);
                displayImage.setCycleCount(1);
                animations.add(displayImage);

                Path path = new Path();
                path.getElements().add(new MoveTo(600,400));
                //path.getElements().add (new LineTo(155,260 + YOffset));
                path.getElements().add(new QuadCurveTo(300, 800, 155, 260 + YOffset));
                PathTransition cardDeal = new PathTransition();
                cardDeal.setDuration(Duration.millis(500));
                cardDeal.setPath(path);
                cardDeal.setNode(cardImage);
                cardDeal.setCycleCount(1);

                if (newGame)
                    animations.add(cardDeal);
                else {
                    System.out.println("Loading cards of Player " + i);
                    cardImage.setTranslateX(10);
                    cardImage.setTranslateY(75 + YOffset);
                }

                final int playerNumber = i;
                if (first && newGame) {
                    cardDeal.setOnFinished(event -> ui.getEventHandler().placeFlag(playerNumber, true));
                    first = false;
                }

                if (!newGame && first) {
                    ui.getEventHandler().placeFlag(playerNumber, false);
                    first = false;
                }

                YOffset += 80;
                if (YOffset/80 == JTEGameStateManager.CARDS && i != currentGame.getPlayers().size() - 1) {
                    cardDeal.setOnFinished(event -> gsm.nextPlayer());
                }
            }
        }

        SequentialTransition sequence = new SequentialTransition();
        sequence.getChildren().addAll(animations);
        sequence.setCycleCount(1);
        sequence.play();
        sequence.setOnFinished(e -> ui.getEventHandler().startGame(newGame)); // cards done dealing, let's start the game!

    }

    public void initCardToolbar() {
        cardToolbar = new Pane[gsm.getData().getPlayers().size()];
        for (int i = 0; i < this.cardToolbar.length; i++) {
            cardToolbar[i] = new Pane();
            cardToolbar[i].setMinWidth(305);
            cardToolbar[i].getStyleClass().add("sidebar");

            Label playerName = new Label(gsm.getData().getPlayer(i).getName());
            playerName.getStyleClass().addAll("label-large", "player-name");

            cardToolbar[i].getChildren().add(playerName);
            //this.setLeft(cardToolbar[i]);
        }
    }

    public void changeSidebar() {
        this.setLeft(cardToolbar[gsm.getData().getCurrentNumber()]);
    }

    public void initPlayerSidebar() {
        playerSidebar = new VBox();
        playerSidebar.setMinWidth(305);
        playerSidebar.setPadding(new Insets(15));
        playerSidebar.setSpacing(40);
        playerSidebar.getStyleClass().add("sidebar");
        this.setRight(playerSidebar);

//        Label currentCity = new Label("No city clicked on yet.");
//        Label cityDetails = new Label("No city selected, so no airport.");
//        cityDetails.setPrefWidth(250);
//        currentCity.setPrefWidth(250);
//        cityDetails.getStyleClass().add("label-med");
//        currentCity.getStyleClass().add("label-large");
//        cityDetails.setWrapText(true);
//        currentCity.setWrapText(true);

        rollDiceLabel = new Label(ROLL_DICE);
        rollDiceLabel.getStyleClass().add("label-large");
        rollDiceLabel.setWrapText(true);

        this.dice = new Dice();
        this.dice.setOnMouseClicked(event -> {
            if (gsm.isHuman())
                gsm.rollDie(dice);
        });

        portWait = new Button(PropertiesManager.getValue("WAIT"));
        portWait.getStyleClass().add("button-game");
        portWait.setTextFill(Color.WHITE);
        portWait.setOnAction(e -> ui.getEventHandler().respondToPortRequest());
        portWait.setDisable(true); // disable until needed

        takeFlight = new Button(PropertiesManager.getValue("FLIGHT"));
        takeFlight.getStyleClass().add("button-game");
        takeFlight.setTextFill(Color.WHITE);
        takeFlight.setOnAction(e -> ui.getEventHandler().respondToFlightRequest(ui));
        takeFlight.setDisable(true); // disable until needed

        Button about = new Button(PropertiesManager.getValue("ABOUT"));
        about.getStyleClass().add("button-normal");
        about.setOnAction(e -> ui.getEventHandler().respondToAboutRequest(ui.getPrimaryStage()));

        Button history = new Button(PropertiesManager.getValue("HISTORY"));
        history.getStyleClass().add("button-normal");
        history.setOnAction(e -> ui.getEventHandler().respondToHistoryRequest(ui.getPrimaryStage()));

        save = new Button(PropertiesManager.getValue("SAVE"));
        save.getStyleClass().add("button-normal");
        save.setOnAction(e -> ui.getEventHandler().respondToSaveRequest());

        Button info = new Button(PropertiesManager.getValue("INFO"));
        info.getStyleClass().add("button-normal");
        info.setOnAction(e -> ui.getEventHandler().respondToCityInfoRequest(ui.getPrimaryStage(), gsm.getCurrentPlayer().getCurrentCity()));

        Button quit = new Button(PropertiesManager.getValue("QUIT"));
        quit.getStyleClass().add("button-normal");
        quit.setOnAction(e -> ui.getEventHandler().respondToExitRequest(ui.getPrimaryStage()));

        playerSidebar.setAlignment(Pos.CENTER);
        playerSidebar.getChildren().addAll(rollDiceLabel, dice, portWait, takeFlight, about, history, save, info, quit);
    }

    public void initMap() {
        map = new Map(ui);
        this.setCenter(map);
    }

    public void displayCity(CityNode city) {
        stopCityAnimation();
//        Label currentCity = new Label(city.getName() + " at coordinates \n(" + city.getX() + ", " + city.getY() + ")");
//        Label cityDetails = new Label(city.getRegion() == 0 ? "City does not contain an airport." : "City contains an airport of region " + city.getRegion());


        ArrayList<CityNode> landNeighbors = city.getRoads();
        ArrayList<CityNode> seaNeighbors = city.getShips();

        if (seaNeighbors.isEmpty())
            portWait.setDisable(true);
        else
            portWait.setDisable(false);

        if (city.getRegion() != 0)
            takeFlight.setDisable(false);
        else
            takeFlight.setDisable(true);


        if (ui.getGsm().getData().getCurrent().isPortClear()) {
            ui.getGamePlayPane().getPortWaitButton().setText(PropertiesManager.getValue("SAIL"));
            portWait.setDisable(true);
        }

        for (CityNode c : landNeighbors) {

            c.setFill(Color.GOLDENROD);

            FadeTransition neighborFade = new FadeTransition(Duration.millis(500), c);
            neighborFade.setFromValue(1.0);
            neighborFade.setToValue(0.2);
            neighborFade.setCycleCount(Transition.INDEFINITE);
            neighborFade.setAutoReverse(true);
            neighborFade.play();

            neighborAnimation.add(neighborFade); // keep track of them so the animation can be stopped on click

            Line line = new Line(city.getX(), city.getY(), c.getX(), c.getY());
            line.setStrokeWidth(3);
            line.setStroke(Color.RED);
            map.getPane().getChildren().add(1, line);

            neighborLines.add(line);
        }
        for (CityNode c : seaNeighbors) {

            c.setFill(Color.GOLDENROD);

            FadeTransition neighborFade = new FadeTransition(Duration.millis(500), c);
            neighborFade.setFromValue(1.0);
            neighborFade.setToValue(0.2);
            neighborFade.setCycleCount(Transition.INDEFINITE);
            neighborFade.setAutoReverse(true);
            neighborFade.play();

            neighborAnimation.add(neighborFade); // keep track of them so the animation can be stopped on click

            Line line = new Line(city.getX(), city.getY(), c.getX(), c.getY());
            line.setStrokeWidth(3);
            line.setStroke(Color.RED);
            map.getPane().getChildren().add(1, line);

            neighborLines.add(line);
        }

//        Label neighborDetails = new Label(neighbors);
//        neighborDetails.setPrefWidth(250);
//        neighborDetails.setStyle("-fx-font-size: 1.0em");
//        neighborDetails.setTextFill(Color.WHITE);
//        neighborDetails.setWrapText(true);
//
//
//        cityDetails.setPrefWidth(250);
//        currentCity.setPrefWidth(250);
//        cityDetails.setStyle("-fx-font-size: 1.1em");
//        currentCity.setStyle("-fx-font-size: 2.0em");
//        cityDetails.setTextFill(Color.WHITE);
//        currentCity.setTextFill(Color.WHITE);
//        cityDetails.setWrapText(true);
//        currentCity.setWrapText(true);
//        playerSidebar.getChildren().remove(0); // remove existing label
//        playerSidebar.getChildren().remove(0);
//        playerSidebar.getChildren().remove(0);
//        playerSidebar.getChildren().add(0, neighborDetails);
//        playerSidebar.getChildren().add(0, cityDetails);
//        playerSidebar.getChildren().add(0, currentCity);


    }

    public void stopCityAnimation() {
        for (FadeTransition fade : neighborAnimation) {
            fade.stop();
            ((CityNode)(fade.getNode())).resetColor();
        }
        neighborAnimation.clear();

        for (Line line : neighborLines) {
            map.getPane().getChildren().remove(line);
        }
        neighborLines.clear();
    }

    public Button getPortWaitButton() {
        return portWait;
    }

    public void placeFlags(int player, boolean newGame) {
        map.placeFlags(player, newGame);
    }

    public Timeline focusPlayer(Player current) {
        if (!currentGame.getCurrent().isHuman()) {
            save.setDisable(true);
        } else {
            save.setDisable(false);
        }

        return map.focusPlayer(current);
    }

    public PathTransition movePlayer(Player current, CityNode city) {
        return map.movePlayer(current, city);
    }

    public void setDiceLabel(int moves) {
        if (moves < -1)
            rollDiceLabel.setText(gsm.getData().getCurrent().getName() + " " + ROLL_DICE + PropertiesManager.getValue("AGAIN"));
        else if (moves < 0)
            rollDiceLabel.setText(gsm.getData().getCurrent().getName() + " " + ROLL_DICE);
        else
            rollDiceLabel.setText(gsm.getData().getCurrent().getName() + ":\n" + moves + PropertiesManager.getValue("MOVESLEFTSELECT"));
    }

    public void removeCard(CityNode city) {
        for (Node node : cardToolbar[gsm.getData().getCurrentNumber()].getChildren()) {
            Object data = node.getUserData();
            if (data != null) {
                if (data instanceof String) {
                    if (data.equals(city.getName())) {

                        Path path = new Path();
                        path.getElements().add(new MoveTo(node.getTranslateX(), node.getTranslateY()));
                        //path.getElements().add (new LineTo(155,260 + YOffset));
                        path.getElements().add(new QuadCurveTo(300, 800, 600, 400));
                        PathTransition cardDeal = new PathTransition();
                        cardDeal.setDuration(Duration.millis(2000));
                        cardDeal.setPath(path);
                        cardDeal.setNode(node);
                        cardDeal.setCycleCount(1);
                        cardDeal.play();

                        cardDeal.setOnFinished(event -> {
                            cardToolbar[gsm.getData().getCurrentNumber()].getChildren().remove(node);
                            if (cardToolbar[gsm.getData().getCurrentNumber()].getChildren().size() == 1) {
                                // just the label left
                                ui.getEventHandler().respondToPlayerWin(gsm.getData().getCurrent());
                            } else {
                                if (gsm.getData().getCurrent().getsRepeat()) {
                                    if (!city.getShips().isEmpty()) // if has port, then player has waited this turn
                                        gsm.waitAtPort(true);
                                    gsm.setLastCity(null);
                                    gsm.repeatPlayer();
                                } else
                                    ui.getGsm().nextPlayer();
                            }
                        });

                        return;
                    }
                }
            }
        }
    }

    public void initFlightPlan() {
        flightPlan = new FlightPlan(ui);
    }

    public void switchToFlight(boolean flight) {
        if(flight) {
            flightPlan.placePlayer(
              gsm.getInfo().getFlightCities().get(gsm.getCurrentPlayer().getCurrentCity())
            );
            takeFlight.setText(PropertiesManager.getValue("CANCEL_FLIGHT"));
            this.setCenter(flightPlan);
        } else {
            takeFlight.setText(PropertiesManager.getValue("FLIGHT"));
            this.setCenter(map);
        }
        this.flight = flight;
    }

    public boolean isFlight() {
        return flight;
    }

    public Button getTakeFlight() {
        return takeFlight;
    }

    public Dice getDie() {
        return dice;
    }

    public Map getMap() {
        return map;
    }
}
