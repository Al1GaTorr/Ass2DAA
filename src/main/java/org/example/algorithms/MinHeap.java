package org.example.algorithms;
import java.util.*;

public class MinHeap<T extends Comparable<T>> {
    private List<T> heap;
    private Map<T, Integer> indexMap; // For O(1) decrease-key lookup

    // Performance metrics
    private long comparisons;
    private long swaps;
    private long arrayAccesses;

    public MinHeap() {
        this.heap = new ArrayList<>();
        this.indexMap = new HashMap<>();
        resetMetrics();
    }

    public MinHeap(T[] array) {
        this();
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }

        // Copy elements
        for (T element : array) {
            if (element == null) {
                throw new IllegalArgumentException("Null elements not allowed");
            }
            heap.add(element);
            indexMap.put(element, heap.size() - 1);
        }

        // Bottom-up heapify: start from last non-leaf node
        for (int i = parent(heap.size() - 1); i >= 0; i--) {
            heapifyDown(i);
        }
    }

    public void insert(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot insert null element");
        }

        heap.add(element);
        int index = heap.size() - 1;
        indexMap.put(element, index);
        arrayAccesses++;

        heapifyUp(index);
    }

    public T extractMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }

        T min = heap.get(0);
        arrayAccesses++;

        T last = heap.remove(heap.size() - 1);
        indexMap.remove(min);

        if (!isEmpty()) {
            heap.set(0, last);
            indexMap.put(last, 0);
            arrayAccesses++;
            heapifyDown(0);
        }

        return min;
    }

    public T peekMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        arrayAccesses++;
        return heap.get(0);
    }

    public void decreaseKey(T oldValue, T newValue) {
        if (oldValue == null || newValue == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }

        if (newValue.compareTo(oldValue) >= 0) {
            comparisons++;
            throw new IllegalArgumentException("New value must be smaller than old value");
        }
        comparisons++;

        Integer index = indexMap.get(oldValue);
        if (index == null) {
            throw new NoSuchElementException("Element not found in heap");
        }

        // Replace the value and update index map
        heap.set(index, newValue);
        indexMap.remove(oldValue);
        indexMap.put(newValue, index);
        arrayAccesses++;

        // Since we decreased the value, we only need to heapify up
        heapifyUp(index);
    }

    public void merge(MinHeap<T> other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot merge with null heap");
        }

        if (other.isEmpty()) {
            return;
        }

        // Add all elements from other heap
        for (T element : other.heap) {
            heap.add(element);
            indexMap.put(element, heap.size() - 1);
            arrayAccesses++;
        }

        // Rebuild heap using bottom-up heapify
        for (int i = parent(heap.size() - 1); i >= 0; i--) {
            heapifyDown(i);
        }
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIdx = parent(index);
            arrayAccesses += 2;
            comparisons++;

            if (heap.get(index).compareTo(heap.get(parentIdx)) < 0) {
                swap(index, parentIdx);
                index = parentIdx;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();

        while (leftChild(index) < size) {
            int smallest = index;
            int left = leftChild(index);
            int right = rightChild(index);

            arrayAccesses++;
            comparisons++;
            if (left < size && heap.get(left).compareTo(heap.get(smallest)) < 0) {
                smallest = left;
            }

            arrayAccesses++;
            comparisons++;
            if (right < size && heap.get(right).compareTo(heap.get(smallest)) < 0) {
                smallest = right;
            }

            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);

        // Update index map
        indexMap.put(heap.get(i), i);
        indexMap.put(heap.get(j), j);

        swaps++;
        arrayAccesses += 4; // 2 reads, 2 writes
    }

    // Helper methods for array-based heap indexing
    private int parent(int i) { return (i - 1) / 2; }
    private int leftChild(int i) { return 2 * i + 1; }
    private int rightChild(int i) { return 2 * i + 2; }

    public boolean isEmpty() { return heap.isEmpty(); }
    public int size() { return heap.size(); }

    // Metrics methods
    public void resetMetrics() {
        this.comparisons = 0;
        this.swaps = 0;
        this.arrayAccesses = 0;
    }

    public long getComparisons() { return comparisons; }
    public long getSwaps() { return swaps; }
    public long getArrayAccesses() { return arrayAccesses; }


    public List<T> getHeapArray() {
        return new ArrayList<>(heap);
    }

    public boolean isValidHeap() {
        for (int i = 0; i < heap.size(); i++) {
            int left = leftChild(i);
            int right = rightChild(i);

            if (left < heap.size() && heap.get(i).compareTo(heap.get(left)) > 0) {
                return false;
            }
            if (right < heap.size() && heap.get(i).compareTo(heap.get(right)) > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return heap.toString();
    }
}
