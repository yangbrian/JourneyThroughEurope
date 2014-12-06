package jte.files;

import javafx.scene.paint.Color;
import jte.game.JTEGameStateManager;
import jte.game.components.Card;
import jte.game.components.CityNode;
import jte.ui.JTEUI;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Loads all files associated with Journey Through Europe
 * @author Brian Yang
 */
public class JTEFileLoader {
    private JTEUI ui;

    public JTEFileLoader(JTEUI ui) {
        this.ui = ui;
    }

    public HashMap<String, CityNode> loadCities() {
        HashMap<String, CityNode> cities = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("data/jteCities.csv")));

            String nextLine = null;

            while((nextLine = reader.readLine()) != null) {
                String[] values = nextLine.split(","); // should be 6 parts
                int currentX = Integer.parseInt(values[3]);
                int currentY = Integer.parseInt(values[4]);
                switch(Integer.parseInt(values[2])) { // apply offsets to adjust for scaling
                    case 1:
                        currentX = (int)(currentX * 0.4);
                        currentY = (int)(currentY * 0.4);
                        break;
                    case 2:
                        currentX = (int)(currentX * 0.4) + 805;
                        currentY = (int)(currentY * 0.4);
                        break;
                    case 3:
                        currentX = (int)(currentX * 0.4);
                        currentY = (int)(currentY * 0.4) + 1024;
                        break;
                    case 4:
                        currentX = (int)(currentX * 0.4) + 794;
                        currentY = (int)(currentY * 0.4) + 1028;
                        break;
                }

                CityNode city = new CityNode(
                  values[0],                    // name
                  Integer.parseInt(values[2]),  // quarter
                  currentX,                     // x
                  currentY,                     // y
                  0,                            // flight region - no region = 0 (default for now)
                  values[1].toUpperCase()       // card color
                );

                cities.put(values[0], city);
            }
        } catch (NumberFormatException | IOException e) {
            System.out.println("City data does not exist or system unable to read it.");
            e.printStackTrace();
        }

        // Add neighbors
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("data/cityNeighbors.csv"), "UTF-8"));

            String nextLine = null; // skip the first line which is just the column headers
            CityNode current = new CityNode("Test City", 0, 0, 0, 0, "Red"); // default test city

            while((nextLine = reader.readLine()) != null) {

                String[] values = nextLine.split(","); // should be 2 parts
                switch (values[0]) {
                    case ("City name"):
                        current = cities.get(values[1]);
                        if (current == null)
                            System.out.println("FAIL: " + values[1]);
                        break;
                    case("Land neighbour"):
                        current.addRoad(cities.get(values[1]));
                        break;
                    case("Sea neighbour"):
                        current.addShip(cities.get(values[1]));
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading neighbor data.");
            e.printStackTrace();
        }

        return cities;
    }

    public HashMap<String, CityNode> loadFlightPlan(HashMap<String, CityNode> cities) {
        HashMap<String, CityNode> flightPlan = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("data/flightPlan.csv"));
            String nextLine = null;

            while((nextLine = reader.readLine()) != null) {
                String[] values = nextLine.split(","); // should be 6 parts

                String name = values[0];
                int x = Integer.parseInt(values[1]);
                int y = Integer.parseInt(values[2]);
                int region = Integer.parseInt(values[3]);

                CityNode flightCity = new CityNode(name, 0, x, y, region, "N/A");
                flightPlan.put(name, flightCity);

                cities.get(name).setRegion(region);

            }
        } catch (IOException e) {
            System.out.println("Error reading flight plan data");
            e.printStackTrace();
        }
        return flightPlan;
    }

    public void saveGame() throws IOException {
        String save = ui.getGsm().getData().getSaveData();

        File saveFile = new File("data/save.jte");
        BufferedWriter out = new BufferedWriter(new FileWriter(saveFile));

        out.write(save);

        out.flush();
        out.close();
    }

    public void loadGame(JTEGameStateManager gsm) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("data/save.jte"));

        int numPlayers = Integer.parseInt(in.readLine()); // total number of players

        String[] humanPlayersString = in.readLine().split(" "); // IDs of human players
        ArrayList<Integer> humanPlayers = new ArrayList<>();
        for (String aHumanPlayersString : humanPlayersString)
            humanPlayers.add(Integer.parseInt(aHumanPlayersString));

        String[] playerNames = in.readLine().split(" "); // player names

        int current = Integer.parseInt(in.readLine());

        String[] currentCities = in.readLine().split(" "); // current cities of players

        // add each player's cards
        ArrayList<ArrayList<String>> playerCards = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            playerCards.add(new ArrayList<>());
            ArrayList<String> playerHand = playerCards.get(i);

            String[] cards = in.readLine().split(" ");
            Collections.addAll(playerHand, cards);
        }

        in.close();

        gsm.loadGame(numPlayers, humanPlayers, playerNames, current, currentCities, playerCards);
    }
}
