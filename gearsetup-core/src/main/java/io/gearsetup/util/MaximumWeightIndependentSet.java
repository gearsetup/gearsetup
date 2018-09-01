package io.gearsetup.util;

import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Iterator;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToDoubleFunction;

/**
 * A utility class providing an implementation of
 * <a href="https://en.wikipedia.org/wiki/Independent_set_(graph_theory)#Finding_maximum_independent_sets">maximum-weight independent set</a>
 * that performs modular decomposition, delegates to {@link RecursiveMaximumWeightIndependentSet} for calculating the
 * maximum-weight independent set of each <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected component</a>,
 * and collects the maximum-weight independent subsets into a single set which is then considered maximal for the {@link IndexedGraph}.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@UtilityClass
public class MaximumWeightIndependentSet {
    /**
     * Finds the<a href="https://en.wikipedia.org/wiki/Independent_set_(graph_theory)#Finding_maximum_independent_sets">maximum-weight independent set</a>
     * of the specified graph using the specified weighting function.
     * <p>
     * For graphs with {@code IndexedGraph#size() < 2}, {@link IndexedGraph#getVertices()} is returned, as there can only
     * be a single combination/subset created from the vertices so it is always maximal and graphs with 0 or 1 vertex
     * cannot have edges.
     * <p>
     * For graphs with {@code IndexedGraph#size() >= 2}, the graph is decomposed into its
     * <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected components</a> where each
     * component is processed by {@link RecursiveMaximumWeightIndependentSet} and the results are aggregated into a set.
     *
     * @param graph  the graph to find the maximum-weight independent set
     * @param weight the weighting function for each candidate
     * @param <T>    the type of value being used for finding maximum-weight independent set
     * @return the independent set of vertices that maximize the weight function
     */
    public <T> Set<T> find(@NonNull IndexedGraph<T> graph, @NonNull ToDoubleFunction<T> weight) {
        return find(graph.getVertices(), graph.getEdgePredicate(), weight);
    }

    /**
     * Finds the<a href="https://en.wikipedia.org/wiki/Independent_set_(graph_theory)#Finding_maximum_independent_sets">maximum-weight independent set</a>
     * of the specified vertices, edge predicate, and weighting function.
     * <p>
     * For sets with {@code IndexedGraph#size() < 2}, {@link IndexedGraph#getVertices()} is returned, as there can only
     * be a single combination/subset created from the vertices so it is always maximal and graphs with 0 or 1 vertex
     * cannot have edges.
     * <p>
     * For sets with {@code IndexedGraph#size() >= 2}, the {@link IndexedGraph} of the vertices is decomposed into its
     * <a href="https://en.wikipedia.org/wiki/Connected_component_(graph_theory)">connected components</a> where each
     * component is processed by {@link RecursiveMaximumWeightIndependentSet} and the results are aggregated into a set.
     *
     * @param vertices  the vertices of the graph to consider when finding the maximum-weight independent set
     * @param predicate the edge predicate for determine if two vertices have an edge
     * @param weight    the weighting function for each candidate
     * @param <T>       the type of value being used for finding maximum-weight independent set
     * @return the independent set of vertices that maximize the weight function
     */
    public <T> Set<T> find(@NonNull Set<T> vertices, @NonNull BiPredicate<T, T> predicate, @NonNull ToDoubleFunction<T> weight) {
        if (vertices.size() < 2) {
            return ImmutableSet.copyOf(vertices);
        }
        ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        IndexedGraph.of(vertices, predicate).forEachComponent(component -> {
            //isolated vertex, always maximum and independent
            if (component.size() < 2) {
                builder.addAll(component);
                return;
            }
            //two vertex component, choose maximum of the two in the component
            if (component.size() == 2) {
                Iterator<T> it = component.iterator();
                T first = it.next();
                T second = it.next();
                builder.add(weight.applyAsDouble(first) >= weight.applyAsDouble(second) ? first : second);
                return;
            }
            builder.addAll(RecursiveMaximumWeightIndependentSet.find(component, predicate, weight));
        });
        return builder.build();
    }
}
