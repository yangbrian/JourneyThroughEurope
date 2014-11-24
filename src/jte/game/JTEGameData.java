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

    public void drawCards(String[] cards) {
        ArrayList<String> playerCards = new ArrayList<>();
        for (String c : cards)
            playerCards.add(c);
        players.get(current).setCards(playerCards);
    }

    public ArrayList<String> getCards() {
        return players.get(current).getCards();
    }

    public Player getPlayer(int player) {
        return players.get(player);
    }

    public boolean hasMovesLeft() {
        return getCurrent().getMoves() > 0;
    }

    public int getMovesLeft() {
        return getCurrent().getMoves();
    }
}
