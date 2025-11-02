# Smart City Scheduling

### Graph Algorithms Implementation

**Author:** Bekdaulet Bolatov
**Course:** Design and Analysis of Algorithms — Assignment 4

---

## Overview

This project presents a **comprehensive Java implementation of graph algorithms** designed for **task dependency management** and **scheduling optimization** in **Smart City** applications.
Three main algorithms for directed graph analysis are implemented and benchmarked.

| Algorithm                              | Purpose                                  | Complexity |
| -------------------------------------- | ---------------------------------------- | ---------- |
| **Tarjan’s SCC Detection**             | Identifies strongly connected components | O(V + E)   |
| **Topological Sorting (Kahn’s + DFS)** | Orders tasks respecting dependencies     | O(V + E)   |
| **DAG Shortest/Longest Paths**         | Computes optimal and critical paths      | O(V + E)   |

---

## Build & Run Instructions

**Build the project:**

```bash
mvn clean compile
```

**Run tests (6 classes, 45 tests, 100% pass rate):**

```bash
mvn test
```

**Execute the main application:**

```bash
mvn exec:java -Dexec.mainClass="com.SmartCity.Main"
```

---

## Test Results

| Test Class              | Tests Run | Failures | Errors | Skipped |
| ----------------------- | --------- | -------- | ------ | ------- |
| DFSTopologicalSortTest  | 8         | 0        | 0      | 0       |
| ComponentTSTest         | 7         | 0        | 0      | 0       |
| SCCTest                 | 8         | 0        | 0      | 0       |
| KahnTopologicalSortTest | 7         | 0        | 0      | 0       |
| DAGTest                 | 6         | 0        | 0      | 0       |
| CondensationGraphTest   | 9         | 0        | 0      | 0       |

**Total:** 45 tests — *All Passed.*

---

## Project Structure

```
src/main/java/com/SmartCity/
├── algorithms/
│   ├── SCC.java                # Tarjan’s algorithm
│   ├── CondensationGraph.java  # DAG construction from SCCs
│   ├── KahnTopologicalSort.java# BFS-based topological sort
│   ├── DFSTopologicalSort.java # DFS-based topological sort
│   ├── ComponentTS.java        # Component-level sorting
│   └── DAG.java                # Shortest/longest path algorithms
├── model/
│   └── Graph.java              # Weighted graph representation
└── utils/
    ├── GraphLoader.java        # JSON graph loader
    └── Metrics.java            # Performance tracking
```

**Additional:**

* `src/test/java/` — JUnit test suite
* `input/` — 9 datasets in JSON format

---

## Datasets

* **Small (6–8 vertices):** Basic cycle and DAG validation
* **Medium (10–18 vertices):** Multiple SCCs and mixed structures
* **Large (25–40 vertices):** Complex dependency graphs for performance testing

All edges include weights representing **task duration (1–7 hours)**.

---

## Algorithm Details

### 1. Strongly Connected Components (Tarjan’s)

* **Complexity:** O(V + E) time, O(V) space
* **Features:** Single-pass DFS, cycle detection, condensation graph building
* **Use Case:** Detect circular dependencies in scheduling

### 2. Topological Sorting

* **Kahn’s Algorithm:** BFS with in-degree tracking and explicit cycle detection
* **DFS-based Algorithm:** Post-order traversal, memory-efficient
* **Use Case:** Determine valid task execution order

### 3. DAG Shortest & Longest Paths

* **Shortest Path:** Minimum completion time
* **Longest Path:** Critical path for project duration
* **Advantage:** Handles negative weights, no priority queue needed

---

## Performance Results

| Dataset | Vertices | SCC Ops | Topo Ops | Total Time (ms) |
| ------- | -------- | ------- | -------- | --------------- |
| Small   | 7–10     | 21–31   | 14–30    | 0.082–0.285     |
| Medium  | 14–19    | 44–59   | 23–59    | 0.106–0.161     |
| Large   | 28–45    | 86–141  | 53–140   | 0.134–0.296     |

**Key Findings:**

* Linear scaling confirmed (~3 ops per vertex)
* Condensation greatly reduces graph complexity
* Dense graphs increase operations but retain O(V + E) scaling
* Critical path length correlates with graph depth

---

## Smart City Applications

### Street Maintenance Scheduling

* Detect route dependencies (SCC)
* Order maintenance tasks (Topological sort)
* Identify critical maintenance paths (Longest path)

### Sensor Network Deployment

* Optimize activation sequence
* Minimize deployment time
* Handle circular sensor dependencies

### Infrastructure Project Planning

* Calculate minimum completion time
* Identify bottleneck tasks
* Optimize resource allocation

---

## Testing Overview

Comprehensive coverage across algorithms and scenarios:

* **SCCTest:** Cycle detection, multi-component graphs
* **CondensationGraphTest:** DAG validation, edge deduplication
* **TopologicalSortTests:** Kahn + DFS, disconnected graphs
* **DAGTest:** Shortest/longest path reconstruction, critical path analysis

---

## Key Features

✓ Edge-weighted graphs for realistic durations
✓ Automatic cycle detection
✓ Task-level and component-level topological ordering
✓ Critical path identification
✓ Performance tracking (operations, execution time)
✓ JSON-based dataset customization

---

## Implementation Highlights

* No external dependencies except **Gson (JSON)** and **JUnit (testing)**
* Clear modular design: algorithms, models, and utilities separated
* Robust error handling for edge cases
* Optimized adjacency-list structures
* Detailed documentation and complexity notes

---

## Conclusion

This project demonstrates efficient and scalable implementations of graph algorithms for **smart city scheduling**.

* **Tarjan’s SCC** excels at dependency and cycle analysis.
* **Topological sorting** ensures correct execution order.
* **DAG shortest/longest path** provides optimal scheduling insights for time-critical applications.

Together, these components form a foundation for **intelligent urban scheduling systems** that are both efficient and adaptable.

**Thank you for your attention!**
