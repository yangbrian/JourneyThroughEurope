package jte.game.components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

/**
 * @author Brian Yang
 */
public class CityNode extends Circle {

    private int quarter;

    private String name;
    private ArrayList<CityNode> roads;
    private ArrayList<CityNode> ships;
    private int region;
    private boolean occupied;
    private int x;
    private int y;
    private String color;

    private Color original;

    public CityNode(String name, int quarter, int x, int y, int region, String color) {
        super(x, y, 10, region == 0 ? Color.BLACK : Color.RED); // red nodes for airport cities, black otherwise
        this.setOpacity(1.0);
        this.name = name;
        this.region = region;
        this.quarter = quarter;
        this.occupied = false;
        this.x = x;
        this.y = y;

        roads = new ArrayList<>();
        ships = new ArrayList<>();

        this.original = region == 0 ? Color.BLACK : Color.RED;
        this.color = color;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<CityNode> getRoads() {
        return roads;
    }

    public ArrayList<CityNode> getShips() {
        return ships;
    }

    public void addRoad(CityNode city) {
        roads.add(city);
    }

    public void addShip(CityNode city) {
        ships.add(city);
    }

    /**
     * Reset the color of the city node after the neighbor animation
     */
    public void resetColor() {
        this.setFill(original);
        this.setOpacity(1.0);
    }

    public String getColor() {
        return color;
    }
}
