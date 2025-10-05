package org.example.algorithms;

import org.example.algorithms.MinHeap;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive test suite for MinHeap implementation.
 * Covers edge cases, correctness, and performance metrics.
 */
class MinHeapTest {

    private MinHeap<Integer> heap;

    @BeforeEach
    void setUp() {
        heap = new MinHeap<>();
    }

    // ============ Basic Operations Tests ============

    @Test
    @DisplayName("Empty heap should be empty")
    void testEmptyHeap() {
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }

    @Test
    @DisplayName("Insert single element")
    void testInsertSingleElement() {
        heap.insert(5);
        assertFalse(heap.isEmpty());
        assertEquals(1, heap.size());
        assertEquals(5, heap.peekMin());
    }

    @Test
    @DisplayName("Insert multiple elements maintains min at root")
    void testInsertMultipleElements() {
        heap.insert(10);
        heap.insert(5);
        heap.insert(15);
        heap.insert(3);

        assertEquals(3, heap.peekMin());
        assertTrue(heap.isValidHeap());
    }

    @Test
    @DisplayName("Extract min returns elements in sorted order")
    void testExtractMinSortedOrder() {
        int[] values = {15, 10, 20, 8, 25, 5};
        for (int val : values) {
            heap.insert(val);
        }

        List<Integer> extracted = new ArrayList<>();
        while (!heap.isEmpty()) {
            extracted.add(heap.extractMin());
        }

        // Should be in ascending order
        for (int i = 1; i < extracted.size(); i++) {
            assertTrue(extracted.get(i) >= extracted.get(i - 1));
        }
    }

    @Test
    @DisplayName("ExtractMin on empty heap throws exception")
    void testExtractMinEmptyHeap() {
        assertThrows(NoSuchElementException.class, () -> heap.extractMin());
    }

    @Test
    @DisplayName("PeekMin on empty heap throws exception")
    void testPeekMinEmptyHeap() {
        assertThrows(NoSuchElementException.class, () -> heap.peekMin());
    }

    // ============ Edge Cases Tests ============

