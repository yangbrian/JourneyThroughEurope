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
public class FlightPlan extends ScrollPane {

    private JTEUI ui;
    private Pane mapPane;
    private ImageView map;

    private double clickX;
    private double clickY;

    private double originalX;
    private double originalY;

    public FlightPlan(JTEUI ui) {
        this.ui = ui;

        mapPane = new Pane();
        map = new ImageView(new Image("file:images/flightplan.jpg"));
        mapPane.getChildren().add(map);

        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setPannable(true);
        this.setPrefSize(670, 860);

        drawCities();

        this.setContent(mapPane);
    }

    public void drawCities() {
        HashMap<String, CityNode> cities = ui.getGsm().getInfo().getFlightCities();
        for (CityNode city : cities.values()) {
            mapPane.getChildren().add(city);
            System.out.println("City draw: " + city.getName());
            city.setOnMouseClicked(e -> ui.getEventHandler().respondToFlightCityClick(city));

            // change cursor when hovering over city
            city.setOnMouseEntered(event -> ui.getEventHandler().hoverOverCity(true));
            city.setOnMouseExited(event -> ui.getEventHandler().hoverOverCity(false));
        }
    }
}
