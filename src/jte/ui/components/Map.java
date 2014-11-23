package jte.ui.components;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * @author
 */
public class Map extends ScrollPane {

    StackPane mapPane;
    ImageView map;

    public Map() {
        mapPane = new StackPane();
        map = new ImageView(new Image("file:images/fullmap.jpg"));
        mapPane.getChildren().add(map);

        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setPannable(true);
        this.setPrefSize(670, 860);

        this.setContent(mapPane);
    }
}
