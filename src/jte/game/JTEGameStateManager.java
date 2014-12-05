package jte.game;

import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import jte.files.JTEFileLoader;
import jte.game.components.CityNode;
import jte.game.components.Player;
import jte.ui.JTEGameSetupUI;
import jte.ui.JTEUI;
import jte.ui.components.Dice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

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
    private boolean diceRoll;
    private boolean portWait;

    private CityNode lastCity;

    private LinkedList<String> history;

    /** number of cards to draw */
    public static final int CARDS = 2;

    public JTEGameStateManager(JTEUI ui) {
        this.ui = ui;
        fileHandler = new JTEFileLoader(ui);
        history = new LinkedList<>();
        loadGameInfo();
        diceRoll = false;
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
        diceRoll = false;
        ui.getGamePlayPane().getPortWaitButton().setDisable(true);
        currentGame.nextPlayer();
        ui.getGamePlayPane().changeSidebar();
        if (gameState == JTEGameState.GAME_IN_PROGRESS) {
            ui.getGamePlayPane().focusPlayer(currentGame.getCurrent());
            ui.getGamePlayPane().setDiceLabel(-1);
            // ui.getGamePlayPane().displayCity(info.getCities().get(currentGame.getCurrent().getCurrentCity()));

        }
    }

    public void repeatPlayer() {
        diceRoll = false;
        ui.getGamePlayPane().getPortWaitButton().setDisable(true);
        ui.getGamePlayPane().focusPlayer(currentGame.getCurrent());
        currentGame.getCurrent().setRepeat(false);
        ui.getGamePlayPane().setDiceLabel(-2);
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

    public int getMovesLeft() {
        return currentGame.getMovesLeft();
    }


    public void rollDie(Dice dice) {

        if (gameState == JTEGameState.GAME_IN_PROGRESS) {

            int roll = dice.roll();

            if (roll == 6)
                currentGame.getCurrent().setRepeat(true);

            currentGame.getCurrent().setMoves(roll);
            diceRoll = true;

            ui.getGamePlayPane().displayCity(info.getCities().get(currentGame.getCurrent().getCurrentCity()));
            ui.getGamePlayPane().setDiceLabel(roll);

            addToHistory(getCurrentPlayerName() + " rolls a " + roll + "\n");
        }
    }

    public boolean rolled() {
        return diceRoll;
    }



    public void removeCard(CityNode city) {

        currentGame.getCurrent().removeCard(city.getName());
        ui.getGamePlayPane().removeCard(city);
    }

    public void waitAtPort(boolean wait) {
        currentGame.getCurrent().setPortClear(wait);
    }

    public boolean waited() {
        return currentGame.getCurrent().isPortClear();
    }

    public CityNode getLastCity() {
        return lastCity;
    }

    public void setLastCity(CityNode city) {
        this.lastCity = city;
    }

    public void addToHistory(String move) {
        history.add(move);
    }

    public LinkedList<String> getHistory() {
        return history;
    }

    public Player getCurrentPlayer() {
        return currentGame.getCurrent();
    }

    public String getCurrentPlayerName() {
        return currentGame.getCurrent().getName();
    }

    public void saveGame() throws IOException {
        fileHandler.saveGame();
    }
}
