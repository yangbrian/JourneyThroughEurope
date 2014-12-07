package jte.game;

import jte.game.components.CityNode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author
 */
public class JTEGameInfo {
    private HashMap<String, CityNode> cities;
    private HashMap<String, CityNode> flightPlan;

    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int YELLOW = 2;

    private ArrayList<String> yellowCards;
    private ArrayList<String> greenCards;
    private ArrayList<String> redCards;


    public JTEGameInfo(HashMap<String, CityNode> cities, HashMap<String, CityNode> flightPlan) {
        this.cities = cities;
        this.flightPlan = flightPlan;
        this.yellowCards = new ArrayList<>();
        this.greenCards = new ArrayList<>();
        this.redCards = new ArrayList<>();

        for (CityNode c : cities.values()) {
            switch (c.getColor()) {
                case "RED":
                    redCards.add(c.getName());
                    break;
                case "YELLOW":
                    yellowCards.add(c.getName());
                    break;
                case "GREEN":
                    greenCards.add(c.getName());
                    break;
                default:
                    System.out.println("Invalid color!");
            }
        }
    }

    public HashMap<String, CityNode> getCities() {
        return cities;
    }

    public String[] dealCards(int count, int start) {
        int color = start % 3;
        String[] cards = new String[count];

        // draw cards, changing colors each time
        for (int i = 0; i < cards.length; i++) {
            color %= 3;
            switch(color) {
                case RED:
                    int card = (int)(Math.random() * redCards.size());
                    cards[i] = redCards.get(card); // draw card
                    redCards.remove(card); // remove from deck
                    break;
                case GREEN:
                    card = (int)(Math.random() * greenCards.size());
                    cards[i] = greenCards.get(card); // draw card
                    greenCards.remove(card); // remove from deck
                    break;
                case YELLOW:
                    card = (int)(Math.random() * yellowCards.size());
                    cards[i] = yellowCards.get(card); // draw card
                    yellowCards.remove(card); // remove from deck
                    break;
                default:
                    System.out.println("Card " + i + ": Invalid card color");
            }
            color++;
        }
        return cards;

    }

    public HashMap<String, CityNode> getFlightCities() {
        return flightPlan;
    }
}
