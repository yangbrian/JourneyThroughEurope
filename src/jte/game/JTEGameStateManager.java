package jte.game;

import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import jte.files.JTEFileLoader;
import jte.game.components.CityNode;
import jte.game.components.Edge;
import jte.game.components.Player;
import jte.ui.JTEUI;
import jte.ui.components.Dice;

import java.io.IOException;
import java.util.*;

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

    private static ArrayList<CityNode> vertices;

    private int roll;

    /** number of cards to draw */
    public static final int CARDS = 3;

    public JTEGameStateManager(JTEUI ui) {
        this.ui = ui;
        fileHandler = new JTEFileLoader(ui);
        fileHandler.loadDescriptions();
        history = new LinkedList<>();
        loadGameInfo();
        diceRoll = false;
        vertices = new ArrayList<>();
    }

    public void loadGameInfo() {
        HashMap<String, CityNode> cities = fileHandler.loadCities();
        info = new JTEGameInfo(cities, fileHandler.loadFlightPlan(cities));
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
            String[] cards = info.dealCards(CARDS, currentGame.getCurrentNumber());
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
            Timeline focus = ui.getGamePlayPane().focusPlayer(currentGame.getCurrent());
            focus.setOnFinished(e -> {
                ui.getGamePlayPane().setDiceLabel(-1);
                getCurrentPlayer().setPortClear(true); // port clear will always be true on the first turn
                ui.getGamePlayPane().getTakeFlight().setDisable(true);

                if (!isHuman())
                    ui.getEventHandler().startComputerTurn();

                // ui.getGamePlayPane().displayCity(info.getCities().get(currentGame.getCurrent().getCurrentCity()));
            });

        }
    }

    public void repeatPlayer() {
        diceRoll = false;
        ui.getGamePlayPane().getPortWaitButton().setDisable(true);
        ui.getGamePlayPane().focusPlayer(currentGame.getCurrent());
        currentGame.getCurrent().setRepeat(false);
        ui.getGamePlayPane().setDiceLabel(-2);
    }

    public void repeatComputer() {
        diceRoll = false;
        ui.getGamePlayPane().getPortWaitButton().setDisable(true);
        ui.getGamePlayPane().focusPlayer(currentGame.getCurrent());
        currentGame.getCurrent().setRepeat(false);
        ui.getGamePlayPane().setDiceLabel(-2);
        ui.getEventHandler().startComputerTurn();
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

        if (gameState == JTEGameState.GAME_IN_PROGRESS && !diceRoll) {

            roll = dice.roll();

            if (roll == 6)
                currentGame.getCurrent().setRepeat(true);

            currentGame.getCurrent().setMoves(roll);
            diceRoll = true;

            ui.getGamePlayPane().displayCity(info.getCities().get(currentGame.getCurrent().getCurrentCity()));
            ui.getGamePlayPane().setDiceLabel(roll);

            addToHistory("\n" + getCurrentPlayerName() + " rolls a " + roll + "\n");
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

    public boolean isHuman() {
        return getCurrentPlayer().isHuman();
    }

    public void saveGame() throws IOException {
        fileHandler.saveGame();
    }

    public void loadGame() throws IOException {
        fileHandler.loadGame(this);
    }

    public void loadGame(int numPlayers, ArrayList<Integer> humans, String[] playerNames, int current, String[] currentCities, ArrayList<ArrayList<String>> cards) {
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player("Player " + (i + 1), humans.contains(i), i);
            player.setCurrentCity(currentCities[i]);
            player.setCards(cards.get(i));
            player.setName(playerNames[i]);
            players.add(player);
        }

        currentGame = new JTEGameData(players);
        currentGame.setCurrent(current - 1);
    }

    public void moveComputer() {
        // regenerate edges array to account for sea routes and occupied spots
        fileHandler.createEdgeArray(info.getCities(), info.getFlightCities(), getRoll());
        CityNode current = info.getCities().get(getCurrentPlayer().getCurrentCity());

        resetVertices();
        computePaths(current);
        double minDistance = 0;
        List<CityNode> shortestPath = new ArrayList<>();
        for (String name : getCurrentPlayer().getCards()) {
            CityNode v = info.getCities().get(name);

            System.out.println("Distance from " + getCurrentPlayer().getCurrentCity() + " to " + v + ": "
              + v.minDistance);
            List<CityNode> path = getShortestPathTo(v);
            System.out.println("Path: " + path);

            if ((getCurrentPlayer().getCards().size() == 1 ||  !getCurrentPlayer().getHome().equals(name)) && (v.minDistance < minDistance || minDistance == 0)) {
                minDistance = v.minDistance;
                shortestPath = path;
            }
        }

        CityNode destination;
        if (shortestPath.size() != 0) {

            if (shortestPath.size() == 1)
                destination = shortestPath.get(0);
            else
                destination = shortestPath.get(1);

            boolean flight = false;
            if (!current.getRoads().contains(destination) && !current.getShips().contains(destination))
                flight = true;
            ui.getEventHandler().respondToCityClick(destination, flight);
        }
        System.out.println("\n\n");
    }

    public static void computePaths(CityNode source) {
        source.minDistance = 0.;
        PriorityQueue<CityNode> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(source);
        while (!vertexQueue.isEmpty()) {
            CityNode u = vertexQueue.poll();
            for (Edge e : u.getEdges()) {
                CityNode v = e.target;
                vertices.add(v);
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);
                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    public static void resetVertices() {
        for (CityNode v : vertices) {
            v.previous = null;
            v.minDistance = Double.POSITIVE_INFINITY;
        }
        vertices.clear();
    }

    public static List<CityNode> getShortestPathTo(CityNode target) {
        List<CityNode> path = new ArrayList<>();
        for (CityNode vertex = target; vertex != null; vertex = vertex.previous) {
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }

    public int getRoll() {
        return roll;
    }
}