    @Test
    @DisplayName("Handle duplicate elements")
    void testDuplicateElements() {
        heap.insert(5);
        heap.insert(5);
        heap.insert(5);
        heap.insert(3);

        assertEquals(3, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    @DisplayName("Insert null element throws exception")
    void testInsertNull() {
        assertThrows(IllegalArgumentException.class, () -> heap.insert(null));
    }

    @Test
    @DisplayName("Handle large number of elements")
    void testLargeNumberOfElements() {
        int n = 10000;
        Random rand = new Random(42);

        for (int i = 0; i < n; i++) {
            heap.insert(rand.nextInt(n));
        }

        assertEquals(n, heap.size());
        assertTrue(heap.isValidHeap());

        int prev = heap.extractMin();
        while (!heap.isEmpty()) {
            int curr = heap.extractMin();
            assertTrue(curr >= prev);
            prev = curr;
        }
    }

    // ============ Constructor Tests ============

    @Test
    @DisplayName("Build heap from array (bottom-up heapify)")
    void testBuildHeapFromArray() {
        Integer[] array = {15, 10, 20, 8, 25, 5, 30};
        MinHeap<Integer> builtHeap = new MinHeap<>(array);

        assertEquals(array.length, builtHeap.size());
        assertTrue(builtHeap.isValidHeap());
        assertEquals(5, builtHeap.peekMin());
    }

    @Test
    @DisplayName("Build heap from single element array")
    void testBuildHeapSingleElement() {
        Integer[] array = {42};
        MinHeap<Integer> builtHeap = new MinHeap<>(array);

        assertEquals(1, builtHeap.size());
        assertEquals(42, builtHeap.peekMin());
    }

    @Test
    @DisplayName("Build heap from null array throws exception")
    void testBuildHeapNullArray() {
        assertThrows(IllegalArgumentException.class, () -> new MinHeap<>(null));
    }

    @Test
    @DisplayName("Build heap with null element throws exception")
    void testBuildHeapWithNullElement() {
        Integer[] array = {5, null, 10};
        assertThrows(IllegalArgumentException.class, () -> new MinHeap<>(array));
    }

    // ============ Decrease-Key Tests ============

    @Test
    @DisplayName("Decrease key moves element up correctly")
    void testDecreaseKey() {
        heap.insert(10);
        heap.insert(20);
        heap.insert(30);
        heap.insert(40);

        heap.decreaseKey(40, 5);

        assertEquals(5, heap.peekMin());
        assertTrue(heap.isValidHeap());
    }

    @Test
    @DisplayName("Decrease key with non-existent element throws exception")
    void testDecreaseKeyNonExistent() {
        heap.insert(10);
        heap.insert(20);

        assertThrows(NoSuchElementException.class, () -> heap.decreaseKey(30, 5));
    }

    @Test
    @DisplayName("Decrease key with larger value throws exception")
    void testDecreaseKeyInvalidValue() {
        heap.insert(10);

        assertThrows(IllegalArgumentException.class, () -> heap.decreaseKey(10, 20));
    }

    @Test
    @DisplayName("Decrease key with equal value throws exception")
    void testDecreaseKeyEqualValue() {
        heap.insert(10);

        assertThrows(IllegalArgumentException.class, () -> heap.decreaseKey(10, 10));
    }

    @Test
    @DisplayName("Decrease key with null values throws exception")
    void testDecreaseKeyNull() {
        heap.insert(10);

        assertThrows(IllegalArgumentException.class, () -> heap.decreaseKey(null, 5));
        assertThrows(IllegalArgumentException.class, () -> heap.decreaseKey(10, null));
    }

    // ============ Merge Tests ============

    @Test
    @DisplayName("Merge two heaps")
    void testMerge() {
        heap.insert(10);
        heap.insert(20);
        heap.insert(30);

        MinHeap<Integer> other = new MinHeap<>();
        other.insert(5);
        other.insert(15);
        other.insert(25);

        heap.merge(other);

        assertEquals(6, heap.size());
        assertEquals(5, heap.peekMin());
        assertTrue(heap.isValidHeap());
    }

    @Test
    @DisplayName("Merge with empty heap")
    void testMergeWithEmpty() {
        heap.insert(10);
        heap.insert(20);

        MinHeap<Integer> empty = new MinHeap<>();
        heap.merge(empty);

        assertEquals(2, heap.size());
        assertEquals(10, heap.peekMin());
    }

    @Test
    @DisplayName("Merge empty heap with non-empty")
    void testMergeEmptyWithNonEmpty() {
        MinHeap<Integer> other = new MinHeap<>();
        other.insert(5);
        other.insert(10);

        heap.merge(other);

        assertEquals(2, heap.size());
        assertEquals(5, heap.peekMin());
    }

    @Test
    @DisplayName("Merge with null heap throws exception")
    void testMergeNull() {
        assertThrows(IllegalArgumentException.class, () -> heap.merge(null));
    }

    @Test
    @DisplayName("Merge maintains sorted order when extracting")
    void testMergeSortedExtraction() {
        heap.insert(10);
        heap.insert(30);
        heap.insert(50);

        MinHeap<Integer> other = new MinHeap<>();
        other.insert(20);
        other.insert(40);
        other.insert(60);

        heap.merge(other);

        List<Integer> extracted = new ArrayList<>();
        while (!heap.isEmpty()) {
            extracted.add(heap.extractMin());
        }

        assertEquals(Arrays.asList(10, 20, 30, 40, 50, 60), extracted);
    }

    // ============ Performance Metrics Tests ============

    @Test
    @DisplayName("Metrics are tracked during operations")
    void testMetricsTracking() {
        heap.resetMetrics();

        heap.insert(10);
        heap.insert(5);
        heap.insert(15);

        assertTrue(heap.getComparisons() > 0);
        assertTrue(heap.getSwaps() >= 0);
        assertTrue(heap.getArrayAccesses() > 0);
    }

    @Test
    @DisplayName("Reset metrics clears all counters")
    void testResetMetrics() {
        heap.insert(10);
        heap.insert(5);

        heap.resetMetrics();

        assertEquals(0, heap.getComparisons());
        assertEquals(0, heap.getSwaps());
        assertEquals(0, heap.getArrayAccesses());
    }

    // ============ Heap Property Tests ============

    @Test
    @DisplayName("Heap property maintained after multiple operations")
    void testHeapPropertyPreserved() {
        Random rand = new Random(123);

        for (int i = 0; i < 100; i++) {
            heap.insert(rand.nextInt(1000));
            assertTrue(heap.isValidHeap());
        }

        for (int i = 0; i < 50; i++) {
            heap.extractMin();
            assertTrue(heap.isValidHeap());
        }
    }

    @Test
    @DisplayName("Heap property maintained with reverse-sorted input")
    void testReverseSortedInput() {
        for (int i = 100; i > 0; i--) {
            heap.insert(i);
        }

        assertTrue(heap.isValidHeap());
        assertEquals(1, heap.peekMin());
    }

    @Test
    @DisplayName("Heap property maintained with sorted input")
    void testSortedInput() {
        for (int i = 1; i <= 100; i++) {
            heap.insert(i);
        }

        assertTrue(heap.isValidHeap());
        assertEquals(1, heap.peekMin());
    }

    // ============ Integration Tests ============

    @Test
    @DisplayName("Complex scenario: insert, decrease, extract, merge")
    void testComplexScenario() {
        heap.insert(50);
        heap.insert(40);
        heap.insert(30);
        heap.insert(20);
        heap.insert(10);

        heap.decreaseKey(50, 5);
        assertEquals(5, heap.extractMin());

        MinHeap<Integer> other = new MinHeap<>();
        other.insert(15);
        other.insert(25);

        heap.merge(other);

        assertTrue(heap.isValidHeap());
        assertEquals(6, heap.size());
    }
}
