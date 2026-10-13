package algorithms;

import graph.Graph;
import java.util.*;

public class DagShortestPath {

    public static class PathResult {
        public final long[] distances;
        public final int[] parent;
        public final long totalSumDistances;
        public final long edgeScans;

        public PathResult(long[] distances, int[] parent, long totalSumDistances, long edgeScans) {
            this.distances = distances;
            this.parent = parent;
            this.totalSumDistances = totalSumDistances;
            this.edgeScans = edgeScans;
        }
    }

    // Вычисление кратчайших путей в DAG за O(V + E)
    public static PathResult compute(Graph graph, List<Integer> topoOrder) {
        int n = graph.size();
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Long.MAX_VALUE / 2); // Защита от переполнения при сложении
        Arrays.fill(parent, -1);

        // Использование виртуального супер-источника: все вершины с in-degree 0 стартуют с расстояния 0
        // (в топологическом порядке они гарантированно идут раньше своих соседей)
        for (int u : topoOrder) {
            // Если у вершины нет входящих ребер, но она первая или доступна,
            // для супер-источника устанавливаем dist = 0, если расстояние еще не задано.
            // По условию: "Every vertex with in-degree 0 starts at distance 0"
            boolean hasIncoming = false;
            // Проверим, есть ли входящие ребра (можно оптимизировать, но для чистоты пройдем по всем)
        }

        // Точнее: найдем вершины с нулевой степенью входа или просто инициализируем все потенциальные истоки нулем,
        // либо сделаем классическую инициализацию:
        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getAdj(u)) {
                inDegree[edge.v]++;
            }
        }

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        long edgeScans = 0;

        // Релаксация ребер в топологическом порядке
        for (int u : topoOrder) {
            if (dist[u] != Long.MAX_VALUE / 2) {
                for (Graph.Edge edge : graph.getAdj(u)) {
                    edgeScans++;
                    int v = edge.v;
                    if (dist[u] + edge.weight < dist[v]) {
                        dist[v] = dist[u] + edge.weight;
                        parent[v] = u;
                    }
                }
            }
        }

        // Подсчет суммы всех расстояний (требуется для сверки с эталоном)
        long totalSumDistances = 0;
        for (int i = 0; i < n; i++) {
            if (dist[i] < Long.MAX_VALUE / 2) {
                totalSumDistances += dist[i];
            }
        }

        return new PathResult(dist, parent, totalSumDistances, edgeScans);
    }

    // Метод для восстановления пути к конкретной вершине
    public static List<Integer> restorePath(int[] parent, int target) {
        List<Integer> path = new ArrayList<>();
        for (int v = target; v != -1; v = parent[v]) {
            path.add(v);
        }
        Collections.reverse(path);
        return path;
    }
}
