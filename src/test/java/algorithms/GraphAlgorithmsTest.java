package algorithms;

import graph.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraphAlgorithmsTest {

    @Test
    public void testDSU() {
        DSU dsu = new DSU(5);
        dsu.union(0, 1);
        dsu.union(1, 2);
        assertEquals(dsu.find(0), dsu.find(2));
        assertNotEquals(dsu.find(0), dsu.find(3));
        assertEquals(3, dsu.components());
    }

    @Test
    public void testMSTAgreement() {
        Graph g = benchmark.GraphBenchmark.generateGraph(50, 200, "undirected");
        MST.MSTResult kruskal = MST.kruskal(g);
        MST.MSTResult prim = MST.prim(g);
        assertEquals(kruskal.totalWeight, prim.totalWeight);
    }

    @Test
    public void testTopologicalSort() {
        Graph dag = benchmark.GraphBenchmark.generateGraph(30, 80, "dag");
        GraphAlgorithms.KahnResult kahn = GraphAlgorithms.kahn(dag);
        assertFalse(kahn.hasCycle);
        assertEquals(30, kahn.order.size());
    }
}
