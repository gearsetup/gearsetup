package io.gearsetup;

import org.immutables.gson.Gson.ExpectedSubtypes;

/**
 * A representation of an in-game requirement of a character to use a piece of equipment in
 * <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 * <p>
 * New characters to <a href="https://oldschool.runescape.com/">Old School Runescape</a> are provided with a basic set
 * of equipment that be used without any requirement. As characters level their {@link Skill} and complete quests, new
 * pieces of equipment are unlocked for use.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@ExpectedSubtypes({QuestRequirement.class, SkillRequirement.class}) //required for gson adapters to support polymorphism
public interface WornItemRequirement {
    /**
     * Accepts a {@link WornItemRequirementVisitor} to visit an implementation of {@link WornItemRequirement}.
     * <p>
     * Implementations of {@link WornItemRequirement} are responsible for forwarding calls to the respective
     * {@code WornItemRequirementVisitor#visit(T)} method for their implementation.
     * <p>
     * {@link WornItemRequirementVisitor} defines a separate {@code WornItemRequirementVisitor#visit(T)} method for each
     * expected implementation of {@link WornItemRequirement}. It is considered an error to create an implementation of
     * {@link WornItemRequirement} that is not covered by the visitor or for the {@link WornItemRequirement} to do
     * nothing in this method.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitation of an implementation class
     * @return the value the visitor produces after visiting an implementation class
     */
    <T> T accept(WornItemRequirementVisitor<T> visitor);
}
