package jte.game.components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * @author Brian Yang
 */
public class CityNode extends Circle {

    private int quarter;

    private String name;
    private CityNode[] roads;
    private CityNode[] ships;
    private int region;
    private boolean occupied;
    private int x;
    private int y;

    public CityNode(String name, int quarter, int x, int y, int region) {
        super(8, region == 0 ? Color.BLACK : Color.RED); // red nodes for airport cities, black otherwise
        this.name = name;
        this.region = region;
        this.quarter = quarter;
        this.occupied = false;
        this.x = x;
        this.y = y;
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

    public CityNode[] getRoads() {
        return roads;
    }

    public void setRoads(CityNode[] roads) {
        this.roads = roads;
    }

    public CityNode[] getShips() {
        return ships;
    }

    public void setShips(CityNode[] ships) {
        this.ships = ships;
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
}
