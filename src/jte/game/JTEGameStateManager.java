package jte.game;

import jte.files.JTEFileLoader;
import jte.game.components.Player;
import jte.ui.JTEGameSetupUI;
import jte.ui.JTEUI;

import java.util.ArrayList;

/**
 * @author Brian Yang
 */
public class JTEGameStateManager {

    private JTEGameData currentGame;
    private JTEGameInfo info;
    private JTEGameSetupUI.JTEGameState gameState;
    private JTEFileLoader fileHandler;
    private JTEUI ui;

    /** number of cards to draw */
    public static final int CARDS = 3;

    public JTEGameStateManager(JTEUI ui) {
        this.ui = ui;
        fileHandler = new JTEFileLoader(ui);
        loadGameInfo();
    }

    public void loadGameInfo() {
        info = new JTEGameInfo(fileHandler.loadCities());
    }

    public JTEGameInfo getInfo() {
        return info;
    }

    public void setGameState(JTEGameSetupUI.JTEGameState gameState) {
        this.gameState = gameState;
    }

    public void setGameData(ArrayList<Player> players) {
        this.currentGame = new JTEGameData(players);
    }

    public JTEGameData getData() {
        return currentGame;
    }

    public void drawCards() {

        for (int i = 0; i < currentGame.getPlayers().size(); i++) {
            System.out.println("PLAYER " + i + " DRAWING CARDS.");
            String[] cards = info.drawCards(CARDS, currentGame.getCurrentNumber());
            currentGame.drawCards(cards);
            currentGame.nextPlayer();
        }
    }
}
