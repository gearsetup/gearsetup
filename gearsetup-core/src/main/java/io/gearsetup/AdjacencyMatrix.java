package io.gearsetup;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * A utility class providing memory-efficient methods of filling buffers with the
 * <a href="https://en.wikipedia.org/wiki/Adjacency_matrix">Adjacency matrix</a> of a collection of values.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@UtilityClass
public class AdjacencyMatrix {
    /**
     * Fills the provided adjacency buffer with the <a href="https://en.wikipedia.org/wiki/Adjacency_matrix">adjacency matrix</a>
     * of the specified values.
     * <p>
     * Each cell {@code (i, j)} in the buffer for values at index {@code i} and {@code j} will be set to {@code true} if
     * the values <b>are not</b> the same and if they are considering to be adjacent by the provided intersection
     * criteria.
     * <p>
     * The adjacency buffer is expected to be as large or larger than the collection of values. Otherwise, an
     * {@link ArrayIndexOutOfBoundsException} will be thrown for any adjacent values whose index is out of range of the buffer.
     *
     * @param values          the values to calculate the adjacency matrix
     * @param criteria        the criteria in which two values are adjacent
     * @param adjacencyBuffer the buffer to store the adjacency matrix
     * @param <T>             the type of vertex in the adjacency matrix
     * @throws ArrayIndexOutOfBoundsException if the adjacency buffer is not large enough
     */
    public <T> void fill(@NonNull List<T> values,
                         @NonNull BiPredicate<T, T> criteria,
                         @NonNull boolean[][] adjacencyBuffer) {
        int size = values.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                //i and j are connected if they are not the same and meet the adjacency criteria
                adjacencyBuffer[i][j] = i != j && criteria.test(values.get(i), values.get(j));
            }
        }
    }

    /**
     * Fills the provided adjacency buffer with the <a href="https://en.wikipedia.org/wiki/Adjacency_matrix">adjacency matrix</a>
     * of the specified values while also keeping track of the total number of neighbors for each vertex and storing it
     * in the count buffer.
     * <p>
     * Each cell {@code (i, j)} in the buffer for values at index {@code i} and {@code j} will be set to {@code true} if
     * the values <b>are not</b> the same and if they are considering to be adjacent by the provided intersection
     * criteria.
     * For each value at index {@code i}, the total number of neighbors is stored in the count buffer at index {@code i}.
     * <p>
     * The adjacency and count buffers are expected to be as large or larger than the collection of values. Otherwise, an
     * {@link ArrayIndexOutOfBoundsException} will be thrown for any values whose index is out of range of the buffer.
     *
     * @param values          the values to calculate the adjacency matrix
     * @param criteria        the criteria in which two values are adjacent
     * @param adjacencyBuffer the buffer to store the adjacency matrix
     * @param countBuffer     the buffer to store the neighbor counts
     * @param <T>             the type of vertex in the adjacency matrix
     * @throws ArrayIndexOutOfBoundsException if the adjacency or count buffer are not large enough
     */
    public <T> void fillWithCounts(@NonNull List<T> values,
                                   @NonNull BiPredicate<T, T> criteria,
                                   @NonNull boolean[][] adjacencyBuffer,
                                   @NonNull int[] countBuffer) {
        int size = values.size();
        for (int i = 0; i < size; i++) {
            int count = 0;
            for (int j = 0; j < size; j++) {
                //i and j are adjacent if they are not the same and meet the adjacency criteria
                boolean neighbors = i != j && criteria.test(values.get(i), values.get(j));
                adjacencyBuffer[i][j] = neighbors;
                if (neighbors) {
                    count++;
                }
            }
            countBuffer[i] = count;
        }
    }
}
