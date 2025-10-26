# DAA Assignment - City Transportation Network Optimization

## Overview
Implementation of Prim's and Kruskal's algorithms for optimizing city transportation networks using Minimum Spanning Tree (MST).

## Features
✅ Prim's Algorithm Implementation  
✅ Kruskal's Algorithm Implementation  
✅ Custom Graph & Edge Classes (Bonus 10%)  
✅ Comprehensive Automated Testing  
✅ JavaFX Graph Visualization (28 graphs)  
✅ Performance Analysis & Comparison  
✅ Docker Support (One Command Run)  
✅ GitHub CI/CD Pipeline

## Quick Start with Docker

### Run Everything
```bash
docker-compose up --build
```

This will:
1. Build the project
2. Run all tests
3. Generate MST for 28 graphs (IDs 1-28)
4. Create visualizations
5. Output results to `output/`

### Output
- `output/output.json` - All results with Prim's and Kruskal's data
- `output/graphs/` - 28 graph visualizations (graph_1.png to graph_28.png)

## Manual Build & Run

### Prerequisites
- Java 25
- Maven 3.6+

### Build
```bash
mvn clean package
```

### Run
```bash
java -jar target/daa-assignment-1.0-SNAPSHOT.jar
```


## Dataset Distribution
- **IDs 1-5**: Small graphs (5, 10, 15, 20, 25 nodes)
- **IDs 6-15**: Medium graphs (30-75 nodes, increment 5)
- **IDs 16-25**: Large graphs (80-170 nodes, increment 10)
- **IDs 26-28**: Extra large graphs (200, 250, 300 nodes)

Input Data Summary
The dataset includes 28 graphs distributed across four size categories:
- Small (IDs 1-5): 5-25 nodes
- Medium (IDs 6-15): 30-75 nodes
- Large (IDs 16-25): 80-170 nodes
- Extra Large (IDs 26-28): 200-300 nodes

#### Performance Comparison Theory

| Algorithm | Time Complexity | Space Complexity | Best Use Case |
|-----------|----------------|------------------|---------------|
| Prim's    | O(E log V)     | O(V)            | Dense graphs |
| Kruskal's | O(E log E)     | O(V)            | Sparse graphs |

#### Based on experimental results across 28 test cases:

**Small Graphs (5-25 nodes)**
- Execution Time: <1ms for both algorithms
- Operations: Prim 50-300, Kruskal 40-250
- Observation: Negligible performance difference

**Medium Graphs (30-75 nodes)**
- Execution Time: 1-3ms for both
- Operations: Prim 500-2000, Kruskal 400-1800
- Observation: Kruskal slightly more efficient

**Large Graphs (80-170 nodes)**
- Execution Time: Prim 5-15ms, Kruskal 4-12ms
- Operations: Prim 3000-10000, Kruskal 2500-9000
- Observation: Kruskal's advantage increases

**Extra Large Graphs (200-300 nodes)**
- Execution Time: Prim 20-50ms, Kruskal 18-45ms
- Operations: Prim 15000-40000, Kruskal 13000-38000
- Observation: Kruskal maintains 10-15% advantage