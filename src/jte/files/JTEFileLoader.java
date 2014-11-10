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
                String[] values = nextLine.split("\\s+"); // should be five parts (or 6 if city has a space)
                CityNode city = null;
                if (values.length == 5)
                    city = new CityNode(values[0], Integer.parseInt(values[2]), Integer.parseInt(values[3])/3, Integer.parseInt(values[4])/3);
                else
                    city = new CityNode(values[0], Integer.parseInt(values[3]), Integer.parseInt(values[4])/3, Integer.parseInt(values[5])/3);
                cities.put(values[0], city);
            }
        } catch (IOException e) {
            System.out.println("City data does not exist or system unable to read it.");
            e.printStackTrace();
        }
        return cities;
    }
}
