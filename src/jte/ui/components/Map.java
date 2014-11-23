package jte.ui.components;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import jte.game.components.CityNode;
import jte.ui.JTEUI;

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
        }
    }
}
