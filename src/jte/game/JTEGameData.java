package jte.game;

import jte.game.components.Player;

import java.util.ArrayList;
import java.util.Collections;

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
        Collections.addAll(playerCards, cards);
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

    /**
     * Generate string used for the save file
     * @return String data representation of the game
     */
    public String getSaveData() {
        StringBuilder data = new StringBuilder();

        data.append(players.size()).append("\n");
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).isHuman())
                data.append(i).append(" ");
        }
        data.append("\n");

        // player names
        for (int i = 0; i < players.size(); i++) {
            if (i == players.size() - 1)
                data.append(players.get(i).getName());
            else
                data.append(players.get(i).getName()).append(",");
        }
        data.append("\n");

        // current player
        data.append(current).append("\n");

        // current cities
        for (Player player : players) {
            data.append(player.getCurrentCity()).append(" ");
        }
        data.append("\n");

        // player cards
        for (Player player : players) {
            for (String card : player.getCards()) {
                data.append(card).append(" ");
            }
            data.append("\n");
        }

        return data.toString();
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
