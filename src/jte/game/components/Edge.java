package jte.game.components;

/**
 *
 */
public class Edge {

    public final CityNode target;
    public final double weight;

    public Edge(CityNode target, double weight) {
        this.target = target;
        this.weight = weight;
    }
}
