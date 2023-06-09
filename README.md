# Path Finding Benchmarking Program

## Description

This is a Java project in progress that will be used as a benchmarking program for path-finding algorithms in directed graphs, including Dijkstra, Floyd-Warshall, A\*, Johnson, and Bellman-Ford.
The program measures the execution time in milliseconds of the given algorithm and displays it to console.

## Installation

Simply open the root directory as project and run from within the IDE.

### Requirements

- Java 18 JDK
- IntelliJ IDEA Community/Ultimate IDE
- JGraphT library (included)

> The project can be executed using other IDEs, but might require additional configuration steps.

## Usage

The program will have a command line interface (CLI) for proper usage. However, it's still under development.
As of this moment, the program only measures the running times of **Dijkstra**, **bellman-Ford**, **Floyd-Warshall**, **Johnson** and **A\***.

The algorithm loads all data from the files and runs a demo test on each algorithm and outputs to console the total shortest-path weight from a give source vertex to a target vertex, the shortest path nodes, and the necessary time to compute each individual algorithm.

> **Note** that Johnson and Floyd-Warshall only run on 1/81 of the total graph due to memory constraints, by using an implemented splicing technique to divide the graph into $k^2$ equal sub-squares and select every node and edge contained within it.

### Input Data

The program reads the input data for testing from text files in the `data` directory. One file contains the vertices, and another file contains the weighted edges in the format of `<source node> <destination node> <weight>`, and another contains each individual node's coordinates in a 2D plane `<vertex_label> <x_coordinate> <y_coordinate>`.

## Algorithms

The program implements the following path-finding algorithms:

- Dijkstra
- Floyd-Warshall
- A\*
- Johnson
- Bellman-Ford

## Author

- Alexandru Iacobescu
  - [E-mail](mailto:alexandru.iacobescu01@e-uvt.ro)
  - [GitHub](https://github.com/AlexandruIacobescu)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
