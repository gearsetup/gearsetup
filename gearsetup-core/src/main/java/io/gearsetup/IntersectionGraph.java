package io.gearsetup;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A representation of an <a href="https://en.wikipedia.org/wiki/Intersection_graph">Intersection graph</a> that represents
 * the intersections between a set of objects.
 * <p>
 * {@link IntersectionGraph} provides a more generic interface than the typical
 * <a href="https://en.wikipedia.org/wiki/Intersection_graph">Intersection graph</a> by allowing for arbitrary intersection
 * criteria to be used when building the graph.
 *
 * @param <T> the type of the vertices in the intersection graph
 * @author Ian Caffey
 * @since 1.0
 */
public final class IntersectionGraph<T> {
    private final List<T> vertices;
    private final int[] neighborCount;
    private final boolean[][] adjacencyMatrix;
    private final Map<T, Integer> indices;

    /**
     * Constructs a new {@link IntersectionGraph} of the specified values using the specified intersection criteria for
     * building the graph.
     *
     * @param values   the values to use when building the intersection graph
     * @param criteria the criteria predicate for determine if two values intersect
     */
    private IntersectionGraph(@NonNull Set<T> values, @NonNull BiPredicate<T, T> criteria) {
        int size = values.size();
        this.vertices = ImmutableList.copyOf(values);
        this.indices = IntStream.range(0, size).boxed().collect(ImmutableMap.toImmutableMap(vertices::get, Function.identity()));
        int[] neighborCount = new int[size];
        boolean[][] adjacencyMatrix = new boolean[size][size];
        AdjacencyMatrix.fillWithCounts(vertices, criteria, adjacencyMatrix, neighborCount);
        this.neighborCount = neighborCount;
        this.adjacencyMatrix = adjacencyMatrix;
    }

    /**
     * Constructs a new {@link IntersectionGraph} of the specified values using {@link Collection} intersection as the
     * criteria when building the graph.
     * <p>
     * Two {@link Collection} can be checked for intersection by using {@link Collections#disjoint(Collection, Collection)}
     * and negating the result.
     *
     * @param values the values to use when building the intersection graph
     * @param <T>    the type of the vertices in the intersection graph
     * @return a new {@link IntersectionGraph} representing the intersections amongst the specified values
     */
    public static <T extends Collection<?>> IntersectionGraph<T> of(@NonNull Set<T> values) {
        return new IntersectionGraph<>(values, (left, right) -> !Collections.disjoint(left, right));
    }

    /**
     * Constructs a new {@link IntersectionGraph} of the specified values using a transformation function to map the
     * value type to a {@link Collection} which can be used for checking intersection as the criteria when building the graph.
     * <p>
     * Two {@link Collection} can be checked for intersection by using {@link Collections#disjoint(Collection, Collection)}
     * and negating the result.
     *
     * @param values    the values to use when building the intersection graph
     * @param transform the transformation function to a collection which will be used when checking collection intersection
     * @param <T>       the type of the vertices in the intersection graph
     * @return a new {@link IntersectionGraph} representing the intersections amongst the specified values
     */
    public static <T> IntersectionGraph<T> of(@NonNull Set<T> values, @NonNull Function<T, ? extends Collection<?>> transform) {
        return new IntersectionGraph<>(values, (left, right) -> !Collections.disjoint(transform.apply(left), transform.apply(right)));
    }

    /**
     * Constructs a new {@link IntersectionGraph} of the specified values using the specified intersection criteria for
     * building the graph.
     *
     * @param values   the values to use when building the intersection graph
     * @param criteria the criteria predicate for determine if two values intersect
     * @param <T>      the type of the vertices in the intersection graph
     * @return a new {@link IntersectionGraph} representing the intersections amongst the specified values
     */
    public static <T> IntersectionGraph<T> of(Set<T> values, BiPredicate<T, T> criteria) {
        return new IntersectionGraph<>(values, criteria);
    }

    /**
     * Returns whether the specified vertex is present within the intersection graph.
     * <p>
     * For a vertex to be present in the intersection graph, it would have to be passed in by the constructor/factory method.
     *
     * @param vertex the vertex to look for in the intersection graph
     * @return {@code true} if the vertex is present in the intersection graph
     */
    public boolean contains(@NonNull T vertex) {
        return indices.containsKey(vertex);
    }

    /**
     * Returns the total number of vertices that intersect the specified vertex.
     * <p>
     * Intersection between vertices is decided by the intersection criteria used when constructing the {@link IntersectionGraph}.
     * <p>
     * Loops are not allowed within {@link IntersectionGraph}, therefore it is possible for a vertex to have a neighbor
     * count of {@code 0}.
     *
     * @param vertex the vertex to find total intersecting vertices
     * @return the count of vertices in the intersection graph that intersect the specified vertex
     * @throws IllegalArgumentException indicating the vertex is not present in the intersection graph
     */
    public int neighborCount(@NonNull T vertex) {
        return neighborCount[indexOrThrow(vertex)];
    }

