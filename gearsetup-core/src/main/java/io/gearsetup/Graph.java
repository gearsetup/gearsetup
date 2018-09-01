package io.gearsetup;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * An implementation of a <a href="https://en.wikipedia.org/wiki/Graph_(abstract_data_type)">Graph</a> that
 * consists of a set of vertices and an edge predicate.
 * <p>
 * An edge predicate is a {@link BiPredicate} that takes in two vertices and returns {@code true} if there is an edge
 * between the vertices.
 *
 * @param <T> the type of the vertices in the graph
 * @author Ian Caffey
 * @since 1.0
 */
public final class Graph<T> {
    @Getter
    private final Set<T> vertices;
    @Getter
    private final BiPredicate<T, T> edgePredicate;
    private final int[] neighborCount;
    private final int[][] adjacencyList;
    private final BiMap<T, Integer> indices;

    /**
     * Constructs a new {@link Graph} of the specified values using the specified edge predicate for building the graph edges.
     *
     * @param vertices      the vertices of the graph
     * @param edgePredicate the edge predicate for determine if two vertices have an edge
     */
    private Graph(@NonNull Set<T> vertices, @NonNull BiPredicate<T, T> edgePredicate) {
        int size = vertices.size();
        int[] neighborCounts = new int[size];
        int[][] adjacencyList = new int[size][size];
        ImmutableBiMap.Builder<T, Integer> builder = ImmutableBiMap.builder();
        int vertexIndex = 0;
        for (T vertex : vertices) {
            int otherIndex = 0;
            int neighbors = 0;
            for (T other : vertices) {
                if (vertex != other && edgePredicate.test(vertex, other)) {
                    adjacencyList[vertexIndex][neighbors++] = otherIndex;
                }
                otherIndex++;
            }
            neighborCounts[vertexIndex] = neighbors;
            builder.put(vertex, vertexIndex++);
        }
        this.vertices = ImmutableSet.copyOf(vertices);
        this.edgePredicate = edgePredicate;
        this.adjacencyList = adjacencyList;
        this.neighborCount = neighborCounts;
        this.indices = builder.build();
    }

    /**
     * Constructs a new {@link Graph} of the specified values using the specified edge predicate for building the graph.
     *
     * @param vertices the values to use when building the graph
     * @param criteria the edge predicate for determine if two vertices have an edge
     * @param <T>      the type of the vertices in the graph
     * @return a new {@link Graph} represented by the vertices and edge predicate
     */
    public static <T> Graph<T> of(Set<T> vertices, BiPredicate<T, T> criteria) {
        return new Graph<>(vertices, criteria);
    }

    /**
     * Returns whether the specified vertex is present within the graph.
     *
     * @param vertex the vertex to look for in the graph
     * @return {@code true} if the vertex is present in the graph
     */
    public boolean contains(@NonNull T vertex) {
        return indices.containsKey(vertex);
    }

    /**
     * Returns the total number of vertices that intersect the specified vertex.
     * <p>
     * Edges between vertices are decided by the edge predicate used when constructing the {@link Graph}.
     * <p>
     * Loops are not allowed within {@link Graph}, therefore it is possible for a vertex to have a neighbor
     * count of {@code 0}.
     *
     * @param vertex the vertex to find total intersecting vertices
     * @return the count of vertices in the graph that have edges with the specified vertex
     * @throws IllegalArgumentException indicating the vertex is not present in the graph
     */
    public int neighborCount(@NonNull T vertex) {
        return neighborCount[indexOf(vertex)];
    }

