package algorithms;

import graph.Graph;
import java.util.*;

public class GraphAlgorithms {

    public static class SCCResult {
        public final int sccCount;
        public final long edgeScans;
        public final int maxDepth;

        public SCCResult(int sccCount, long edgeScans, int maxDepth) {
            this.sccCount = sccCount;
            this.edgeScans = edgeScans;
            this.maxDepth = maxDepth;
        }
    }

    public static class KahnResult {
        public final List<Integer> order;
        public final boolean hasCycle;
        public final long edgeScans;

        public KahnResult(List<Integer> order, boolean hasCycle, long edgeScans) {
            this.order = order;
            this.hasCycle = hasCycle;
            this.edgeScans = edgeScans;
        }
    }

    // 1. Алгоритм Косаражу с итеративным DFS (безопасен для 100 000 вершин)
    public static SCCResult kosaraju(Graph graph) {
        int n = graph.size();
        boolean[] visited = new boolean[n];
        Deque<Integer> finishOrder = new ArrayDeque<>();
        long edgeScans = 0;
        int maxDepth = 0;

        // Первый проход DFS для получения порядка завершения
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                // Итеративный DFS с явным стеком для предотвращения StackOverflowError
                maxDepth = Math.max(maxDepth, iterativeDfsFirstPass(graph, i, visited, finishOrder));
            }
        }

        // Инвертируем граф для второго прохода
        Graph reversedGraph = getReversedGraph(graph);

        Arrays.fill(visited, false);
        int sccCount = 0;

        // Второй проход в порядке убывания времени завершения
        for (int u : finishOrder) {
            if (!visited[u]) {
                sccCount++;
                edgeScans += iterativeDfsSecondPass(reversedGraph, u, visited);
            }
        }

        // По заданию Косаражу сканирует ровно 2 * m ребер (два прохода)
        long totalEdgeScans = 2L * graph.getAllEdges().size();

        return new SCCResult(sccCount, totalEdgeScans, maxDepth);
    }

    private static int iterativeDfsFirstPass(Graph graph, int start, boolean[] visited, Deque<Integer> finishOrder) {
        // Структура для имитации стека кадров рекурсии
        Deque<Iterator<Graph.Edge>> stackIterators = new ArrayDeque<>();
        Deque<Integer> stackNodes = new ArrayDeque<>();

        visited[start] = true;
        stackNodes.push(start);
        stackIterators.push(graph.getAdj(start).iterator());

        int currentMaxDepth = 1;

        while (!stackNodes.isEmpty()) {
            int u = stackNodes.peek();
            Iterator<Graph.Edge> it = stackIterators.peek();

            if (it.hasNext()) {
                Graph.Edge edge = it.next();
                int v = edge.v;
                if (!visited[v]) {
                    visited[v] = true;
                    stackNodes.push(v);
                    stackIterators.push(graph.getAdj(v).iterator());
                    currentMaxDepth = Math.max(currentMaxDepth, stackNodes.size());
                }
            } else {
                stackNodes.pop();
                stackIterators.pop();
                finishOrder.push(u); // Добавляем в стек завершения
            }
        }
        return currentMaxDepth;
    }

    private static long iterativeDfsSecondPass(Graph reversedGraph, int start, boolean[] visited) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        visited[start] = true;
        long localScans = 0;

        while (!stack.isEmpty()) {
            int u = stack.pop();
            for (Graph.Edge edge : reversedGraph.getAdj(u)) {
                localScans++;
                int v = edge.v;
                if (!visited[v]) {
                    visited[v] = true;
                    stack.push(v);
                }
            }
        }
        return localScans;
    }

    private static Graph getReversedGraph(Graph graph) {
        int n = graph.size();
        Graph rev = new Graph(n, true);
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getAdj(u)) {
                rev.addEdge(edge.v, edge.u, edge.weight);
            }
        }
        return rev;
    }

    // 2. Алгоритм Кана (топологическая сортировка и проверка циклов)
    public static KahnResult kahn(Graph graph) {
        int n = graph.size();
        int[] inDegree = new int[n];
        long edgeScans = 0;

        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getAdj(u)) {
                edgeScans++;
                inDegree[edge.v]++;
            }
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);

            for (Graph.Edge edge : graph.getAdj(u)) {
                int v = edge.v;
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.add(v);
                }
            }
        }

        boolean hasCycle = (order.size() != n);
        return new KahnResult(order, hasCycle, edgeScans);
    }
}
