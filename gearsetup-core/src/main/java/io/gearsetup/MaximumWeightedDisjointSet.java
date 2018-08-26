package io.gearsetup;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.ToDoubleFunction;

/**
 * A utility class providing a variant of <a href="https://en.wikipedia.org/wiki/Maximum_disjoint_set">Maximum disjoint set</a>
 * that calculates the maximal non-overlapping <a href="https://en.wikipedia.org/wiki/Class_(set_theory)">class</a> of
 * a <a href="https://en.wikipedia.org/wiki/Family_of_sets">family</a>.
 * <p>
 * {@link MaximumWeightedDisjointSet} allows for an arbitrary weighting algorithm for each set. To align with the original
 * <a href="https://en.wikipedia.org/wiki/Maximum_disjoint_set">Maximum disjoint set</a> algorithm, the weight algorithm
 * can be the cardinality of the set.
 * <p>
 * {@code Set<T> set = MaximumWeightedDisjointSet.find(candidates, Set::size)}
 *
 * @author Ian Caffey
 * @since 1.0
 */
@UtilityClass
public class MaximumWeightedDisjointSet {
    /**
     * Finds the maximal weighted disjoint class of the specified candidate set.
     * <p>
     * For sets with {@code Set#size() < 2}, the specified set is returned as an {@link ImmutableSet}, as there can only
     * be a single combination/subset created from the set so it is always maximal.
     * <p>
     * For sets with {@code Set#size() >= 2}, the intersection matrix for all combinations of candidates is calculated
     * while producing the weighted sum of disjoint sets for each row, maximizing it incrementally as each row is processed.
     * Once all rows of the intersection matrix have been processed, the row with the maximum weight is collected to the
     * result set. Each element in the row that did not intersect with the row owner along with the row owner itself are
     * contained by the result set.
     * <p>
     * Step 1: Intersection Matrix
     * <p>
     * For each row in the matrix, check if the cell intersects with the row owner. If the cell intersects, mark the cell
     * as "Intersects", otherwise mark the cell as "Does not intersect".
     *
     * <table summary="Intersection matrix for the sets {}">
     * <tr><td></td><td>{1,2}</td><td>{1,3}</td><td>{0,3}</td></tr>
     * <tr><td>{1,2}</td><td>Intersects</td><td>Intersect</td><td>Does not intersect</td></tr>
     * <tr><td>{1,3}</td><td>Intersects</td><td>Intersect</td><td>Intersect</td></tr>
     * <tr><td>{0,3}</td><td>Does not intersect</td><td>Intersect</td><td>Intersect</td></tr>
     * </table>
     * <p>
     * Step 2: Weighted Intersection Matrix
     * <p>
     * For each row in the matrix, calculate the sum of w(t) for each cell that does not intersect with the row owner
     * along with the cell owner itself.
     *
     * <table summary="Weighted intersection matrix for the sets {}">
     * <tr><td></td><td>{1,2}, w({1,2}) = 1</td><td>{1,3}, w({1,3}) = 2</td><td>{0,3}, w({0,3}) = 3</td><td>w(r)</td></tr>
     * <tr><td>{1,2}</td><td>Intersects</td><td>Intersect</td><td>Does not intersect</td><td>4</td></tr>
     * <tr><td>{1,3}</td><td>Intersects</td><td>Intersect</td><td>Intersect</td><td>0</td></tr>
     * <tr><td>{0,3}</td><td>Does not intersect</td><td>Intersect</td><td>Intersect</td><td>4</td></tr>
     * </table>
     * <p>
     * Step 3: Maximum Disjoint Row
     * <p>
     * For the row with maximum weight, collect all cells that are marked "Does not intersect" along with the row owner
     * to a set. This set represents the maximum disjoint subset of elements in the original candidate set.
     * <p>
     * Time Complexity: {@code O(n^2)} where n is the number of candidates. The intersection matrix {@code boolean[][]}
     * must be calculated and processed to find the disjoint set with maximal weight.
     * <p>
     * Space Complexity: {@code O(n^2)} where n is the number of candidates. An indexable copy of the candidate set
     * {@link ImmutableSet} and the intersection matrix {@code boolean[][]}.
     *
     * @param candidates the sets to consider when finding the maximal disjoint subset
     * @param weight     the weighting function for each candidate
     * @param <T>        the type of set being used for finding maximum weighted disjoint sets
     * @return the set of candidates that maximize the weight function without intersecting other members of the set
     */
    public <T extends Set<?>> Set<T> find(@NonNull Set<T> candidates, ToDoubleFunction<T> weight) {
        int size = candidates.size();
        //empty and singleton candidate sets cannot produce any combinations, so always return itself
        if (size < 2) {
            return ImmutableSet.copyOf(candidates);
        }
        int maximumDisjointSetIndex = -1;
        double maximumDisjointSetWeight = -Double.MAX_VALUE;
        boolean[][] intersections = new boolean[size][size];
        List<T> elements = ImmutableList.copyOf(candidates);
        for (int row = 0; row < size; row++) {
            T source = elements.get(row);
            double disjointSetWeight = weight.applyAsDouble(source); //starting element for row is always considered
            for (int column = 0; column < size; column++) {
                T destination = elements.get(column);
                boolean intersects = !Collections.disjoint(source, destination);
                intersections[row][column] |= intersects;
                if (!intersects) {
                    disjointSetWeight += weight.applyAsDouble(destination);
                }
            }
            if (disjointSetWeight > maximumDisjointSetWeight) {
                maximumDisjointSetIndex = row;
                maximumDisjointSetWeight = disjointSetWeight;
            }
        }
        ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        builder.add(elements.get(maximumDisjointSetIndex));
        //add all sets from the maximum disjoint set row that do not intersect
        for (int i = 0; i < size; i++) {
            if (!intersections[maximumDisjointSetIndex][i]) {
                builder.add(elements.get(i));
            }
        }
        return builder.build();
    }
}
