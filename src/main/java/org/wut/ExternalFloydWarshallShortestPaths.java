package org.wut;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.VertexDegreeComparator;
import org.jgrapht.util.CollectionUtil;
import org.jgrapht.util.TypeUtil;

import java.io.*;
import java.util.*;

public class ExternalFloydWarshallShortestPaths<V, E> {
    private final Graph<V, E> graph;
    private final List<V> vertices;
    private final List<Integer> degrees;
    private final Map<V, Integer> vertexIndices;
    private final int minDegreeOne;
    private final int minDegreeTwo;

    private final String matrixFilePath;
    private final int chunkSize;
    private final int matrixSize;

    public ExternalFloydWarshallShortestPaths(Graph<V, E> graph, String matrixFilePath, int chunkSize) {
        this.graph = graph;
        this.matrixFilePath = matrixFilePath;
        this.chunkSize = chunkSize;
        this.matrixSize = graph.vertexSet().size();

        this.vertices = new ArrayList<>(graph.vertexSet());
        Collections.sort(vertices, VertexDegreeComparator.of(graph));
        this.degrees = new ArrayList<>();
        this.vertexIndices = CollectionUtil.newHashMapWithExpectedSize(this.vertices.size());

        int i = 0;
        int minDegreeOne = vertices.size();
        int minDegreeTwo = vertices.size();
        for (V vertex : vertices) {
            vertexIndices.put(vertex, i);
            int degree = graph.degreeOf(vertex);
            degrees.add(degree);

            if (degree > 1) {
                if (i < minDegreeOne) {
                    minDegreeOne = i;
                }
                if (i < minDegreeTwo) {
                    minDegreeTwo = i;
                }
            } else if (i < minDegreeOne && degree == 1) {
                minDegreeOne = i;
            }

            ++i;
        }
        this.minDegreeOne = minDegreeOne;
        this.minDegreeTwo = minDegreeTwo;
    }

    public void calculateShortestPaths() throws IOException {
        // Initialize the matrix on disk
        initializeMatrix();

        int n = vertices.size();
        int[][] matrix = new int[n][n];

        // Process the matrix in chunks
        for (int chunk = 0; chunk < n; chunk += chunkSize) {
            int chunkEnd = Math.min(chunk + chunkSize, n);

            // Load chunk from disk
            loadMatrixChunk(matrix, chunk, chunkEnd);

            // Perform Floyd-Warshall algorithm on the chunk
            for (int k = minDegreeTwo; k < n; k++) {
                for (int i = minDegreeOne; i < n; i++) {
                    if (i == k) {
                        continue;
                    }
                    for (int j = minDegreeOne; j < n; j++) {
                        if (i == j || j == k) {
                            continue;
                        }

                        int ik_kj = matrix[i][k] + matrix[k][j];
                        if (ik_kj < matrix[i][j]) {
                            matrix[i][j] = ik_kj;
                        }
                    }
                }
            }

            // Save the updated chunk back to disk
            saveMatrixChunk(matrix, chunk, chunkEnd);
        }
    }

    public int getShortestPathDistance(V source, V target) throws IOException {
        int sourceIndex = vertexIndices.get(source);
        int targetIndex = vertexIndices.get(target);

        // Load the specific cells from disk
        int distance = loadMatrixCell(sourceIndex, targetIndex);
        return distance;
    }

    private void initializeMatrix() throws IOException {
        int n = vertices.size();
        int[][] matrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(matrix[i], Integer.MAX_VALUE / 2);
            matrix[i][i] = 0;
        }

        // Save the initial matrix to disk
        saveMatrix(matrix);
    }

    private void loadMatrixChunk(int[][] matrix, int chunkStart, int chunkEnd) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(matrixFilePath))) {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                if (row < chunkStart || row >= chunkEnd) {
                    row++;
                    continue;
                }

                String[] cells = line.split(",");
                for (int col = chunkStart; col < chunkEnd; col++) {
                    matrix[row][col] = Integer.parseInt(cells[col - chunkStart]);
                }

                row++;
            }
        }
    }

    private int loadMatrixCell(int row, int col) throws IOException {
        int cellValue = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(matrixFilePath))) {
            String line;
            int currentRow = 0;

            while ((line = reader.readLine()) != null) {
                if (currentRow != row) {
                    currentRow++;
                    continue;
                }

                String[] cells = line.split(",");
                cellValue = Integer.parseInt(cells[col]);
                break;
            }
        }

        return cellValue;
    }

    private void saveMatrixChunk(int[][] matrix, int chunkStart, int chunkEnd) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(matrixFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(matrixFilePath + ".tmp"))) {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                if (row < chunkStart || row >= chunkEnd) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int col = 0; col < chunkStart; col++) {
                        sb.append(matrix[row][col]).append(",");
                    }
                    for (int col = chunkStart; col < chunkEnd; col++) {
                        sb.append(matrix[row][col]).append(",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    writer.write(sb.toString());
                    writer.newLine();
                }

                row++;
            }
        }

        // Rename the temporary file to replace the original matrix file
        File tempFile = new File(matrixFilePath + ".tmp");
        File originalFile = new File(matrixFilePath);
        originalFile.delete();
        tempFile.renameTo(originalFile);
    }

    private void saveMatrix(int[][] matrix) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(matrixFilePath))) {
            for (int[] row : matrix) {
                StringBuilder sb = new StringBuilder();
                for (int cell : row) {
                    sb.append(cell).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }
}
