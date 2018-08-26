package io.gearsetup;

/**
 * A representation of a <a href="https://en.wikipedia.org/wiki/Visitor_pattern">visitor</a> to the
 * {@link EquipmentRequirement} class hierarchy.
 * <p>
 * Java does not support <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> natively so an
 * interface with knowledge of all implementations of {@link EquipmentRequirement} is provided with overloaded
 * implementations of {@code EquipmentRequirementVisitor#visit(T)}to invoke for each different implementation of
 * {@link EquipmentRequirement} while the implementation handles the dispatching to the appropriate method call through
 * {@link EquipmentRequirement#accept(EquipmentRequirementVisitor)}.
 *
 * @param <T> the type of value returned from visiting an implementation class
 * @author Ian Caffey
 * @since 1.0
 */
public interface EquipmentRequirementVisitor<T> {
    /**
     * Visits the {@link QuestRequirement} implementation of {@link EquipmentRequirement}.
     * <p>
     * This method is not intended to be invoked directly, but by calling
     * {@link EquipmentRequirement#accept(EquipmentRequirementVisitor)} by which
     * <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> can be accomplished by each
     * implementation of {@link EquipmentRequirement} forwarding the method call to the appropriate
     * {@code EquipmentRequirementVisitor#visit(T)} variant.
     * <p>
     * {@link QuestRequirement} will forward to this method in {@link EquipmentRequirement#accept(EquipmentRequirementVisitor)}.
     *
     * @param requirement the quest requirement to visit
     * @return the value after visiting the quest requirement
     */
    T visit(QuestRequirement requirement);

    /**
     * Visits the {@link SkillRequirement} implementation of {@link EquipmentRequirement}.
     * <p>
     * This method is not intended to be invoked directly, but by calling
     * {@link EquipmentRequirement#accept(EquipmentRequirementVisitor)} by which
     * <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> can be accomplished by each
     * implementation of {@link EquipmentRequirement} forwarding the method call to the appropriate
     * {@code EquipmentRequirementVisitor#visit(T)} variant.
     * <p>
     * {@link SkillRequirement} will forward to this method in {@link EquipmentRequirement#accept(EquipmentRequirementVisitor)}.
     *
     * @param requirement the skill requirement to visit
     * @return the value after visiting the skill requirement
     */
    T visit(SkillRequirement requirement);
}
