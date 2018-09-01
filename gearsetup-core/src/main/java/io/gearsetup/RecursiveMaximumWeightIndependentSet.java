package io.gearsetup;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToDoubleFunction;

/**
 * A utility class providing an implementation of
 * <a href="https://en.wikipedia.org/wiki/Independent_set_(graph_theory)#Finding_maximum_independent_sets">maximum-weight independent set</a>
 * that preforms a recursive, right-to-left, pre-order, pruning <a href="https://en.wikipedia.org/wiki/Depth-first_search">depth-first search</a>
 * over the binary tree of set combinations to find the independent set that maximizes an arbitrary weighting function.
 * <p>
 * {@link RecursiveMaximumWeightIndependentSet} defines a graph as a {@link Set} of vertices with an edge predicate.
 * An edge predicate is a {@link BiPredicate} that takes in two vertices and returns {@code true} if there is an
 * edge between the vertices.
 * <p>
 * Each path in the binary tree from the root to the leaf nodes is a combination of the graph vertices, where at each depth
 * {@code d} the node represents the vertex at index {@code d} and each branch of the node can either take or ignore
 * the next vertex in the combination.
 * <pre>
 *  Combination Binary Tree
 *      0            1
 *    /   \        /   \
 *   0     1      0     1
 *  / \   / \    / \   / \
 * 0   1 0   1  0   1 0   1
 * </pre>
 *
 * @author Ian Caffey
 * @since 1.0
 */
@UtilityClass
public class RecursiveMaximumWeightIndependentSet {
    /**
     * Finds the <a href="https://en.wikipedia.org/wiki/Independent_set_(graph_theory)#Finding_maximum_independent_sets">maximum-weight independent set</a>
     * of the specified vertices using the specified edge criteria and weight function.
     * <p>
     * For sets with {@code Set#size() < 2}, the specified vertices are returned as an {@link ImmutableSet}, as there can only
     * be a single combination/subset created from the vertices so it is always maximal and graphs with 0 or 1 vertex
     * cannot have edges.
     * For sets with {@code Set#size() >= 2}, a recursive, right-to-left, pre-order, pruning
     * <a href="https://en.wikipedia.org/wiki/Depth-first_search">depth-first search</a> is performed over the binary tree
     * of set combinations to find the independent set that maximizes the specified weighting function.
     *
     * @param vertices the vertices of the graph to consider when finding the maximum-weight independent set
     * @param criteria the criteria predicate for determining if two vertices have an edge
     * @param weight   the weighting function for each candidate
     * @param <T>      the type of vertex being used for finding maximum-weight independent set
     * @return the set of candidates that maximize the weight function without being adjacent to other members of the set
     */
    public <T> Set<T> find(@NonNull Set<T> vertices, @NonNull BiPredicate<T, T> criteria, @NonNull ToDoubleFunction<T> weight) {
        if (vertices.size() < 2) {
            return ImmutableSet.copyOf(vertices);
        }
        List<T> values = ImmutableList.copyOf(vertices);
        int size = vertices.size();
        //buffers for the recursive maximum-weight independent set algorithm
        int[] selected = new int[size];
        int[] maximum = new int[size];
        double[] maximumWeight = new double[1];
        int[] maximumSize = new int[1];
        fill(values, criteria, weight, selected, 0, 0, maximum, maximumSize, maximumWeight);
        //maximum[0, maximumSize[0]] is the maximum-weight independent set
        ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        for (int i = 0; i < maximumSize[0]; i++) {
            builder.add(values.get(maximum[i]));
        }
        return builder.build();
    }

    /**
     * Performs a recursive, right-to-left, pre-order, pruning <a href="https://en.wikipedia.org/wiki/Depth-first_search">depth-first search</a>
     * over the binary tree of set combinations to find the independent set that maximizes an arbitrary weighting function.
     *
     * @param vertices      the vertices of the graph to consider when finding the maximum-weight independent set
     * @param criteria      the criteria predicate for determining if two vertices have an edge
     * @param weight        the weighting function for each vertex
     * @param selected      the buffer for the currently selected vertices in the graph
     * @param selectedCount the length of the sub-array in the selected buffer
     * @param depth         the current depth of the combination binary tree
     * @param maximum       the buffer for the maximum-weight independent set
     * @param maximumSize   the buffer to hold the size of the maximum-weight independent set
     * @param maximumWeight the buffer to hold the weight of the maximum-weight independent set
     * @param <T>           the type of value being used for finding maximum-weight independent set
     */
    private <T> void fill(@NonNull List<T> vertices, @NonNull BiPredicate<T, T> criteria, @NonNull ToDoubleFunction<T> weight,
                          int[] selected, int selectedCount, int depth,
                          @NonNull int[] maximum, @NonNull int[] maximumSize, @NonNull double[] maximumWeight) {
        //traverse tree until recursive call reaches leaf node
        if (depth < vertices.size()) {
            boolean independent = true;
            T current = vertices.get(depth);
            for (int i = 0; i < selectedCount; i++) {
                if (criteria.test(current, vertices.get(selected[i]))) {
                    independent = false;
                    break;
                }
            }
            if (independent) {
                selected[selectedCount++] = depth;
                //continue looking for a maximum independent set which contains the current vertex
                fill(vertices, criteria, weight, selected, selectedCount, depth + 1, maximum, maximumSize, maximumWeight);
                //discard current vertex from selection, to allow finding maximum independent sets that do not contain the current vertex
                --selectedCount;
            }
            //continue looking for a maximum independent set, skipping the vertex at the current depth as it intersected with the current selection
            fill(vertices, criteria, weight, selected, selectedCount, depth + 1, maximum, maximumSize, maximumWeight);
            return;
        }
        //reached leaf node for path, accumulate weight of independent vertices
        double pathWeight = 0;
        for (int i = 0; i < selectedCount; i++) {
            pathWeight += weight.applyAsDouble(vertices.get(selected[i]));
        }
        if (pathWeight > maximumWeight[0]) {
            System.arraycopy(selected, 0, maximum, 0, selectedCount);
            maximumWeight[0] = pathWeight;
            maximumSize[0] = selectedCount;
        }
    }
}
