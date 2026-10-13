package graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Graph {

    public static class Edge implements Comparable<Edge> {
        public final int u;
        public final int v;
        public final int weight;

        public Edge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }
    }

    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> allEdges;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        this.allEdges = new ArrayList<>();
    }

    public void addEdge(int u, int v, int weight) {
        Edge edge = new Edge(u, v, weight);
        allEdges.add(edge);
        adj.get(u).add(edge);
        if (!directed) {
            // Для неориентированного графа добавляем обратное ребро для удобства некоторых алгоритмов,
            // но следим за дублированием в списке allEdges.
            Edge reverseEdge = new Edge(v, u, weight);
            adj.get(v).add(reverseEdge);
        }
    }

    public int size() {
        return n;
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> getAdj(int u) {
        return adj.get(u);
    }

    public List<Edge> getAllEdges() {
        return allEdges;
    }

    // Чтение графа из текстового файла по формату задания:
    // Первая строка: n m directed (1 или 0) undirected (1 или 0)
    // Следующие m строк: u v w
    public static Graph fromFile(String filepath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String header = reader.readLine();
            if (header == null || header.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid graph file header");
            }
            String[] parts = header.trim().split("\\s+");
            int n = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            int directedFlag = Integer.parseInt(parts[2]);
            boolean directed = (directedFlag == 1);

            Graph graph = new Graph(n, directed);

            for (int i = 0; i < m; i++) {
                String line = reader.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] edgeParts = line.split("\\s+");
                int u = Integer.parseInt(edgeParts[0]);
                int v = Integer.parseInt(edgeParts[1]);
                int w = Integer.parseInt(edgeParts[2]);
                graph.addEdge(u, v, w);
            }
            return graph;
        }
    }
}

