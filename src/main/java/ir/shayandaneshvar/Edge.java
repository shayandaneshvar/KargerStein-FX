package ir.shayandaneshvar;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Edge implements Serializable {
    private ArrayList<Pair<Integer, Integer>> edges;
    private Boolean marked = false;

    public Edge(Integer from, Integer to) {
        this();
        addEdge(from, to);
    }

    public Edge() {
        edges = new ArrayList<>();
    }

    public void addEdge(Integer from, Integer to) {
        edges.add(new Pair<>(from, to));
    }

    public void setMarked(boolean bool) {
        marked = bool;
    }

    public Boolean marked() {
        return marked;
    }

    public ArrayList<Pair<Integer, Integer>> getEdges() {
        return (ArrayList<Pair<Integer, Integer>>) edges.clone();
    }

    public Integer getFrom() {
        return edges.get(0).getKey();
    }

    public Integer getTo() {
        return edges.get(0).getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return (Objects.equals(edges.get(0).getKey(), edge.getEdges().get(0)
                .getKey()) && Objects.equals(edges.get(0).getValue(),
                edge.getEdges().get(0).getValue())) || (Objects
                .equals(edges.get(0).getValue(), edge.getEdges().get(0)
                        .getKey()) && Objects.equals(edges.get(0).getKey(),
                edge.getEdges().get(0).getValue()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEdges().get(0).getKey(),
                getEdges().get(0).getValue()) * Objects.hash(getEdges()
                .get(0).getValue(), getEdges().get(0).getKey());
    }
}