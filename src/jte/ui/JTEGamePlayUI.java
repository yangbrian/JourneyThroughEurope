package jte.ui;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import jte.game.JTEGameInfo;
import jte.game.components.CityNode;

import java.util.HashMap;

/**
 * @author Brian Yang
 */
public class JTEGamePlayUI extends BorderPane {

    VBox cardToolbar;
    VBox playerSidebar;
    StackPane map;
    Pane map1;
    Pane map2;
    Pane map3;
    Pane map4;

    private JTEGameInfo info;

    public JTEGamePlayUI(JTEGameInfo info) {
        initCardToolbar();
        initPlayerSidebar();
        this.info = info;
        initMap();

    }

    public void initCardToolbar() {
        cardToolbar = new VBox();
    }

    public void initPlayerSidebar() {

    }

    public void initMap() {
        map = new StackPane();
        map.setPrefSize(1000, 1000);
        map1 = new Pane();
        map2 = new Pane();
        map3 = new Pane();
        map4 = new Pane();
        map.getChildren().addAll(map1, map2, map3, map4);
        map1.toFront();

        HashMap<String, CityNode> cities = info.getCities();
        for (CityNode city : cities.values()) {
            int quarter = city.getQuarter();
            switch (quarter) {
                case 1:
                    map1.getChildren().add(city);
                    break;
                case 2:
                    map2.getChildren().add(city);
                    break;
                case 3:
                    map3.getChildren().add(city);
                    break;
                case 4:
                    map4.getChildren().add(city);
                    break;
                default:
                    System.out.println("Something went wrong with the city data...");
            }
            city.relocate(city.getX(), city.getY());
        }

        this.setCenter(map);
    }

    public void changePartition(int quarter) {

    }
}
