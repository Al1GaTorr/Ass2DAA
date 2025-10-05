# Assignment 2 - MinHeap Implementation

## Description
This project implements a **MinHeap** in Java with the following operations:  

- `insert`  
- `extractMin`  
- `decreaseKey`  
- `heapifyUp` / `heapifyDown`  
- `merge`  

Additionally, it includes:

- **PerformanceTracker**: collects metrics like execution time, number of comparisons, and swaps, and outputs them to CSV.  
- **BenchmarkRunner**: runs automated performance tests and generates plots.

---

## Project Structure
assignment2-minheap/
├── pom.xml
├── README.md
├── docs/
│ ├── analysis-report.pdf
│ └── performance-plots/
│ └── heap_operations_time.png
├── src/
│ ├── main/java/org/example/algorithms/MinHeap.java
│ ├── main/java/org/example/metrics/PerformanceTracker.java
│ └── main/java/org/example/cli/BenchmarkRunner.java
│ └── test/java/org/example/algorithms/MinHeapTest.java
└── .github/workflows/maven.yml

yaml
---

## Installation & Build

1. Clone the repository:

```bash
git clone https://github.com/Al1GaTorr/Ass2DAA.git
cd Ass2DAA
Build the project using Maven:

bash
Копировать код
mvn clean install
Run JUnit tests:

bash
Копировать код
mvn test
Run BenchmarkRunner to generate performance data:

bash
Копировать код
mvn compile exec:java -Dexec.mainClass="org.example.cli.BenchmarkRunner"
Benchmark results will be saved as CSV files:

minheap_benchmark_results_<timestamp>.csv

plot_data_insert.csv

plot_data_extractmin.csv

plot_data_decreasekey.csv

plot_data_merge.csv

```
Benchmark Graph
The following graph shows execution time vs number of elements for all heap operations:
<img width="615" height="757" alt="image" src="https://github.com/user-attachments/assets/f9ecfa73-8cd8-4bc3-95a2-67a90f386f57" />





Observations:


Insert and ExtractMin grow logarithmically with the number of elements.
<img width="1088" height="207" alt="image" src="https://github.com/user-attachments/assets/7a680026-31fd-4c84-a33b-f3f53486a539" />


BuildHeap is faster for large n than repeated inserts.
<img width="290" height="205" alt="image" src="https://github.com/user-attachments/assets/aed2e90a-82e8-4526-b714-51e596182fe4" />

DecreaseKey is extremely fast, almost constant time in these benchmarks.
<img width="291" height="166" alt="image" src="https://github.com/user-attachments/assets/f7c2b2f6-9efe-47aa-bc43-40cab3d34639" />

Merge scales roughly linearly with heap size.

CI/CD
The project includes GitHub Actions (.github/workflows/maven.yml) to automatically build and run tests on push and pull requests.
Green check marks indicate successful builds and passing tests.

Authors
[Ali Qazybai / Al1GaTorr]