    /**
     * Constructs a {@link Stream} that allows streaming the neighbor vertices of the specified vertex. A neighboring
     * vertex is a vertex that has an edge (or intersection) with the specified vertex.
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
     * It is recommended to use {@link IntersectionGraph#neighborCount(Object)} for counting the number of neighbors of
     * a vertex instead of using {@code neighbors(vertex).count()} as it eliminates the unnecessary {@link Stream} object creation.
     * <p>
     * It is recommended to use {@link IntersectionGraph#forEachNeighbor(Object, Consumer)}} for performing a single
     * operation on each neighbor as it eliminates a {@link Spliterator} and {@link Stream} object creation.
     *
     * @param vertex the vertex to stream neighboring vertices
     * @return a {@link Stream} of the vertices which intersect the specified vertex
     * @throws IllegalArgumentException indicating the vertex is not present in the intersection graph
     * @see IntersectionGraph#neighborIterator(int)
     */
    public Stream<T> neighbors(@NonNull T vertex) {
        int index = indexOrThrow(vertex);
        int characteristics = Spliterator.DISTINCT | Spliterator.SIZED | Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.SUBSIZED;
        return StreamSupport.stream(Spliterators.spliterator(neighborIterator(index), neighborCount[index], characteristics), false);
    }

    /**
     * Applies the specified {@link Consumer} to each neighboring vertex of the specified vertex. A neighboring
     * vertex is a vertex that has an edge (or intersection) with the specified vertex.
     * <p>
     * The {@link Iterator} internally used when applying the function finds the neighboring vertices of
     * the vertex lazily, so it is advised to limit the number of repeated iterations over the neighboring vertices.
     *
     * @param vertex   the vertex whose neighbors to find
     * @param consumer the function to apply to each neighbor of the vertex
     * @throws IllegalArgumentException indicating the vertex is not present in the intersection graph
     * @see IntersectionGraph#indexOrThrow(Object)
     * @see IntersectionGraph#neighborIterator(int)
     */
    public void forEachNeighbor(@NonNull T vertex, Consumer<T> consumer) {
        neighborIterator(indexOrThrow(vertex)).forEachRemaining(consumer);
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
     * @param consumer the function to apply to each connected component of the intersection graph
     * @see IntersectionGraph#componentIterator()
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
     * @return an {@link Iterator} that produces each connected component of the intersection graph
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
                    component.add(vertices.get(currentVertex));
                    neighborIterator(currentVertex).forEachRemaining(neighbor -> verticesToSearch.push(indexOrThrow(neighbor)));
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

    /**
     * Constructs an {@link Iterator} that iterates over each neighboring vertex of the vertex at the specified index.
     * A neighboring vertex is a vertex that has an edge (or intersection) with the specified vertex.
     * <p>
     * The {@link Iterator} produces the neighboring vertices of specified the vertex lazily, so it is advised to
     * limit the number of repeated iterations over the neighboring vertices.
     *
     * @return an {@link Iterator} that produces each connected component of the intersection graph
     * @throws IllegalArgumentException indicating the vertex is not valid for the intersection graph
     */
    private Iterator<T> neighborIterator(int index) {
        int size = vertices.size();
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException(index + " is not a valid index for an intersection graph with " + size + " vertices.");
        }
        return new Iterator<T>() {
            private final boolean[] potentialNeighbors = adjacencyMatrix[index];
            private int currentIndex;
            private boolean checkedNextNeighbor;

            /**
             * Attempts to find the next neighboring vertex, if not already found.
             *
             * Neighboring vertices are guaranteed to be found in ascending index order.
             * <p>
             * The ordering of vertices is determined by the {@link Set} of values when constructing the {@link IntersectionGraph}.
             *
             * @return {@code true} if there
             */
            @Override
            public boolean hasNext() {
                if (!checkedNextNeighbor) {
                    moveToNextNeighbor();
                }
                return currentIndex < potentialNeighbors.length;
            }

            /**
             * Finds the next neighboring vertex, if not already found by {@link #hasNext()}.
             *
             * Neighboring vertices are guaranteed to be found in ascending index order.
             * <p>
             * The ordering of vertices is determined by the {@link Set} of values when constructing the {@link IntersectionGraph}.
             *
             * @return {@code true} if there was a neighbor already found or one remaining for the vertex
             * @throws NoSuchElementException if there does not exist another neighbor the vertex
             */
            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                checkedNextNeighbor = false;
                return vertices.get(currentIndex++); //get current and move cursor to next index to enable searching for next neighbor
            }

            /**
             * Moves the current cursor in the adjacency matrix to the next neighbor of the vertex. If the vertex at the
             * current index is a neighbor, the index <b>does not</b> change.
             */
            private void moveToNextNeighbor() {
                while (currentIndex < potentialNeighbors.length && !potentialNeighbors[currentIndex]) {
                    currentIndex++;
                }
                checkedNextNeighbor = true;
            }
        };
    }

    /**
     * Finds the index of the specified vertex in the graph.
     * <p>
     * The ordering of vertices is determined by the {@link Set} of values when constructing the {@link IntersectionGraph}.
     *
     * @param vertex the vertex to find
     * @return the index of the vertex in the graph
     * @throws IllegalArgumentException indicating the vertex is not present in the intersection graph
     */
    private int indexOrThrow(@NonNull T vertex) {
        Integer index = indices.get(vertex);
        if (index == null) {
            throw new IllegalArgumentException(vertex + " could not be found in the intersection graph.");
        }
        return index;
    }
}
