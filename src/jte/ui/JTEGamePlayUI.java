package jte.ui;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import jte.game.JTEGameData;
import jte.game.JTEGameInfo;
import jte.game.JTEGameStateManager;
import jte.game.components.CityNode;
import jte.game.components.Player;
import jte.ui.components.Dice;
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
    public static final String ROLL_DICE = "Roll dice";

    private Button portWait;

    private JTEGameInfo info;
    private JTEGameStateManager gsm;
    private JTEGameData currentGame;

    public JTEGamePlayUI(JTEUI ui) {

        initPlayerSidebar();
        this.ui = ui;
        this.gsm = ui.getGsm();
        this.info = gsm.getInfo();

        this.setWidth(1280);
        initMap();

        neighborAnimation = new ArrayList<>();
    }

    public void drawCards() {
        initCardToolbar();
        this.setLeft(cardToolbar[0]);
        // draw cards
        gsm.drawCards();

        this.currentGame = gsm.getData();

        LinkedList<Transition> animations = new LinkedList<>();
        for (int i = 0; i < currentGame.getPlayers().size(); i++) {
            int YOffset = 0;
            ArrayList<String> cards = currentGame.getPlayer(i).getCards();
            boolean first = true;
            for (String c : cards) {
                System.out.println("CARD: " + i + "file:images/cards/" + c + ".jpg");
                ImageView cardImage;
                try {
                    cardImage = new ImageView(new Image("file:images/cards/" + c + ".jpg", 295, 419, true, true));
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

                    animations.add(cardDeal);

                    final int playerNumber = i;
                    if (first) {
                        cardDeal.setOnFinished(event -> ui.getEventHandler().placeFlag(playerNumber));
                        first = false;
                    }

                    YOffset += 80;
                    if (YOffset/80 == JTEGameStateManager.CARDS && i != currentGame.getPlayers().size() - 1) {
                        cardDeal.setOnFinished(event -> gsm.nextPlayer());
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("ERROR: IMAGE NOT FOUND! " + c);
                }
            }
        }
        SequentialTransition sequence = new SequentialTransition();
        sequence.getChildren().addAll(animations);
        sequence.setCycleCount(1);
        sequence.play();
        sequence.setOnFinished(e -> ui.getEventHandler().startGame()); // cards done dealing, let's start the game!

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
        playerSidebar.setSpacing(50);
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
        this.dice.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                gsm.rollDie(dice);
            }
        });

        portWait = new Button("Wait for Ship");
        portWait.getStyleClass().add("button-game");
        portWait.setTextFill(Color.WHITE);
        portWait.setOnAction(e -> ui.getEventHandler().respondToPortRequest());
        portWait.setDisable(true); // disable until needed

        Button about = new Button("About JTE");
        about.getStyleClass().add("button-normal");
        about.setOnAction(e -> ui.getEventHandler().respondToAboutRequest(ui.getPrimaryStage()));

        Button history = new Button("Game History");
        history.getStyleClass().add("button-normal");
        history.setOnAction(e -> ui.getEventHandler().respondToHistoryRequest(ui.getPrimaryStage()));

        Button quit = new Button("Quit");
        quit.getStyleClass().add("button-normal");
        quit.setOnAction(e -> ui.getEventHandler().respondToExitRequest(ui.getPrimaryStage()));

        playerSidebar.setAlignment(Pos.CENTER);
        playerSidebar.getChildren().addAll(rollDiceLabel, dice, portWait, about, history, quit);
    }

    public void initMap() {
        map = new Map(ui);
        this.setCenter(map);
    }

    public void displayCity(CityNode city) {
        System.out.println("Displaying: " + city.getName());
        stopCityAnimation();
//        Label currentCity = new Label(city.getName() + " at coordinates \n(" + city.getX() + ", " + city.getY() + ")");
//        Label cityDetails = new Label(city.getRegion() == 0 ? "City does not contain an airport." : "City contains an airport of region " + city.getRegion());

        String neighbors = "";
        ArrayList<CityNode> landNeighbors = city.getRoads();
        ArrayList<CityNode> seaNeighbors = city.getShips();

        if (seaNeighbors.isEmpty())
            portWait.setDisable(true);
        else
            portWait.setDisable(false);

        if (ui.getGsm().getData().getCurrent().isPortClear()) {
            ui.getGamePlayPane().getPortWaitButton().setText("Ready to Sail!");
            portWait.setDisable(true);
        }

        for (CityNode c : landNeighbors) {
            System.out.println("Displaying Land Neighbors: " + city.getName());
            neighbors += "Land: " + c.getName() + "\n";

            c.setFill(Color.GOLDENROD);

            FadeTransition neighborFade = new FadeTransition(Duration.millis(500), c);
            neighborFade.setFromValue(1.0);
            neighborFade.setToValue(0.2);
            neighborFade.setCycleCount(Transition.INDEFINITE);
            neighborFade.setAutoReverse(true);
            neighborFade.play();

            neighborAnimation.add(neighborFade); // keep track of them so the animation can be stopped on click
        }
        for (CityNode c : seaNeighbors) {
            neighbors += "Sea: " + c.getName() + "\n";

            c.setFill(Color.GOLDENROD);

            FadeTransition neighborFade = new FadeTransition(Duration.millis(500), c);
            neighborFade.setFromValue(1.0);
            neighborFade.setToValue(0.2);
            neighborFade.setCycleCount(Transition.INDEFINITE);
            neighborFade.setAutoReverse(true);
            neighborFade.play();

            neighborAnimation.add(neighborFade); // keep track of them so the animation can be stopped on click
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
    }

    public Button getPortWaitButton() {
        return portWait;
    }

    public void placeFlags(int player) {
        map.placeFlags(player);
    }

    public void focusPlayer(Player current) {
        map.focusPlayer(current);
    }

    public PathTransition movePlayer(Player current, CityNode city) {
        return map.movePlayer(current, city);
    }

    public void setDiceLabel(int moves) {
        if (moves < -1)
            rollDiceLabel.setText(gsm.getData().getCurrent().getName() + " " + ROLL_DICE + " again");
        else if (moves < 0)
            rollDiceLabel.setText(gsm.getData().getCurrent().getName() + " " + ROLL_DICE);
        else
            rollDiceLabel.setText(gsm.getData().getCurrent().getName() + ":\n" + moves + " moves left. Select a city.");
    }

    public void removeCard(CityNode city) {
        for (Node node : cardToolbar[gsm.getData().getCurrentNumber()].getChildren()) {
            Object data = node.getUserData();
            if (data != null) {
                if (data instanceof String) {
                    if (data.equals(city.getName())) {

                        Path path = new Path();
                        path.getElements().add(new MoveTo(node.getLayoutX(), node.getLayoutY()));
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

    public void setTranslate(double x, double y) {
        ui.getGsm().getData().getCurrent().setOriginal(x, y);
    }
}
