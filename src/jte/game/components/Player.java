package jte.game.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

/**
 * @author
 */
public class Player extends ImageView {

    private String name;
    private boolean human;
    private ArrayList<String> cards;

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

    @Override
    public void relocate(double x, double y) {
        super.relocate(x, y);
    }
}
