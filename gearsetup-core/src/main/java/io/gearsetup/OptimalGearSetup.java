package io.gearsetup;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * A utility class providing the ability to calculate the optimal set of {@link Equipment} to be worn by a character
 * in <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 * <p>
 * {@link OptimalGearSetup} extends <a href="https://en.wikipedia.org/wiki/Maximum_disjoint_set">Maximum disjoint set</a>
 * with the ability to apply arbitrary weights to each "shape" in the candidate set to maximize arbitrary characteristics
 * of each shape in the candidate set. An example where {@link OptimalGearSetup} reduces back to the original
 * <a href="https://en.wikipedia.org/wiki/Maximum_disjoint_set">Maximum disjoint set</a> algorithm is when the weighting
 * function is {@code equipment -> equipment.getOccupiedSlots().size()} which then maximizes the cardinality of occupied
 * slots.
 * <p>
 * Examples:
 * <p>
 * Finding the equipment setup that maximizes prayer bonus:
 * <p>
 * {@code Set<Equipment> highestPrayerBonus = OptimalGearSetup.find(candidates, equipment -> equipment.getCombatBonuses().getPrayerBonus());}
 * <p>
 * Finding the equipment setup that occupies the most equipment slots:
 * <p>
 * {@code Set<Equipment> mostSlotsUsed = OptimalGearSetup.find(candidates, equipment -> equipment.getOccupiedSlots().size());}
 *
 * @author Ian Caffey
 * @since 1.0
 */
@UtilityClass
public class OptimalGearSetup {
    /**
     * Finds the optimal gear setup given the specified candidate {@link Equipment} and the weighting function to maximize.
     * <p>
     * Every occupied slot combination is calculated for the candidate {@link Equipment}, the occupied slots set is maximized
     * using {@link MaximumWeightedDisjointSet#find(Set, ToDoubleFunction)} where the weight function is the maximum
     * weight of a piece of {@link Equipment} that occupies that set of {@link EquipmentSlot}, and then the set of disjoint
     * {@link EquipmentSlot} is converted back to a set of {@link Equipment} by mapping each slot back to its maximum
     * weight {@link Equipment} calculated previously.
     * <p>
     * Time Complexity: {@code O(n)} where n is the number of candidates. The weighting of each piece of equipment must
     * be calculated and the call to {@link MaximumWeightedDisjointSet#find(Set, ToDoubleFunction)}. While finding the
     * maximum weighted disjoint set is {@code O(m^2)}, {@link EquipmentSlot} is bounded at 12 currently for
     * <a href="https://oldschool.runescape.com/">Old School Runescape</a> as there are 11 slots that {@link Equipment}
     * can occupy and a special case for two-handed equipment which occupies both {@link EquipmentSlot#WEAPON} and
     * {@link EquipmentSlot#SHIELD}, so the call to find the maximum weighted disjoint set of equipment slots is bounded
     * by a constant 12.
     * <p>
     * Space Complexity: {@code O(n)} where n is the number of candidates. A map containing the weights for candidates
     * and the maximum weights for equipment slots and sets for unique slot combinations and maximum disjoint slot sets.
     *
     * @param candidates the candidates to consider when finding optimal gear setup
     * @param weight     the weight function to apply to each candidate when maximizing
     * @return the set of candidates that maximize the weight function while occupying unique equipment slot
     * @see MaximumWeightedDisjointSet#find(Set, ToDoubleFunction)
     */
    public Set<Equipment> find(@NonNull Set<Equipment> candidates, @NonNull ToDoubleFunction<Equipment> weight) {
        //collect weight and maximum slot heuristics
        Map<Equipment, Double> weights = candidates.stream().collect(ImmutableMap.toImmutableMap(Function.identity(), weight::applyAsDouble));
        Map<Set<EquipmentSlot>, Equipment> maximumWeightForSlot = new HashMap<>();
        candidates.forEach(equipment -> {
            Set<EquipmentSlot> occupiedSlots = equipment.getOccupiedSlots();
            Equipment previousMaximum = maximumWeightForSlot.putIfAbsent(occupiedSlots, equipment);
            //first equipment found for slot or the weight is less than the previous maximum
            if (previousMaximum == null || weights.get(equipment) <= weights.get(previousMaximum)) {
                return;
            }
            //replace maximum equipment for slot if weight is greater than the existing weight
            maximumWeightForSlot.put(occupiedSlots, equipment);
        });
        //calculate all unique slot combinations and maximize those combinations
        Set<Set<EquipmentSlot>> uniqueSlotCombinations = candidates.stream()
                .map(Equipment::getOccupiedSlots)
                .collect(ImmutableSet.toImmutableSet());
        Set<Set<EquipmentSlot>> maximumDisjointSets = MaximumWeightedDisjointSet.find(
                uniqueSlotCombinations,
                slots -> weights.get(maximumWeightForSlot.get(slots))
        );
        //map maximized slot combinations back to equipment
        return maximumDisjointSets.stream()
                .map(maximumWeightForSlot::get)
                .collect(ImmutableSet.toImmutableSet());
    }
}
