
package algorithms;

public class DSU {
    private final int[] parent;
    private final int[] rank;
    private int componentsCount;
    private long dsuOps; // Счетчик операций DSU (find + успешные union)

    public DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        componentsCount = n;
        dsuOps = 0;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    // Поиск корня с эвристикой сжатия путей (Path Compression)
    public int find(int i) {
        dsuOps++; // Каждое обращение к find увеличивает счетчик
        if (parent[i] == i) {
            return i;
        }
        // Сжатие пути: подвешиваем узел напрямую к корню
        return parent[i] = find(parent[i]);
    }

    // Объединение по рангу (Union by Rank)
    public boolean union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);

        if (rootI != rootJ) {
            // Успешное объединение тоже учитывается в dsuOps согласно требованиям
            dsuOps++;
            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ;
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI;
            } else {
                parent[rootJ] = rootI;
                rank[rootI]++;
            }
            componentsCount--;
            return true; // Объединение произошло
        }
        return false; // Элементы уже в одном множестве
    }

    public int components() {
        return componentsCount;
    }

    public long getDsuOps() {
        return dsuOps;
    }

    public void resetOps() {
        this.dsuOps = 0;
    }
}
