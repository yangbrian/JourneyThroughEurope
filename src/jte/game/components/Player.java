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
    private String currentCity;
    private int moves;

    private boolean portClear;

    private boolean repeat;

    private boolean flight;

    public double getOriginalX() {
        return originalX;
    }

    public void setOriginal(double originalX, double originalY) {
        this.originalX = originalX;
        this.originalY = originalY;
    }

    public double getOriginalY() {
        return originalY;
    }

    private double originalX;
    private double originalY;

    public Player(String name, boolean human, int number) {
        super(new Image("file:images/piece_" + (number + 1) + ".png"));

        this.name = name;
        this.human = human;

        this.portClear = false;

        this.repeat = false;
        this.flight = false;
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

    public PathTransition move(int x, int y) {
        Path path = new Path();

        path.getElements().add(new MoveTo(getTranslateX() + 100, getTranslateY() + 100));
        path.getElements().add (new LineTo(x + 100, y + 100));


        PathTransition move = new PathTransition();
        move.setDuration(Duration.millis(500));
        move.setPath(path);
        move.setNode(this);
        move.setCycleCount(1);
        move.play();

        if (!flight)
            moves--;
        flight = false;
        return move;
    }

    public void takeFlight(int moves) {
        this.moves -= moves;
        flight = true;
    }

    public void setCurrentCity(String city) {
        this.currentCity = city;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public void setPortClear(boolean clear) {
        this.portClear = clear;
    }

    public boolean isPortClear() {
        return portClear;
    }

    public void removeCard(String name) {
        cards.remove(name);
    }

    public boolean getsRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
}
