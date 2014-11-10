package jte.game;

import jte.game.components.Card;
import jte.game.components.CityNode;

import java.util.HashMap;

/**
 * @author
 */
public class JTEGameInfo {
    private HashMap<String, CityNode> cities;
    private Card[] yellowCards;
    private Card[] greenCards;
    private Card[] redCards;


    public JTEGameInfo(HashMap<String, CityNode> cities) {
        this.cities = cities;
    }

    public HashMap<String, CityNode> getCities() {
        return cities;
    }
}
