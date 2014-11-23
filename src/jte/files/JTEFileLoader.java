package jte.files;

import jte.game.components.Card;
import jte.game.components.CityNode;
import jte.ui.JTEUI;

import java.io.*;
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
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("data/jteCities.csv"), "UTF-16"));

            String nextLine = null;

            while((nextLine = reader.readLine()) != null) {
                String[] values = nextLine.split("\\s+"); // should be 6 parts

                int currentX = Integer.parseInt(values[3]);
                int currentY = Integer.parseInt(values[4]);
                switch(Integer.parseInt(values[2])) { // apply offsets to adjust for scaling
                    case 1:
                        currentX = currentX/3;
                        currentY = currentY/3;
                        break;
                    case 2:
                        currentX = currentX/3 + 670 - 12;
                        currentY = currentY/3 - 5;
                        break;
                    case 3:
                        currentX = currentX/3 - 5;
                        currentY = currentY/3 + 862 - 25;
                        break;
                    case 4:
                        currentX = currentX/3 + 662 - 14;
                        currentY = currentY/3 + 862 - 16;
                        break;
                }

                CityNode city = new CityNode(
                  values[0],                    // name
                  Integer.parseInt(values[2]),  // quarter
                  currentX,                     // x
                  currentY,                     // y
                  Integer.parseInt(values[5]),  // flight region
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

    public Card makeCard(String name, String color) {
        return null;
    }
}
