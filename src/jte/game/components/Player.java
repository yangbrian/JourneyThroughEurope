package jte.game.components;

import java.util.ArrayList;

/**
 * @author
 */
public class Player {

    private String name;
    private boolean human;
    private ArrayList<String> cards;

    public Player(String name, boolean human) {
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
}
