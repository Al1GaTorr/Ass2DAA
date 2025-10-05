package org.example.cli;

import org.example.algorithms.MinHeap;
import org.example.metrics.PerformanceTracker;
import  org.example.metrics.PerformanceTracker.BenchmarkResult;

import java.io.IOException;
import java.util.*;

/**
 * Command-line interface for benchmarking MinHeap operations.
 */
public class BenchmarkRunner {

    private static final int[] DEFAULT_SIZES = {100, 1000, 5000, 10000, 50000, 100000};
    private static final int WARMUP_ITERATIONS = 5;
    private static final int BENCHMARK_ITERATIONS = 10;

    public static void main(String[] args) {
        BenchmarkRunner runner = new BenchmarkRunner();

        if (args.length > 0 && args[0].equals("--help")) {
            printUsage();
            return;
        }

        try {
            runner.runFullBenchmarkSuite();
        } catch (Exception e) {
            System.err.println("Error running benchmarks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Runs the complete benchmark suite.
     */
    public void runFullBenchmarkSuite() throws IOException {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         MinHeap Performance Benchmark Suite                ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        PerformanceTracker tracker = new PerformanceTracker();

        // Benchmark different operations
        System.out.println("Running benchmarks...\n");

        benchmarkInsertOperations(tracker);
        benchmarkExtractMinOperations(tracker);
        benchmarkBuildHeapOperations(tracker);
        benchmarkDecreaseKeyOperations(tracker);
        benchmarkMergeOperations(tracker);

        // Print and export results
        tracker.printSummary();
        tracker.verifyComplexity("Insert");
        tracker.verifyComplexity("ExtractMin");
        tracker.verifyComplexity("BuildHeap");

        String csvFile = "minheap_benchmark_results.csv";
        tracker.exportToCSV(csvFile);
        System.out.println("Results exported to: " + csvFile);

        // Generate performance plots data
        generatePlotData(tracker);
    }

    /**
     * Benchmarks insert operations.
     */
    private void benchmarkInsertOperations(PerformanceTracker tracker) {
        System.out.println("Benchmarking Insert Operations...");

        for (int size : DEFAULT_SIZES) {
            MinHeap<Integer> heap = new MinHeap<>();
            Random rand = new Random(42);

            // Warmup
            for (int w = 0; w < WARMUP_ITERATIONS; w++) {
                MinHeap<Integer> warmupHeap = new MinHeap<>();
                for (int i = 0; i < size; i++) {
                    warmupHeap.insert(rand.nextInt(size * 10));
                }
            }

            // Actual benchmark
            long totalTime = 0;
            long totalComparisons = 0;
            long totalSwaps = 0;
            long totalAccesses = 0;

            for (int iter = 0; iter < BENCHMARK_ITERATIONS; iter++) {
                heap = new MinHeap<>();
                heap.resetMetrics();
                rand = new Random(42 + iter);

                long startTime = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    heap.insert(rand.nextInt(size * 10));
                }
                long endTime = System.nanoTime();

                totalTime += (endTime - startTime);
                totalComparisons += heap.getComparisons();
                totalSwaps += heap.getSwaps();
                totalAccesses += heap.getArrayAccesses();
            }

            long avgTime = totalTime / BENCHMARK_ITERATIONS;
            long avgComparisons = totalComparisons / BENCHMARK_ITERATIONS;
            long avgSwaps = totalSwaps / BENCHMARK_ITERATIONS;
            long avgAccesses = totalAccesses / BENCHMARK_ITERATIONS;
            long memory = estimateMemoryUsage(size);

            tracker.recordResult(new BenchmarkResult(
                    "Insert", size, avgTime, avgComparisons, avgSwaps, avgAccesses, memory
            ));

            System.out.printf("  n=%6d: %.3f ms%n", size, avgTime / 1_000_000.0);
        }
        System.out.println();
    }

    /**
     * Benchmarks extractMin operations.
     */
    private void benchmarkExtractMinOperations(PerformanceTracker tracker) {
        System.out.println("Benchmarking ExtractMin Operations...");

        for (int size : DEFAULT_SIZES) {
            Random rand = new Random(42);

            // Warmup
            for (int w = 0; w < WARMUP_ITERATIONS; w++) {
                MinHeap<Integer> warmupHeap = new MinHeap<>();
                for (int i = 0; i < size; i++) {
                    warmupHeap.insert(rand.nextInt(size * 10));
                }
                while (!warmupHeap.isEmpty()) {
                    warmupHeap.extractMin();
                }
            }

            // Actual benchmark
            long totalTime = 0;
            long totalComparisons = 0;
            long totalSwaps = 0;
            long totalAccesses = 0;

            for (int iter = 0; iter < BENCHMARK_ITERATIONS; iter++) {
                MinHeap<Integer> heap = new MinHeap<>();
                rand = new Random(42 + iter);
                for (int i = 0; i < size; i++) {
                    heap.insert(rand.nextInt(size * 10));
                }

                heap.resetMetrics();
                long startTime = System.nanoTime();
                while (!heap.isEmpty()) {
                    heap.extractMin();
                }
                long endTime = System.nanoTime();

                totalTime += (endTime - startTime);
                totalComparisons += heap.getComparisons();
                totalSwaps += heap.getSwaps();
                totalAccesses += heap.getArrayAccesses();
            }

            long avgTime = totalTime / BENCHMARK_ITERATIONS;
            long avgComparisons = totalComparisons / BENCHMARK_ITERATIONS;
            long avgSwaps = totalSwaps / BENCHMARK_ITERATIONS;
            long avgAccesses = totalAccesses / BENCHMARK_ITERATIONS;
            long memory = estimateMemoryUsage(size);

            tracker.recordResult(new BenchmarkResult(
                    "ExtractMin", size, avgTime, avgComparisons, avgSwaps, avgAccesses, memory
            ));

            System.out.printf("  n=%6d: %.3f ms%n", size, avgTime / 1_000_000.0);
        }
        System.out.println();
    }

    /**
     * Benchmarks buildHeap (bottom-up heapify) operations.
     */
    private void benchmarkBuildHeapOperations(PerformanceTracker tracker) {
        System.out.println("Benchmarking BuildHeap Operations...");

        for (int size : DEFAULT_SIZES) {
            Random rand = new Random(42);

            // Warmup
            for (int w = 0; w < WARMUP_ITERATIONS; w++) {
                Integer[] arr = generateRandomArray(size, rand);
                new MinHeap<>(arr);
            }

            // Actual benchmark
            long totalTime = 0;
            long totalComparisons = 0;
            long totalSwaps = 0;
            long totalAccesses = 0;

            for (int iter = 0; iter < BENCHMARK_ITERATIONS; iter++) {
                rand = new Random(42 + iter);
                Integer[] arr = generateRandomArray(size, rand);

                long startTime = System.nanoTime();
                MinHeap<Integer> heap = new MinHeap<>(arr);
                long endTime = System.nanoTime();

                totalTime += (endTime - startTime);
                totalComparisons += heap.getComparisons();
                totalSwaps += heap.getSwaps();
                totalAccesses += heap.getArrayAccesses();
            }

            long avgTime = totalTime / BENCHMARK_ITERATIONS;
            long avgComparisons = totalComparisons / BENCHMARK_ITERATIONS;
            long avgSwaps = totalSwaps / BENCHMARK_ITERATIONS;
            long avgAccesses = totalAccesses / BENCHMARK_ITERATIONS;
            long memory = estimateMemoryUsage(size);

            tracker.recordResult(new BenchmarkResult(
                    "BuildHeap", size, avgTime, avgComparisons, avgSwaps, avgAccesses, memory
            ));

            System.out.printf("  n=%6d: %.3f ms%n", size, avgTime / 1_000_000.0);
        }
        System.out.println();
    }

    /**
     * Benchmarks decreaseKey operations.
     */
    private void benchmarkDecreaseKeyOperations(PerformanceTracker tracker) {
        System.out.println("Benchmarking DecreaseKey Operations...");

        for (int size : DEFAULT_SIZES) {
            if (size > 10000) continue; // DecreaseKey is expensive with current implementation

            Random rand = new Random(42);

            // Warmup
            for (int w = 0; w < WARMUP_ITERATIONS; w++) {
                MinHeap<Integer> warmupHeap = new MinHeap<>();
                for (int i = 0; i < size; i++) {
                    warmupHeap.insert(i * 10);
                }
                for (int i = 0; i < size / 10; i++) {
                    int oldVal = rand.nextInt(size) * 10;
                    int newVal = oldVal - 5;
                    try {
                        warmupHeap.decreaseKey(oldVal, newVal);
                    } catch (Exception e) {
                        // Element might not exist
                    }
                }
            }

            // Actual benchmark
            long totalTime = 0;
            long totalComparisons = 0;
            long totalSwaps = 0;
            long totalAccesses = 0;
            int operations = size / 10;

            for (int iter = 0; iter < BENCHMARK_ITERATIONS; iter++) {
                MinHeap<Integer> heap = new MinHeap<>();
                for (int i = 0; i < size; i++) {
                    heap.insert(i * 10);
                }

                rand = new Random(42 + iter);
                heap.resetMetrics();

                long startTime = System.nanoTime();
                for (int i = 0; i < operations; i++) {
                    int oldVal = rand.nextInt(size) * 10;
                    int newVal = oldVal - 5;
                    try {
                        heap.decreaseKey(oldVal, newVal);
                    } catch (Exception e) {
                        // Element might not exist or invalid decrease
                    }
                }
                long endTime = System.nanoTime();

                totalTime += (endTime - startTime);
                totalComparisons += heap.getComparisons();
                totalSwaps += heap.getSwaps();
                totalAccesses += heap.getArrayAccesses();
            }

            long avgTime = totalTime / BENCHMARK_ITERATIONS;
            long avgComparisons = totalComparisons / BENCHMARK_ITERATIONS;
            long avgSwaps = totalSwaps / BENCHMARK_ITERATIONS;
            long avgAccesses = totalAccesses / BENCHMARK_ITERATIONS;
            long memory = estimateMemoryUsage(size);

            tracker.recordResult(new BenchmarkResult(
                    "DecreaseKey", size, avgTime, avgComparisons, avgSwaps, avgAccesses, memory
            ));

            System.out.printf("  n=%6d: %.3f ms (%d operations)%n",
                    size, avgTime / 1_000_000.0, operations);
        }
        System.out.println();
    }

    /**
     * Benchmarks merge operations.
     */
    private void benchmarkMergeOperations(PerformanceTracker tracker) {
        System.out.println("Benchmarking Merge Operations...");

        for (int size : DEFAULT_SIZES) {
            Random rand = new Random(42);
            int halfSize = size / 2;

            // Warmup
            for (int w = 0; w < WARMUP_ITERATIONS; w++) {
                MinHeap<Integer> h1 = new MinHeap<>();
                MinHeap<Integer> h2 = new MinHeap<>();
                for (int i = 0; i < halfSize; i++) {
                    h1.insert(rand.nextInt(size * 10));
                    h2.insert(rand.nextInt(size * 10));
                }
                h1.merge(h2);
            }

            // Actual benchmark
            long totalTime = 0;
            long totalComparisons = 0;
            long totalSwaps = 0;
            long totalAccesses = 0;

            for (int iter = 0; iter < BENCHMARK_ITERATIONS; iter++) {
                MinHeap<Integer> heap1 = new MinHeap<>();
                MinHeap<Integer> heap2 = new MinHeap<>();
                rand = new Random(42 + iter);

                for (int i = 0; i < halfSize; i++) {
                    heap1.insert(rand.nextInt(size * 10));
                    heap2.insert(rand.nextInt(size * 10));
                }

                heap1.resetMetrics();
                long startTime = System.nanoTime();
                heap1.merge(heap2);
                long endTime = System.nanoTime();

                totalTime += (endTime - startTime);
                totalComparisons += heap1.getComparisons();
                totalSwaps += heap1.getSwaps();
                totalAccesses += heap1.getArrayAccesses();
            }

            long avgTime = totalTime / BENCHMARK_ITERATIONS;
            long avgComparisons = totalComparisons / BENCHMARK_ITERATIONS;
            long avgSwaps = totalSwaps / BENCHMARK_ITERATIONS;
            long avgAccesses = totalAccesses / BENCHMARK_ITERATIONS;
            long memory = estimateMemoryUsage(size);

            tracker.recordResult(new BenchmarkResult(
                    "Merge", size, avgTime, avgComparisons, avgSwaps, avgAccesses, memory
            ));

            System.out.printf("  n=%6d: %.3f ms (merging %d+%d)%n",
                    size, avgTime / 1_000_000.0, halfSize, halfSize);
        }
        System.out.println();
    }

    /**
     * Generates random integer array.
     */
    private Integer[] generateRandomArray(int size, Random rand) {
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(size * 10);
        }
        return arr;
    }

    /**
     * Estimates memory usage for heap of given size.
     */
    private long estimateMemoryUsage(int size) {
        // Rough estimate: ArrayList overhead + references + HashMap
        // Object reference: 8 bytes (compressed OOP)
        // ArrayList: ~40 bytes overhead + size * 8 bytes
        // HashMap: ~64 bytes overhead + size * 32 bytes (entry overhead)
        return 40 + (size * 8) + 64 + (size * 32);
    }

    /**
     * Generates data files for plotting.
     */
    private void generatePlotData(PerformanceTracker tracker) throws IOException {
        Map<String, List<BenchmarkResult>> byOperation = new HashMap<>();

        for (BenchmarkResult result : tracker.getResults()) {
            byOperation.computeIfAbsent(result.operation, k -> new ArrayList<>())
                    .add(result);
        }

        for (Map.Entry<String, List<BenchmarkResult>> entry : byOperation.entrySet()) {
            String operation = entry.getKey();
            String filename = "plot_data_" + operation.toLowerCase() + ".csv";

            try (java.io.PrintWriter writer = new java.io.PrintWriter(filename)) {
                writer.println("n,time_ms,nlogn");

                for (BenchmarkResult result : entry.getValue()) {
                    double timeMs = result.timeNanos / 1_000_000.0;
                    double nlogn = result.inputSize *
                            Math.log(result.inputSize) / Math.log(2);
                    writer.printf("%d,%.3f,%.2f%n",
                            result.inputSize, timeMs, nlogn);
                }
            }

            System.out.println("Plot data saved to: " + filename);
        }
    }

    /**
     * Prints usage information.
     */
    private static void printUsage() {
        System.out.println("MinHeap Benchmark Runner");
        System.out.println("========================");
        System.out.println();
        System.out.println("Usage: java cli.BenchmarkRunner [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --help    Show this help message");
        System.out.println();
        System.out.println("The benchmark suite will:");
        System.out.println("  1. Run performance tests on various input sizes");
        System.out.println("  2. Measure time, comparisons, swaps, and memory");
        System.out.println("  3. Export results to CSV files");
        System.out.println("  4. Generate data for plotting");
        System.out.println("  5. Verify theoretical complexity");
    }
}