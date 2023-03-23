# Path Finding Benchmarking Program

## Description

This is a Java project in progress that will be used as a benchmarking program for path-finding algorithms in directed graphs, including Dijkstra, Floyd-Warshall, A*, Johnson, and Bellman-Ford.
The program measures the execution time in milliseconds of the given algorithm and displays it to console.

## Installation
Simply open the root directory as project and run from within the IDE.
### Requirements

- Java 18 JDK
- IntelliJ IDEA Community/Ultimate IDE

> The project can be executed using other IDEs, but might require additional configuration steps.


## Usage

The program will have a command line interface (CLI) for proper usage. However, it's still under development.
As of this moment, the program only measures the Dijkstra algorithm for shortest path, on the graph provided in the `data` directory.
The only way to customize the use case is to modify the `vertices.txt` file in the `data` directory and the `weighted_edges.txt` file, and also the starting node and sink node from within the code (in the `main` method).

### Input Data

The program reads the input data for testing from text files in the `data` directory. One file contains the vertices, and another file contains the weighted edges in the format of `<source node> <destination node> <weight>`.

## Algorithms

The program will implement the following path-finding algorithms:

- Dijkstra (**currently available**)
- Floyd-Warshall
- A*
- Johnson
- Bellman-Ford

## Author
- Alexandru Iacobescu
  - [E-mail](mailto:alexandru.iacobescu01@e-uvt.ro)
  - [GitHub](https://github.com/AlexandruIacobescu)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
