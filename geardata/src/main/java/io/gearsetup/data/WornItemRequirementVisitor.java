package io.gearsetup.data;

/**
 * A representation of a <a href="https://en.wikipedia.org/wiki/Visitor_pattern">visitor</a> to the
 * {@link WornItemRequirement} class hierarchy.
 * <p>
 * Java does not support <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> natively so an
 * interface with knowledge of all implementations of {@link WornItemRequirement} is provided with overloaded
 * implementations of {@code WornItemRequirementVisitor#visit(T)}to invoke for each different implementation of
 * {@link WornItemRequirement} while the implementation handles the dispatching to the appropriate method call through
 * {@link WornItemRequirement#accept(WornItemRequirementVisitor)}.
 *
 * @param <T> the type of value returned from visiting an implementation class
 * @author Ian Caffey
 * @since 1.0
 */
public interface WornItemRequirementVisitor<T> {
    /**
     * Visits the {@link QuestRequirement} implementation of {@link WornItemRequirement}.
     * <p>
     * This method is not intended to be invoked directly, but by calling
     * {@link WornItemRequirement#accept(WornItemRequirementVisitor)} by which
     * <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> can be accomplished by each
     * implementation of {@link WornItemRequirement} forwarding the method call to the appropriate
     * {@code WornItemRequirementVisitor#visit(T)} variant.
     * <p>
     * {@link QuestRequirement} will forward to this method in {@link WornItemRequirement#accept(WornItemRequirementVisitor)}.
     *
     * @param requirement the quest requirement to visit
     * @return the value after visiting the quest requirement
     */
    T visit(QuestRequirement requirement);

    /**
     * Visits the {@link SkillRequirement} implementation of {@link WornItemRequirement}.
     * <p>
     * This method is not intended to be invoked directly, but by calling
     * {@link WornItemRequirement#accept(WornItemRequirementVisitor)} by which
     * <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> can be accomplished by each
     * implementation of {@link WornItemRequirement} forwarding the method call to the appropriate
     * {@code WornItemRequirementVisitor#visit(T)} variant.
     * <p>
     * {@link SkillRequirement} will forward to this method in {@link WornItemRequirement#accept(WornItemRequirementVisitor)}.
     *
     * @param requirement the skill requirement to visit
     * @return the value after visiting the skill requirement
     */
    T visit(SkillRequirement requirement);
}
