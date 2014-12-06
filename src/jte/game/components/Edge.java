package jte.game.components;

/**
 *
 */
public class Edge {

    public final CityNode vertex;
    public final double weight;

    public Edge(CityNode vertex, double weight) {
        this.vertex = vertex;
        this.weight = weight;
    }
}
