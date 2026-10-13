package benchmark;

import algorithms.*;
import graph.Graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class GraphBenchmark {

    public static class GraphParams {
        public final int n;
        public final int m;
        public final String type;

        public GraphParams(int n, int m, String type) {
            this.n = n;
            this.m = m;
            this.type = type;
        }
    }

    // Точный генератор графов из задания с Random(42)
    public static Graph generateGraph(int n, int m, String type) {
        Random rnd = new Random(42);
        boolean directed = !type.equals("undirected");
        Graph graph = new Graph(n, directed);

        if (type.equals("undirected")) {
            int[] perm = new int[n];
            for (int i = 0; i < n; i++) perm[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int temp = perm[i];
                perm[i] = perm[j];
                perm[j] = temp;
            }
            for (int i = 1; i < n; i++) {
                int parent = perm[rnd.nextInt(i)];
                int current = perm[i];
                int weight = 1 + rnd.nextInt(1000);
                graph.addEdge(parent, current, weight);
            }
            int currentEdges = n - 1;
            while (currentEdges < m) {
                int u = rnd.nextInt(n);
                int v = rnd.nextInt(n);
                if (u != v) {
                    int weight = 1 + rnd.nextInt(1000);
                    graph.addEdge(u, v, weight);
                    currentEdges++;
                }
            }
        } else if (type.equals("directed")) {
            int currentEdges = 0;
            while (currentEdges < m) {
                int u = rnd.nextInt(n);
                int v = rnd.nextInt(n);
                if (u != v) {
                    int weight = 1 + rnd.nextInt(1000);
                    graph.addEdge(u, v, weight);
                    currentEdges++;
                }
            }
        } else if (type.equals("dag")) {
            int[] rank = new int[n];
            for (int i = 0; i < n; i++) rank[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int temp = rank[i];
                rank[i] = rank[j];
                rank[j] = temp;
            }

            int currentEdges = 0;
            while (currentEdges < m) {
                int u = rnd.nextInt(n);
                int v = rnd.nextInt(n);
                if (u != v) {
                    if (rank[u] > rank[v]) {
                        int temp = u;
                        u = v;
                        v = temp;
                    }
                    if (u != v) {
                        int weight = 1 + rnd.nextInt(1000);
                        graph.addEdge(u, v, weight);
                        currentEdges++;
                    }
                }
            }
        }
        return graph;
    }

    public static void runBenchmark() {
        int[] sizes = {1000, 3162, 10000, 31623, 100000};

        System.out.println("Running benchmarks and saving to CSV...");

        // Создаем папку results в корне проекта
        File resultsDir = new File("results");
        if (!resultsDir.exists()) {
            resultsDir.mkdir();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("results/results.csv"))) {
            // Заголовок CSV файла
            writer.println("algorithm,graph,n,m,time_ms,result");

            for (int n : sizes) {
                int sparseM = 4 * n;
                int denseM = 20 * n;

                runAndSave(n, sparseM, writer);
                runAndSave(n, denseM, writer);
            }
            System.out.println("Benchmark finished successfully! File results/results.csv created.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runAndSave(int n, int m, PrintWriter writer) {
        // MST (Kruskal & Prim)
        Graph undirGraph = generateGraph(n, m, "undirected");

        long start = System.currentTimeMillis();
        MST.MSTResult kruskalRes = MST.kruskal(undirGraph);
        long kruskalTime = System.currentTimeMillis() - start;
        writer.println(String.format("Kruskal,undirected,%d,%d,%d,%d", n, m, kruskalTime, kruskalRes.totalWeight));

        start = System.currentTimeMillis();
        MST.MSTResult primRes = MST.prim(undirGraph);
        long primTime = System.currentTimeMillis() - start;
        writer.println(String.format("Prim,undirected,%d,%d,%d,%d", n, m, primTime, primRes.totalWeight));

        // SCC (Kosaraju)
        Graph dirGraph = generateGraph(n, m, "directed");
        start = System.currentTimeMillis();
        GraphAlgorithms.SCCResult sccRes = GraphAlgorithms.kosaraju(dirGraph);
        long sccTime = System.currentTimeMillis() - start;
        writer.println(String.format("Kosaraju,directed,%d,%d,%d,%d", n, m, sccTime, sccRes.sccCount));

        // DAG Shortest Paths
        Graph dagGraph = generateGraph(n, m, "dag");
        start = System.currentTimeMillis();
        GraphAlgorithms.KahnResult kahnRes = GraphAlgorithms.kahn(dagGraph);
        DagShortestPath.PathResult spRes = DagShortestPath.compute(dagGraph, kahnRes.order);
        long dagTime = System.currentTimeMillis() - start;
        writer.println(String.format("DAG_ShortestPath,dag,%d,%d,%d,%d", n, m, dagTime, spRes.totalSumDistances));

        System.out.println(String.format("Processed n=%d, m=%d", n, m));
    }
}