package jte.game.components;

import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * @author
 */
public class Player extends ImageView {

    private String name;
    private boolean human;
    private ArrayList<String> cards;
    private CityNode currentCity;

    public Player(String name, boolean human, int number) {
        super(new Image("file:images/piece_" + (number + 1) + ".png"));

        this.name = name;
        this.human = human;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void robotize(boolean computer) {
        human = !computer;
    }

    public boolean isHuman() {
        return human;
    }

    public void setCards(ArrayList<String> cards) {

        this.cards = cards;
    }

    public ArrayList<String> getCards() {
        return cards;
    }

    public String getHome() {
        return cards.get(0);
    }

    public void move(int x, int y) {
        Path path = new Path();

        // I really don't know why the +100s are needed...
        path.getElements().add(new MoveTo(getTranslateX() + 100, getTranslateY() + 100));
        path.getElements().add (new LineTo(x + 100, y + 100));


        PathTransition move = new PathTransition();
        move.setDuration(Duration.millis(500));
        move.setPath(path);
        move.setNode(this);
        move.setCycleCount(1);
        move.play();
    }
}
