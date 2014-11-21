package jte.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import jte.game.JTEGameInfo;
import jte.game.components.CityNode;

import java.util.ArrayList;
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
    JTEUI ui;

    ArrayList<FadeTransition> neighborAnimation;

    private JTEGameInfo info;

    public JTEGamePlayUI(JTEUI ui, JTEGameInfo info) {
        initCardToolbar();
        initPlayerSidebar();
        this.info = info;
        this.ui = ui;
        this.setWidth(1280);
        initMap();

        neighborAnimation = new ArrayList<>();
    }

    public void initCardToolbar() {
        cardToolbar = new VBox();
        cardToolbar.setMinWidth(305);
        cardToolbar.setSpacing(10);
        cardToolbar.setPadding(new Insets(15));
        cardToolbar.setStyle("-fx-background-color:#81b5dd");
        this.setLeft(cardToolbar);
    }

    public void initPlayerSidebar() {
        playerSidebar = new VBox();
        playerSidebar.setMinWidth(305);
        playerSidebar.setPadding(new Insets(15));
        playerSidebar.setSpacing(50);
        playerSidebar.setStyle("-fx-background-color:#81b5dd");
        this.setRight(playerSidebar);

        Label currentCity = new Label("No city clicked on yet.");
        Label cityDetails = new Label("No city selected, so no airport.");
        cityDetails.setPrefWidth(250);
        currentCity.setPrefWidth(250);
        cityDetails.setStyle("-fx-font-size: 1.1em");
        currentCity.setStyle("-fx-font-size: 2.0em");
        cityDetails.setTextFill(Color.WHITE);
        currentCity.setTextFill(Color.WHITE);
        cityDetails.setWrapText(true);
        currentCity.setWrapText(true);


        Button about = new Button("About JTE");
        about.setStyle("-fx-font-size: 1.9em; -fx-background-color:#ecc323;-fx-background-radius: 30;");
        about.setOnAction(e -> {
            ui.getEventHandler().respondToAboutRequest(ui.getPrimaryStage());
        });

        Button history = new Button("Game History");
        history.setStyle("-fx-font-size: 1.9em; -fx-background-color:#ecc323;-fx-background-radius: 30;");
        history.setOnAction(e -> {
            ui.getEventHandler().respondToHistoryRequest(ui.getPrimaryStage());
        });

        Button quit = new Button("Quit");
        quit.setStyle("-fx-font-size: 1.9em; -fx-background-color:#ecc323;-fx-background-radius: 30;");
        quit.setOnAction(e -> {
            ui.getEventHandler().respondToExitRequest(ui.getPrimaryStage());
        });

        playerSidebar.getChildren().addAll(currentCity, cityDetails, about, history, quit);
    }

    public void initMap() {
        map = new StackPane();
        map.setAlignment(Pos.CENTER_LEFT);
        map.setMaxWidth(670);
        map1 = new Pane();
        //map1.setPrefWidth(670);
        map2 = new Pane();
        //map2.setPrefWidth(670);
        map3 = new Pane();
        //map3.setPrefWidth(670);
        map4 = new Pane();
        //map4.setPrefWidth(670);

        ImageView map1Img = new ImageView(new Image("file:images/map1.jpg"));
        ImageView map2Img = new ImageView(new Image("file:images/map2.jpg"));
        ImageView map3Img = new ImageView(new Image("file:images/map3.jpg"));
        ImageView map4Img = new ImageView(new Image("file:images/map4.jpg"));

        map.getChildren().add(map1Img);
        map2.getChildren().add(map2Img);
        map3.getChildren().add(map3Img);
        map4.getChildren().add(map4Img);

        map.getChildren().addAll(map1, map2, map3, map4);
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
            //city.relocate(city.getX() - 5, city.getY() - 3);
            city.setOnMouseClicked(e -> {
                ui.getEventHandler().respondToCityClick(city);
            });
        }

        this.setCenter(map);
    }

    public void changePartition(int quarter) {

    }

    public void displayCity(CityNode city) {
        stopCityAnimation();
        Label currentCity = new Label(city.getName() + " at coordinates \n(" + city.getX() + ", " + city.getY() + ")");
        Label cityDetails = new Label(city.getRegion() == 0 ? "City does not contain an airport." : "City contains an airport of region " + city.getRegion());

        String neighbors = "";
        ArrayList<CityNode> landNeighbors = city.getRoads();
        ArrayList<CityNode> seaNeighbors = city.getShips();

        for (CityNode c : landNeighbors) {
            neighbors += "Land: " + c.getName() + "\n";

            c.setFill(Color.GOLDENROD);

            FadeTransition neighborFade = new FadeTransition(Duration.millis(500), c);
            neighborFade.setFromValue(1.0);
            neighborFade.setToValue(0.1);
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
            neighborFade.setToValue(0.1);
            neighborFade.setCycleCount(Transition.INDEFINITE);
            neighborFade.setAutoReverse(true);
            neighborFade.play();

            neighborAnimation.add(neighborFade); // keep track of them so the animation can be stopped on click
        }

        Label neighborDetails = new Label(neighbors);
        neighborDetails.setPrefWidth(250);
        neighborDetails.setStyle("-fx-font-size: 1.0em");
        neighborDetails.setTextFill(Color.WHITE);
        neighborDetails.setWrapText(true);


        cityDetails.setPrefWidth(250);
        currentCity.setPrefWidth(250);
        cityDetails.setStyle("-fx-font-size: 1.1em");
        currentCity.setStyle("-fx-font-size: 2.0em");
        cityDetails.setTextFill(Color.WHITE);
        currentCity.setTextFill(Color.WHITE);
        cityDetails.setWrapText(true);
        currentCity.setWrapText(true);
        playerSidebar.getChildren().remove(0); // remove existing label
        playerSidebar.getChildren().remove(0);
        playerSidebar.getChildren().remove(0);
        playerSidebar.getChildren().add(0, neighborDetails);
        playerSidebar.getChildren().add(0, cityDetails);
        playerSidebar.getChildren().add(0, currentCity);


    }

    public void stopCityAnimation() {
        for (FadeTransition fade : neighborAnimation) {
            fade.stop();
            ((CityNode)(fade.getNode())).resetColor();
        }
        neighborAnimation.clear();
    }
}
