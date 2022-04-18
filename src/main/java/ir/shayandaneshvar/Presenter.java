package ir.shayandaneshvar;

import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Presenter {
    private Graph graph;
    private SmartGraphPanel<String, String> graphView;
    private GraphEdgeList<String, String> graphEdgeList;
    private ArrayList<Integer>[] minCutGraph = null;
    private Integer minCut = Integer.MAX_VALUE;
    private Integer[][] matrix;
    @FXML
    private JFXTextArea matrixArea;
    @FXML
    private JFXTextField length;
    @FXML
    private JFXCheckBox autoLayout;
    @FXML
    private JFXCheckBox optimized;

    @FXML
    void drawGraph() {
        Platform.runLater(() -> {
            handleGraphWindow("Graph");
            getGraph(Integer.parseInt(length.getText().trim()));
            prepareGraph(graph);
        });
    }

    private void prepareGraph(Graph graph) {
        for (int i = 0; i < graph.getSize(); i++) {
            graphEdgeList.insertVertex(String.valueOf(i));
        }
        graph.getEdges().forEach(e -> {
            var from = String.valueOf(e.getFrom());
            var to = String.valueOf(e.getTo());
            String edgeElement = from + "-" + to;
            if (!e.marked()) {
                graphEdgeList.insertEdge(from, to, edgeElement);
            }
            graphView.update();
        });
    }

    private void handleGraphWindow(String title) {
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        graphEdgeList = new GraphEdgeList<>();
        graphView = new SmartGraphPanel<>(graphEdgeList, strategy);
        graphView.automaticLayoutProperty.setValue(autoLayout.isSelected());
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.getIcons().add(new Image("file:///" + new File("" +
                "src/main/resources/images/icon.png").getAbsolutePath()));
        Scene scene = new Scene(graphView, 800, 800, false,
                SceneAntialiasing.BALANCED);
        stage.setScene(scene);
        stage.show();
        graphView.init();
    }

    private void getGraph(int size) {
        matrix = new Integer[size][size];
        String string = matrixArea.getText().replaceAll("\\D+", "-");
        StringTokenizer tokenizer = new StringTokenizer(string, "-");
        if (tokenizer.countTokens() < size) {
            length.setText("?");
            return;
        }
        graph = new Graph(size);
        for (int i = 0; i < size * size; i++) {
            int weight = Integer.parseInt(tokenizer.nextToken());
            if (weight == 0) {
                matrix[i / size][i % size] = 0;
                continue;
            }
            matrix[i / size][i % size] = 1;
            graph.addEdge(new Edge(i / size, i % size));
        }
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log10(2);
    }

    @FXML
    void drawMinCut() {
        minCut = Integer.MAX_VALUE;
        getGraph(Integer.parseInt(length.getText().trim()));
        Platform.runLater(() -> {
            handleGraphWindow("Karger-Stein Minimum Cut Algorithm");
            while (true) {
                try {
                    Graph res = getMinCut();
                    prepareGraph(res);
                } catch (Exception e) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    continue;
                }
                break;
            }
        });
    }

    private Graph getMinCut() {
        final double delta = 0.05;
        minCutGraph = new ArrayList[graph.getSize()];
        for (int i = 0; i < graph.getSize(); i++) {
            minCutGraph[i] = new ArrayList<>();
            minCutGraph[i].add(i);
        }
        for (int i = 0; i < (optimized.isSelected() ?
                log2(graph.getSize()) * log2(1.0 / delta) : 1); i++) {
            kargerSteinAlgorithm(minCutGraph);
        }
        Graph result = new Graph(graph.getSize());
        graph.getEdges().forEach(result::addEdge);
        for (var i : minCutGraph[0]) {
            for (var j : minCutGraph[1]) {
                result.getEdges().stream().filter(x -> (x.getFrom().equals(i) &&
                        x.getTo().equals(j)) || (x.getFrom().equals(j) && x.
                        getTo().equals(i))).forEach(y -> y.setMarked(true));
            }
        }
        return result;
    }

    private void kargerSteinAlgorithm(ArrayList<Integer>[] temp) {
        ArrayList<Integer>[] tempGraph = temp;
        if (temp.length == 2) {
            int counter = 0;
            for (var i : temp[0]) {
                for (var j : temp[1]) {
                    if (matrix[i][j] == 1) {
                        counter++;
                    }
                }
            }
            if (minCut > counter) {
                minCut = counter;
                this.minCutGraph = tempGraph;
            }
            return;
        }
        while (tempGraph.length > (int) (temp.length / Math.sqrt(2))) {
            var edge = getRandomVertices(tempGraph);
            ArrayList<Integer> first = Arrays.stream(tempGraph)
                    .filter(z -> z.contains(edge.getValue()))
                    .findAny().orElseThrow();
            ArrayList<Integer> second = Arrays.stream(tempGraph)
                    .filter(z -> z.contains(edge.getKey()))
                    .findAny().orElseThrow();
            ArrayList<Integer>[] newTemp = new ArrayList[tempGraph.length - 1];
            for (int j = 0, i = 0; i < tempGraph.length; i++) {
                if (tempGraph[i].contains(edge.getKey()) || tempGraph[i].contains(edge.getValue())) {
                    continue;
                }
                newTemp[j] = tempGraph[i];
                j++;
            }
            first.addAll(second);
            newTemp[newTemp.length - 1] = first;
            tempGraph = newTemp;
        }
        kargerSteinAlgorithm(tempGraph);
        kargerSteinAlgorithm(tempGraph);
    }

    private Pair<Integer, Integer> getRandomVertices(ArrayList<Integer>[] inputGraph) {
        int rand = (int) Math.abs(Math.random() * 1000000) % inputGraph.length;
        int rand1 = (int) Math.abs(Math.random() * 100000) % inputGraph.length;
        int u = inputGraph[rand].get((int) Math.abs(Math.random() * 100000) %
                inputGraph[rand].size());
        int v = inputGraph[rand1].get((int) Math.abs(Math.random() * 100000) %
                inputGraph[rand1].size());
        if (rand == rand1 || graph.getEdges().stream().flatMap(x -> x.getEdges()
                .stream()).noneMatch(z ->
                (z.getKey().equals(v) && z.getValue().equals(u)) || (z.
                        getKey().equals(u) && z.getValue().equals(v)))) {
            return getRandomVertices(inputGraph);
        }
        return new Pair<>(u, v);
    }
}