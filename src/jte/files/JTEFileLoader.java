package jte.files;

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

            String nextLine = null; // skip the first line which is just the column headers

            while((nextLine = reader.readLine()) != null) {
                String[] values = nextLine.split("\\s+"); // should be 6 parts (or 7 if city has a space)
                CityNode city = null;
                if (values.length == 6)
                    city = new CityNode(values[0], Integer.parseInt(values[2]), Integer.parseInt(values[3])/3, Integer.parseInt(values[4])/3, Integer.parseInt(values[5]));
                else
                    city = new CityNode(values[0], Integer.parseInt(values[3]), Integer.parseInt(values[4])/3, Integer.parseInt(values[5])/3, Integer.parseInt(values[6]));
                cities.put(values[0], city);
                if (values[0].equals("ARKHANGEL'SK") )
                    System.out.println("STOP");
            }
        } catch (NumberFormatException | IOException e) {
            System.out.println("City data does not exist or system unable to read it.");
            e.printStackTrace();
        }

        // Add neighbors
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("data/cityNeighbors.csv"), "UTF-8"));

            String nextLine = null; // skip the first line which is just the column headers
            CityNode current = new CityNode("Test City", 0, 0, 0, 0); // default test city

            while((nextLine = reader.readLine()) != null) {

                String[] values = nextLine.split(","); // should be 2 parts
                switch (values[0]) {
                    case ("City name"):
                        current = cities.get(values[1]);
                        if (current == null)
                            System.out.println("FAIL: " + values[1]);
                        break;
                    case("Land neighbour"):
                        System.out.println("LAND: " + values[1]);
                        current.addRoad(cities.get(values[1]));
                        break;
                    case("Sea neighbour"):
                        current.addShip(cities.get(values[1]));
                        break;
                    default:
                        break;
                }
            }
        } catch (NumberFormatException | IOException e) {
            System.out.println("Error reading neighbor data.");
            e.printStackTrace();
        }
        return cities;
    }
}
