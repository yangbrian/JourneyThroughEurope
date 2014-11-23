package jte.ui.components;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * @author
 */
public class Map extends ImageView {

    Rectangle2D viewport;

    double x;
    double y;
    double clickX;
    double clickY;

    public Map() {
        super("file:images/fullmap.jpg");
        this.x = 0;
        this.y = 0;

        viewport = new Rectangle2D(x, y, 670, 860);
        this.setViewport(viewport);

        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                clickX = event.getX();
                clickY = event.getY();
            }
        });

        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double offsetX = event.getX() - clickX;
                double offsetY = event.getY() - clickY;

                clickX = event.getX();
                clickY = event.getY();

                x -= offsetX;
                y -= offsetY;

                if (x < 0) x = 0;
                if (y < 0) y = 0;
                if (x > 618) x = 618;
                if (y > 837) y = 837;

                setViewport(new Rectangle2D(x, y, 670, 860));
            }
        });
    }
}
