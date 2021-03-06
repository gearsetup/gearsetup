package io.gearsetup;

import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.gson.Gson;
import org.immutables.value.Value.Immutable;

import java.util.Optional;
import java.util.Set;

/**
 * A representation of a piece of <a href="http://oldschoolrunescape.wikia.com/wiki/Equipment">equipment</a> in
 * <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 * <p>
 * <img src="https://vignette.wikia.nocookie.net/2007scape/images/1/12/Worn_equipment_tab.png" alt="Worn Equipment">
 * </p>
 * Equipment is a special type of item that can be equipped in the equipped inventory and will give a character equipment bonuses.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Immutable
@Gson.TypeAdapters
@ImmutableGearSetupStyle
public interface Equipment {
    //Immutables builder stub to hide immutable class dependency
    static Builder builder() {
        return ImmutableEquipment.builder();
    }

    //Immutables factory stub to hide immutable class dependency
    static Equipment of(int id, String name, Iterable<EquipmentSlot> occupiedSlots, CombatBonuses combatBonuses,
                        AttackSpeed attackSpeed, Iterable<? extends EquipmentRequirement> requirements,
                        double weight) {
        return ImmutableEquipment.of(id, name, occupiedSlots, combatBonuses, Optional.ofNullable(attackSpeed), requirements, weight);
    }

    //Immutables factory stub to hide immutable class dependency
    static Equipment of(int id, String name, Set<EquipmentSlot> occupiedSlots, CombatBonuses combatBonuses,
                        AttackSpeed attackSpeed, Set<? extends EquipmentRequirement> requirements,
                        double weight) {
        return ImmutableEquipment.of(id, name, occupiedSlots, combatBonuses, Optional.ofNullable(attackSpeed), requirements, weight);
    }

    //Immutables factory stub to hide immutable class dependency
    static Equipment of(int id, String name, Iterable<EquipmentSlot> occupiedSlots, CombatBonuses combatBonuses,
                        Optional<? extends AttackSpeed> attackSpeed, Iterable<? extends EquipmentRequirement> requirements,
                        double weight) {
        return ImmutableEquipment.of(id, name, occupiedSlots, combatBonuses, attackSpeed, requirements, weight);
    }

    //Immutables factory stub to hide immutable class dependency
    static Equipment of(int id, String name, Set<EquipmentSlot> occupiedSlots, CombatBonuses combatBonuses,
                        Optional<? extends AttackSpeed> attackSpeed, Set<? extends EquipmentRequirement> requirements,
                        double weight) {
        return ImmutableEquipment.of(id, name, occupiedSlots, combatBonuses, attackSpeed, requirements, weight);
    }

    /**
     * Represents the unique identifier for the <a href="http://oldschoolrunescape.wikia.com/wiki/Items">equipment</a>.
     *
     * @return the unique identifier for the equipment
     */
    int getId();

    /**
     * Represents the name of the <a href="http://oldschoolrunescape.wikia.com/wiki/Items">equipment</a>.
     *
     * @return the name of the equipment
     */
    String getName();

    /**
     * Represents the set of {@link EquipmentSlot} that are occupied by the equipment when equipped.
     * <p>
     * Two-handed weapons are the only {@link Equipment} that occupy multiple slots.
     *
     * @return the set of occupied slots
     */
    Set<EquipmentSlot> getOccupiedSlots();

    /**
     * Represents the bonuses given by the equipment when equipped.
     * <p>
     * The bonuses an item provides are placed into three different categories: Attack bonus, Defence bonus, and Other bonuses.
     * These bonuses, in addition to level of the player's relevant combat skills and the attack style used, are used to
     * calculate both the damage dealt and received during combat, and the likelihood of an attack being successful.
     *
     * @return the combat bonuses given by the item
     */
    CombatBonuses getCombatBonuses();

    /**
     * Represents the attack speed of the equipment.
     * <p>
     * {@link Equipment} which occupy the {@link EquipmentSlot#WEAPON} slot will have an {@link AttackSpeed}.
     * Otherwise, the {@link Equipment} will not have one.
     *
     * @return the weapon attack speed or {@link Optional#empty()}
     */
    Optional<AttackSpeed> getAttackSpeed();

    /**
     * Represents the set of requirements a character must meet to use a piece of equipment.
     *
     * @return the requirements to use a piece of equipment
     */
    Set<EquipmentRequirement> getRequirements();

    /**
     * Represents the weight of the equipment in kilograms.
     *
     * @return the equipment weight in kg
     */
    double getWeight();

    //Immutables builder stub to hide immutable class dependency
    interface Builder {
        Builder setId(int id);

        Builder setName(String name);

        Builder addOccupiedSlot(EquipmentSlot slot);

        Builder addOccupiedSlots(EquipmentSlot... slots);

        Builder setOccupiedSlots(Iterable<EquipmentSlot> slots);

        Builder addAllOccupiedSlots(Iterable<EquipmentSlot> slots);

        Builder setCombatBonuses(CombatBonuses combatBonuses);

        Builder setAttackSpeed(AttackSpeed attackSpeed);

        Builder setAttackSpeed(Optional<? extends AttackSpeed> attackSpeed);

        Builder addRequirement(EquipmentRequirement requirement);

        Builder addRequirements(EquipmentRequirement... requirements);

        Builder setRequirements(Iterable<? extends EquipmentRequirement> requirements);

        Builder addAllRequirements(Iterable<? extends EquipmentRequirement> elements);

        Builder setWeight(double weight);

        Equipment build();
    }
}
