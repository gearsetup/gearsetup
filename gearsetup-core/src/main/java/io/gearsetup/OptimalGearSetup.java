package io.gearsetup;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.gearsetup.util.MaximumWeightIndependentSet;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.BiPredicate;
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
    private static final Map<EquipmentSlot, Set<EquipmentSlot>> SINGLETON_SLOTS = Arrays.stream(EquipmentSlot.values())
            .collect(ImmutableMap.toImmutableMap(Function.identity(), ImmutableSet::of));

    /**
     * Finds the optimal gear setup given the specified candidate {@link Equipment} and the weighting function to maximize.
     * <p>
     * Every occupied slot combination is calculated for the candidate {@link Equipment}, the occupied slots set is maximized
     * using {@link MaximumWeightIndependentSet#find(Set, BiPredicate, ToDoubleFunction)} where the weight function is the maximum
     * weight of a piece of {@link Equipment} that occupies that set of {@link EquipmentSlot}, and then the set of disjoint
     * {@link EquipmentSlot} is converted back to a set of {@link Equipment} by mapping each slot back to its maximum
     * weight {@link Equipment} calculated previously.
     *
     * @param candidates the candidates to consider when finding optimal gear setup
     * @param weight     the weight function to apply to each candidate when maximizing
     * @return the set of candidates that maximize the weight function while occupying unique equipment slot
     * @see MaximumWeightIndependentSet#find(Set, BiPredicate, ToDoubleFunction)
     */
    public Set<Equipment> find(@NonNull Set<Equipment> candidates, @NonNull ToDoubleFunction<Equipment> weight) {
        //collect weight and maximum slot heuristics
        Map<Equipment, Double> weights = candidates.stream().collect(ImmutableMap.toImmutableMap(Function.identity(), weight::applyAsDouble));
        Map<Set<EquipmentSlot>, Equipment> maximumWeightForSlots = new HashMap<>();
        candidates.forEach(equipment -> {
            double equipmentWeight = weights.get(equipment);
            //equipment does not contribute to maximizing the weighting function, having no item would be better than this equipment
            if (equipmentWeight <= 0) {
                return;
            }
            Set<EquipmentSlot> occupiedSlots = equipment.getOccupiedSlots();
            Equipment previousMaximum = maximumWeightForSlots.putIfAbsent(occupiedSlots, equipment);
            //first equipment found for slot or the weight is less than the previous maximum
            if (previousMaximum == null || equipmentWeight <= weights.get(previousMaximum)) {
                return;
            }
            maximumWeightForSlots.put(occupiedSlots, equipment);
        });
        //most multi-slot equipment provide no bonuses over their components, so to reduce overhead in MWIS, filter them out
        Set<Equipment> considered = maximumWeightForSlots.values().stream().filter(equipment -> {
            Set<EquipmentSlot> slots = equipment.getOccupiedSlots();
            if (slots.size() < 2) {
                return true;
            }
            double totalIndividualEquipmentWeight = slots.stream()
                    .mapToDouble(slot -> weights.getOrDefault(maximumWeightForSlots.get(SINGLETON_SLOTS.get(slot)), 0.0))
                    .sum();
            //multi-slot equipment is better than the total individual weight, keep the multi-slot
            return weights.get(equipment) > totalIndividualEquipmentWeight;
        }).collect(ImmutableSet.toImmutableSet());
        //only 0 or 1 candidates are remaining to be considered, they are optimal as there are no other options to consider
        if (considered.size() < 2) {
            return ImmutableSet.copyOf(considered);
        }
        boolean allSingleSlotItems = considered.stream().allMatch(equipment -> equipment.getOccupiedSlots().size() == 1);
        //if all considered items are single slot items, they are guaranteed to each be the best-in-slot, so they are guaranteed to be disjoint
        if (allSingleSlotItems) {
            return ImmutableSet.copyOf(considered);
        }
        return MaximumWeightIndependentSet.find(considered,
                (left, right) -> !Collections.disjoint(left.getOccupiedSlots(), right.getOccupiedSlots()),
                weights::get
        );
    }
}