    /**
     * Determines if the two specified vertices are neighbors in the graph. A neighboring vertex is a vertex
     * that has an edge with the specified vertex.
     * <p>
     * Edges between vertices are decided by the edge predicate used when constructing the {@link Graph}.
     * <p>
     * Loops are not allowed within {@link Graph}, therefore a vertex is not a neighbor with itself.
     *
     * @param one the first vertex
     * @param two the second vertex
     * @return {@code true} if the vertices are neighbors in the graph
     * @throws IllegalArgumentException indicating one of the vertices is not present in the graph
     */
    public boolean neighbors(@NonNull T one, @NonNull T two) {
        if (one == two) {
            return false;
        }
        int oneIndex = indexOf(one);
        int twoIndex = indexOf(two);
        for (int i = 0; i < neighborCount[oneIndex]; i++) {
            if (adjacencyList[oneIndex][i] == twoIndex) {
                return true;
            }
        }
        return false;
    }

    /**
     * Represents the total number of vertices in the graph.
     *
     * @return the total vertex count
     */
    public int size() {
        return vertices.size();
    }

    /**
     * Finds the vertex of the specified index in the graph.
     * <p>
     * The ordering of vertices is determined by the {@link Set} of values when constructing the {@link Graph}.
     *
     * @param index the index of the vertex to find
     * @return the vertex at the specified index in the graph
     * @throws IllegalArgumentException indicating there is not a vertex present at the specified index in the graph
     */
    public T at(int index) {
        T vertex = indices.inverse().get(index);
        if (vertex == null) {
            throw new IllegalArgumentException("A vertex could not be found in the graph at index " + index + ".");
        }
        return vertex;
    }

    /**
     * Finds the index of the specified vertex in the graph.
     * <p>
     * The ordering of vertices is determined by the {@link Set} of values when constructing the {@link Graph}.
     *
     * @param vertex the vertex to find
     * @return the index of the vertex in the graph
     * @throws IllegalArgumentException indicating the vertex is not present in the graph
     */
    public int indexOf(@NonNull T vertex) {
        Integer index = indices.get(vertex);
        if (index == null) {
            throw new IllegalArgumentException(vertex + " could not be found in the graph.");
        }
        return index;
    }

    /**
     * Constructs a {@link Stream} that allows streaming the neighbor vertices of the specified vertex. A neighboring
     * vertex is a vertex that has an edge with the specified vertex.
     * <p>
     * The {@link Stream} has the following characteristics:
     * <ul>
     * <li>{@link Spliterator#DISTINCT}</li>
     * <li>{@link Spliterator#SIZED}</li>
     * <li>{@link Spliterator#NONNULL}</li>
     * <li>{@link Spliterator#IMMUTABLE}</li>
     * <li>{@link Spliterator#SUBSIZED}</li>
     * </ul>
     * <p>
     * It is recommended to use {@link Graph#neighbors(Object, Object)} for checking if two vertices are
     * neighbors as it eliminates an {@link Iterator}, {@link Spliterator}, and {@link Stream} object creation.
     * <p>
     * It is recommended to use {@link Graph#neighborCount(Object)} for counting the number of neighbors of
     * a vertex instead of using {@code neighbors(vertex).count()} as it eliminates the unnecessary {@link Stream} object creation.
     * <p>
     * It is recommended to use {@link Graph#forEachNeighbor(Object, Consumer)} for performing a single
     * operation on each neighbor as it eliminates a {@link Spliterator} and {@link Stream} object creation.
     *
     * @param vertex the vertex to stream neighboring vertices
     * @return a {@link Stream} of the vertices which intersect the specified vertex
     * @throws IllegalArgumentException indicating the vertex is not present in the graph
     */
    public Stream<T> neighbors(@NonNull T vertex) {
        int index = indexOf(vertex);
        return Arrays.stream(adjacencyList[index], 0, neighborCount[index]).mapToObj(this::at);
    }

    /**
     * Applies the specified {@link Consumer} to each neighboring vertex of the specified vertex. A neighboring
     * vertex is a vertex that has an edge with the specified vertex.
     *
     * @param vertex   the vertex whose neighbors to find
     * @param consumer the function to apply to each neighbor of the vertex
     * @throws IllegalArgumentException indicating the vertex is not present in the graph
     * @see Graph#indexOf(Object)
     */
    public void forEachNeighbor(@NonNull T vertex, Consumer<T> consumer) {
        int index = indexOf(vertex);
        for (int i = 0; i < neighborCount[index]; i++) {
            consumer.accept(at(adjacencyList[index][i]));
        }
    }

