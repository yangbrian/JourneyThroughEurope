package jte.ui.components;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import jte.game.components.CityNode;
import jte.ui.JTEUI;

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
            System.out.println("Flight City draw: " + city.getName());
            city.setOnMouseClicked(e -> ui.getEventHandler().respondToFlightCityClick(city));
        }
    }
}
