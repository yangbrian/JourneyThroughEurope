package jte.files;

import java.util.HashMap;

/**
 * Manages all string values including city descriptions
 */
public class PropertiesManager {

    private static HashMap<String, String> descriptions;
    private static HashMap<String, String> values;

    public static void setDescriptions(HashMap<String, String> descriptions) {
        PropertiesManager.descriptions = descriptions;
    }

    public static void setValues(HashMap<String, String> values) {
        PropertiesManager.values = values;
    }

    public static String getValue(String key) {
        return values.get(key);
    }

    public static String getDescription(String city) {
        return descriptions.containsKey(city) ? descriptions.get(city) : "No description of " + city + " available.";
    }
}
