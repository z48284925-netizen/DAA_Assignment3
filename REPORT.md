# Assignment 3 - Graphs: DSU, MST, SCC and Shortest Paths

## 1. Complexity Table
| Algorithm | Time Complexity | Space Complexity | Justification |
| :--- | :--- | :--- | :--- |
| **DSU** (find/union) | $O(\alpha(n))$ | $O(n)$ | Path compression and union by rank keep trees nearly flat. |
| **Kruskal (MST)** | $O(E \log E)$ | $O(V + E)$ | Dominated by sorting all edges by weight. |
| **Prim (MST)** | $O(E \log V)$ | $O(V + E)$ | Using a PriorityQueue to greedily select the lightest cut edge. |
| **Kosaraju (SCC)** | $O(V + E)$ | $O(V + E)$ | Two independent graph traversals (DFS passes) using an explicit stack. |
| **Kahn (Topological Sort)**| $O(V + E)$ | $O(V + E)$ | In-degree tracking and queue processing for every vertex and edge. |
| **DAG Shortest Paths** | $O(V + E)$ | $O(V + E)$ | Single relaxation pass over edges in topological order. |

---

## 2. Correctness Arguments

### Cut Property in Minimum Spanning Trees
The cut property states that for any cut in a graph, if the weight of an edge $e$ is strictly smaller than the weights of all other edges crossing the cut, then this edge belongs to all MSTs of the graph. Both **Kruskal's** and **Prim's** algorithms rely on this invariant. Kruskal inspects edges globally in increasing order of weight and safely adds them if they bridge two disjoint components via DSU. Prim grows a single tree locally from a starting vertex by always picking the minimum-weight edge connecting the visited set to unvisited vertices using a priority queue.

### Induction on Topological Order for DAG Shortest Paths
In a Directed Acyclic Graph (DAG), processing vertices in topological order guarantees that when we relax the outgoing edges of a vertex $u$, all incoming paths to $u$ have already been fully processed and their optimal distances finalized. By induction, the shortest path to every vertex is correctly computed in a single linear pass of $O(V + E)$, since no back-edges exist to invalidate prior distances.

---

## 3. Which Algorithm When?
| Graph Type / Condition | Best Algorithm | Reason |
| :--- | :--- | :--- |
| **Sparse Graph (MST)** | Kruskal | Very efficient when $E$ is close to $V$, especially with nearly-constant DSU. |
| **Dense Graph (MST)** | Prim | Performs better with a priority queue when edge count approaches $V^2$. |
| **Cycle Detection / Ordering** | Kahn's Algorithm | Straightforward in-degree queue approach; naturally detects cycles if processed count $< V$. |
| **Strongly Connected Components** | Kosaraju / Tarjan | Efficient linear-time identification of maximal strongly connected subgraphs in directed networks. |
| **Shortest Paths with Negatives** | DAG Shortest Paths | Safe and runs in $O(V + E)$ time; general graphs would require slower Bellman-Ford or fail with cycles. |