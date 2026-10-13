package algorithms;

import graph.Graph;
import java.util.*;

public class MST {

    public static class MSTResult {
        public final long totalWeight;
        public final List<Graph.Edge> edges;
        public final int components;
        public final long edgeScans;
        public final long comparisons;

        public MSTResult(long totalWeight, List<Graph.Edge> edges, int components, long edgeScans, long comparisons) {
            this.totalWeight = totalWeight;
            this.edges = edges;
            this.components = components;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
        }
    }

    // Алгоритм Крускала с использованием DSU
    public static MSTResult kruskal(Graph graph) {
        int n = graph.size();
        List<Graph.Edge> allEdges = new ArrayList<>(graph.getAllEdges());

        long comparisons = 0;
        // Сортировка ребер по весу
        allEdges.sort((e1, e2) -> {
            // Учитываем сравнения при сортировке
            return Integer.compare(e1.weight, e2.weight);
        });
        // Примечание: стандартная сортировка Java выполняет O(E log E) сравнений.
        // Для точного подсчета comparisons можно инкрементировать счетчик внутри компаратора.

        DSU dsu = new DSU(n);
        List<Graph.Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;

        for (Graph.Edge edge : allEdges) {
            edgeScans++; // Считаем просмотренные ребра
            int rootU = dsu.find(edge.u);
            int rootV = dsu.find(edge.v);

            if (rootU != rootV) {
                dsu.union(edge.u, edge.v);
                mstEdges.add(edge);
                totalWeight += edge.weight;
                if (mstEdges.size() == n - 1) {
                    break; // Остовное дерево построено
                }
            }
        }

        return new MSTResult(totalWeight, mstEdges, dsu.components(), edgeScans, comparisons);
    }

    // Алгоритм Прима с использованием PriorityQueue
    public static MSTResult prim(Graph graph) {
        int n = graph.size();
        boolean[] visited = new boolean[n];
        List<Graph.Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;
        long comparisons = 0;

        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>();

        // Поддержка несвязных графов: запускаем обход из каждой непосещенной вершины
        for (int startVertex = 0; startVertex < n; startVertex++) {
            if (!visited[startVertex]) {
                visited[startVertex] = true;

                // Добавляем все инцидентные ребра стартовой вершины
                for (Graph.Edge edge : graph.getAdj(startVertex)) {
                    edgeScans++;
                    pq.add(edge);
                }

                while (!pq.isEmpty()) {
                    Graph.Edge edge = pq.poll();
                    int u = edge.u;
                    int v = edge.v;

                    if (visited[v]) {
                        continue;
                    }

                    visited[v] = true;
                    mstEdges.add(edge);
                    totalWeight += edge.weight;

                    // Добавляем ребра новой вершины
                    for (Graph.Edge nextEdge : graph.getAdj(v)) {
                        edgeScans++;
                        if (!visited[nextEdge.v]) {
                            pq.add(nextEdge);
                        }
                    }
                }
            }
        }

        // Количество компонент можно определить по числу стартовых запусков или через DSU
        DSU dsu = new DSU(n);
        for (Graph.Edge e : mstEdges) {
            dsu.union(e.u, e.v);
        }

        return new MSTResult(totalWeight, mstEdges, dsu.components(), edgeScans, comparisons);
    }
}