    /**
     * Applies the specified {@link Consumer} to each
     * <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a> of the graph.
     * <p>
     * Each <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a> is found
     * by performing a DFS search from an arbitrary vertex in the graph that has not already been visited by the iterator.
     * Searching for a connected component terminates once all vertices in the graph have been visited.
     * <p>
     * Each vertex is guaranteed to appear <b>only once</b> in a
     * <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a>.
     * <p>
     * The {@link Iterator} internally used when applying the component function produces the
     * <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected components</a> of the graph
     * lazily, so it is advised to limit the number of repeated iterations over the components.
     *
     * @param consumer the function to apply to each connected component of the graph
     * @see Graph#componentIterator()
     */
    public void forEachComponent(Consumer<Set<T>> consumer) {
        componentIterator().forEachRemaining(consumer);
    }

    /**
     * Constructs an {@link Iterator} that iterates over each
     * <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a> of the graph.
     * <p>
     * Each <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a> is found
     * by performing a DFS search from an arbitrary vertex in the graph that has not already been visited by the iterator.
     * Searching for a connected component terminates once all vertices in the graph have been visited.
     * <p>
     * Each vertex is guaranteed to appear <b>only once</b> in a
     * <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a>.
     * <p>
     * The {@link Iterator} produces the
     * <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected components</a> of the graph
     * lazily, so it is advised to limit the number of repeated iterations over the components.
     *
     * @return an {@link Iterator} that produces each connected component of the graph
     */
    private Iterator<Set<T>> componentIterator() {
        return new Iterator<Set<T>>() {
            private boolean[] visited = new boolean[vertices.size()];
            private int currentIndex;
            private Set<T> component;

            /**
             * Attempts to find the next
             * <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a> if not
             * already found.
             *
             * @return {@code true} if there was a connected component already found or one remaining in the graph
             * @see #findNextComponent()
             */
            @Override
            public boolean hasNext() {
                if (component == null) {
                    findNextComponent();
                }
                return component != null;
            }

            /**
             * Finds the next <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a>
             * of the graph, if not already found by {@link #hasNext()}.
             *
             * @return the next connected component in the graph
             * @throws NoSuchElementException if there does not exist another connected component in the graph
             */
            @Override
            public Set<T> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Set<T> nextComponent = component;
                component = null;
                return nextComponent;
            }

            /**
             * Finds the next <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a>
             * of the graph.
             *<p>
             * Each <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a> is found
             * by performing a DFS search from the current vertex (the current vertex is always the unvisited vertex with
             * the smallest index) in the graph that has not already been visited by the iterator.
             */
            private void findNextComponent() {
                moveToNextUnvisited();
                if (currentIndex >= visited.length) {
                    return;
                }
                ImmutableSet.Builder<T> component = ImmutableSet.builder();
                //dfs from current index to generate a component
                Deque<Integer> verticesToSearch = new LinkedList<>();
                verticesToSearch.push(currentIndex);
                while (!verticesToSearch.isEmpty()) {
                    int currentVertex = verticesToSearch.pop();
                    if (visited[currentVertex]) {
                        continue;
                    }
                    visited[currentVertex] = true;
                    component.add(at(currentVertex));
                    for (int i = 0; i < neighborCount[currentVertex]; i++) {
                        verticesToSearch.push(adjacencyList[currentVertex][i]);
                    }
                }
                this.component = component.build();
            }

            /**
             * Moves the current cursor past any visited vertices within {@link #visited}. If the vertex at the current
             * index is unvisited, the index <b>does not</b> change.
             */
            private void moveToNextUnvisited() {
                while (currentIndex < visited.length && visited[currentIndex]) {
                    currentIndex++;
                }
            }
        };
    }
}
