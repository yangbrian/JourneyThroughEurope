package jte.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        this.setWidth(1280);
        initMap();

    }

    public void initCardToolbar() {
        cardToolbar = new VBox();
        cardToolbar.setMinWidth(300);
        cardToolbar.setSpacing(10);
        cardToolbar.setStyle("-fx-background-color:gray");
        this.setLeft(cardToolbar);
    }

    public void initPlayerSidebar() {
        playerSidebar = new VBox();
        playerSidebar.setMinWidth(300);
        playerSidebar.setStyle("-fx-background-color:gray");
        this.setRight(playerSidebar);
    }

    public void initMap() {
        map = new StackPane();
        map.setAlignment(Pos.CENTER_LEFT);
        map.setPrefWidth(670);
        map1 = new Pane();
        map1.setPrefWidth(670);
        map2 = new Pane();
        map3 = new Pane();
        map4 = new Pane();

        ImageView map1Img = new ImageView(new Image("file:images/map1.jpg"));
        ImageView map2Img = new ImageView(new Image("file:images/map2.jpg"));
        ImageView map3Img = new ImageView(new Image("file:images/map3.jpg"));
        ImageView map4Img = new ImageView(new Image("file:images/map4.jpg"));

        map.getChildren().add(map1Img);
        //map2.getChildren().add(map2Img);
        //map3.getChildren().add(map3Img);
        //map4.getChildren().add(map4Img);

        map.getChildren().addAll(map1);
        map1Img.toFront();
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
            city.relocate(city.getX() - 5, city.getY());
        }

        this.setCenter(map);
    }

    public void changePartition(int quarter) {

    }
}
