package jte.game;

import jte.game.components.Player;

import java.util.ArrayList;

/**
 * @author
 */
public class JTEGameData {
    private ArrayList<Player> players;
    private int current;

    public JTEGameData(ArrayList<Player> players) {
        this.players = players;
        this.current = 0;
    }

    public int getCurrentNumber() {
        return current;
    }

    public Player getCurrent() {
        return players.get(current);
    }

    public void nextPlayer() {
        current++;
        current %= players.size();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
