package benchmark;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== DAA Assignment 3: Graphs Benchmark Starting ===");

        // Запуск полного бенчмарка с генерацией графов и сверкой результатов
        GraphBenchmark.runBenchmark();

        System.out.println("=== Benchmark Completed Successfully ===");
    }
}
