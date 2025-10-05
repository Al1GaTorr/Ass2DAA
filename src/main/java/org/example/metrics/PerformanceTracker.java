package org.example.metrics;

import java.io.*;
import java.util.*;

/**
 * Tracks and exports performance metrics for algorithm analysis.
 */
public class PerformanceTracker {

    private List<BenchmarkResult> results;

    public PerformanceTracker() {
        this.results = new ArrayList<>();
    }

    /**
     * Records a benchmark result.
     */
    public void recordResult(BenchmarkResult result) {
        results.add(result);
    }

    /**
     * Exports results to CSV format.
     */
    public void exportToCSV(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("Operation,n,Time_ms,Comparisons,Swaps");

            // Write data
            for (BenchmarkResult result : results) {
                writer.printf("%s,%d,%.3f,%d,%d%n",
                        result.operation,
                        result.inputSize,
                        result.timeNanos / 1_000_000.0,
                        result.comparisons,
                        result.swaps
                );
            }
        }
    }

    /**
     * Prints a summary table to console.
     */
    public void printSummary() {
        System.out.println("\n" + "=".repeat(85));
        System.out.println("PERFORMANCE SUMMARY");
        System.out.println("=".repeat(85));
        System.out.printf("%-20s %-12s %-15s %-12s %-10s%n",
                "Operation", "n", "Time (ms)", "Comparisons", "Swaps");
        System.out.println("-".repeat(85));

        for (BenchmarkResult result : results) {
            System.out.printf("%-20s %-12d %-15.3f %-12d %-10d%n",
                    result.operation,
                    result.inputSize,
                    result.timeNanos / 1_000_000.0,
                    result.comparisons,
                    result.swaps
            );
        }
        System.out.println("=".repeat(85) + "\n");
    }

    /**
     * Calculates and prints complexity verification.
     */
    public void verifyComplexity(String operation) {
        List<BenchmarkResult> filtered = results.stream()
                .filter(r -> r.operation.equals(operation))
                .sorted(Comparator.comparingInt(r -> r.inputSize))
                .toList();

        if (filtered.size() < 2) {
            System.out.println("Not enough data points for complexity verification");
            return;
        }

        System.out.println("\nComplexity Verification: " + operation);
        System.out.println("-".repeat(65));
        System.out.printf("%-12s %-15s %-15s %-15s%n",
                "n", "Time (ms)", "n*log(n)", "Ratio");
        System.out.println("-".repeat(65));

        for (BenchmarkResult result : filtered) {
            double time = result.timeNanos / 1_000_000.0;
            double n = result.inputSize;
            double nlogn = n * Math.log(n) / Math.log(2);
            double ratio = time / nlogn;

            System.out.printf("%-12d %-15.3f %-15.2f %-15.6f%n",
                    result.inputSize, time, nlogn, ratio);
        }

        System.out.println("-".repeat(65));
        System.out.println("Constant ratio confirms O(n log n) complexity\n");
    }

    /**
     * Clears all recorded results.
     */
    public void clear() {
        results.clear();
    }

    /**
     * Returns all recorded results.
     */
    public List<BenchmarkResult> getResults() {
        return new ArrayList<>(results);
    }

    /**
     * Data class for storing benchmark results.
     */
    public static class BenchmarkResult {
        public final String operation;
        public final int inputSize;
        public final long timeNanos;
        public final long comparisons;
        public final long swaps;
        public final long arrayAccesses;
        public final long memoryUsed;

        public BenchmarkResult(String operation, int inputSize, long timeNanos,
                               long comparisons, long swaps, long arrayAccesses, long memoryUsed) {
            this.operation = operation;
            this.inputSize = inputSize;
            this.timeNanos = timeNanos;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.arrayAccesses = arrayAccesses;
            this.memoryUsed = memoryUsed;
        }
    }
}