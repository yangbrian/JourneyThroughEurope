package jte.ui.components;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * @author
 */
public class Dice extends StackPane {

    ImageView[] faces;
    public Dice() {
        faces = new ImageView[6];
        for (int i = 0; i < faces.length; i++) {
            faces[i] = new ImageView(new Image("file:images/die_" + (i + 1) + ".jpg"));
        }
        this.getChildren().addAll(faces);

        this.setHeight(120);
        this.setPadding(new Insets(5));
    }

    public int roll() {
        int roll = (int)(Math.random() * 6);
        faces[roll].toFront();
        return roll + 1;
    }
}
