package jte.ui.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

        player.relocate(home.getX() - 100, home.getY() - 125);

        ScaleTransition dropPlayer = new ScaleTransition(Duration.millis(1000), player);
        dropPlayer.setFromX(0.9);
        dropPlayer.setFromY(0.9);
        dropPlayer.setToX(0.4);
        dropPlayer.setToY(0.4);
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
          new KeyValue(this.hvalueProperty(), (current.getX() - 225)/670)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
          new KeyValue(this.vvalueProperty(), (current.getY() - 400)/860)));
        timeline.play();
    }
}
