package jte.game;

import javafx.animation.PathTransition;
import jte.files.JTEFileLoader;
import jte.game.components.CityNode;
import jte.game.components.Player;
import jte.ui.JTEGameSetupUI;
import jte.ui.JTEUI;

import java.util.ArrayList;

/**
 * @author Brian Yang
 */
public class JTEGameStateManager {



    public enum JTEGameState {
        SPLASH_SCREEN, PLAYER_SELECT, CARD_DEALING, GAME_IN_PROGRESS, GAME_OVER
    }

    private JTEGameData currentGame;
    private JTEGameInfo info;
    private JTEGameState gameState;
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

    public void setGameState(JTEGameState gameState) {
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
            String[] cards = info.drawCards(CARDS, currentGame.getCurrentNumber());
            currentGame.drawCards(cards);
            currentGame.nextPlayer();
        }
    }

    public void nextPlayer() {
        currentGame.nextPlayer();
        ui.getGamePlayPane().changeSidebar();
        if (gameState == JTEGameState.GAME_IN_PROGRESS) {
            ui.getGamePlayPane().focusPlayer(currentGame.getCurrent());
            ui.getGamePlayPane().displayCity(info.getCities().get(currentGame.getCurrent().getCurrentCity()));
        }
    }

    public void startGame() {
        gameState = JTEGameState.GAME_IN_PROGRESS;
        nextPlayer();
    }


    public PathTransition movePlayer(CityNode city) {
        currentGame.getCurrent().setCurrentCity(city.getName());
        return ui.getGamePlayPane().movePlayer(currentGame.getCurrent(), city);
    }

    public boolean hasMovesLeft() {
        return currentGame.hasMovesLeft();
    }

}
