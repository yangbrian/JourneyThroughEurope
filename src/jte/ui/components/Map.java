package jte.ui.components;

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import jte.game.components.CityNode;
import jte.game.components.Player;
import jte.ui.JTEUI;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author
 */
public class Map extends ScrollPane {

    private JTEUI ui;
    private Pane mapPane;
    private ImageView map;

    private double clickX;
    private double clickY;

    public Map(JTEUI ui) {
        this.ui = ui;

        mapPane = new Pane();
        map = new ImageView(new Image("file:images/fullmap.jpg"));
        mapPane.getChildren().add(map);

        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setPannable(true);
        this.setPrefSize(670, 860);

        drawCities();

        this.setContent(mapPane);
    }

    public void drawCities() {
        HashMap<String, CityNode> cities = ui.getGsm().getInfo().getCities();
        for (CityNode city : cities.values()) {
            mapPane.getChildren().add(city);
            System.out.println("City draw: " + city.getName());
            city.setOnMouseClicked(e -> ui.getEventHandler().respondToCityClick(city));

            // change cursor when hovering over city
            city.setOnMouseEntered(event -> ui.getEventHandler().hoverOverCity(true));
            city.setOnMouseExited(event -> ui.getEventHandler().hoverOverCity(false));
        }
    }

    public void placeFlags(int playerNumber) {
        Player player = ui.getGsm().getData().getPlayer(playerNumber);
        CityNode home = ui.getGsm().getInfo().getCities().get(player.getHome());

        // set starting city
        player.setCurrentCity(home.getName());

        ImageView flag = new ImageView(new Image("file:images/flag_" + (playerNumber+1) + ".png"));
        flag.setX(home.getX() - 95);
        flag.setY(home.getY() - 130);

        // scroll to the home cities as the card is shown
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
          new KeyValue(this.hvalueProperty(), (flag.getX() - 225)/670)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
          new KeyValue(this.vvalueProperty(), (flag.getY() - 400)/860)));
        timeline.play();

        // drop the flag onto the city
        ScaleTransition dropFlag = new ScaleTransition(Duration.millis(1000), flag);
        dropFlag.setFromX(1.0);
        dropFlag.setFromY(1.0);
        dropFlag.setToX(0.4);
        dropFlag.setToY(0.4);
        dropFlag.setCycleCount(1);

        player.setX(0);
        player.setY(0);
        player.setTranslateX(home.getX() - 100);
        player.setTranslateY(home.getY() - 125);

        if (player.getTranslateX() < 0)
            player.setTranslateX(1);
        if (player.getTranslateY() < 0)
            player.setTranslateY(1);

        //player.relocate(home.getX() - 100, home.getY() - 125);
        //player.setTranslateX(getLayoutX());
        //player.setTranslateY(getLayoutY());

        final double originalX = player.getTranslateX();
        final double originalY = player.getTranslateY();

        player.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setPannable(false); // disable panning while dragging
                clickX = event.getSceneX() + 305 /* card toolbar */ + getHvalue();
                clickY = event.getSceneY() + getVvalue();
            }
        });

        player.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(ui.getEventHandler().respondToPlayerDrag(player) && ui.getGsm().rolled()) { // only drag if is current player
                    double offsetX = event.getSceneX() + 305 /* card toolbar */ + getHvalue() - clickX;
                    double offsetY = event.getSceneY() + getVvalue() - clickY;

                    clickX = event.getSceneX() + 305 + getHvalue();
                    clickY = event.getSceneY() + getVvalue();

                    player.setTranslateX(player.getTranslateX() + offsetX);
                    player.setTranslateY(player.getTranslateY() + offsetY);
                }
            }
        });

        player.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setPannable(true); // re-enable panning
                CityNode city;
                if ((city = ui.getEventHandler().playerDrop(player, player.getTranslateX(), player.getTranslateY())) != null) {
                    ui.getEventHandler().respondToCityClick(city);
                    System.out.println("DRAG GOOD");
                } else {
                    System.out.println("DRAG BAD");
                    player.setTranslateX(originalX);
                    player.setTranslateY(originalY);
                }
            }
        });


        ScaleTransition dropPlayer = new ScaleTransition(Duration.millis(1000), player);
        dropPlayer.setFromX(0.9);
        dropPlayer.setFromY(0.9);
        dropPlayer.setToX(0.5);
        dropPlayer.setToY(0.5);
        dropPlayer.setCycleCount(1);

        // index 1 to go in front of the map (0) but beneath the city nodes so they remain clickable
        mapPane.getChildren().add(1, flag);

        mapPane.getChildren().add(player);

        dropFlag.play(); // play the flag drop
        dropPlayer.play();
    }

    public void focusPlayer(Player current) {
        // scroll to the home cities as the card is shown
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
          new KeyValue(this.hvalueProperty(), (current.getTranslateX() - 225) / 670)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
          new KeyValue(this.vvalueProperty(), (current.getTranslateY() - 400)/860)));
        timeline.play();
    }

    public PathTransition movePlayer(Player current, CityNode city) {
        return current.move(city.getX() - 100, city.getY() - 125);
    }
}
