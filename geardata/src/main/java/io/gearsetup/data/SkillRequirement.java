package io.gearsetup.data;

import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.gson.Gson;
import org.immutables.value.Value.Immutable;

/**
 * A representation of an in-game requirement of a character to level up a
 * <a href="http://oldschoolrunescape.wikia.com/wiki/Skills">skill</a> in order to use a piece of equipment in
 * <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 * <p>
 * <a href="https://oldschool.runescape.com/">Old School Runescape</a> uses a tier system for distributing access to
 * equipment across skill levels from 1-99. Classes of equipment provide a variant of the piece of equipment at increasing
 * levels (e.g. <a href="http://oldschoolrunescape.wikia.com/wiki/Axe">Axes</a> used in
 * <a href="http://oldschoolrunescape.wikia.com/wiki/Woodcutting">Woodcutting</a> are distributed across levels 1, 6,
 * 11, 21, 31, 41, and 61).
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Immutable
@Gson.TypeAdapters
@ImmutableGearSetupStyle
public interface SkillRequirement extends WornItemRequirement {
    /**
     * Represents the {@link Skill} in which the character level must meet or exceed {@link SkillRequirement#getLevel()}.
     *
     * @return the skill the character level must meet or exceed
     */
    Skill getSkill();

    /**
     * Represents the level in {@link SkillRequirement#getSkill()} the character must meet or exceed to equip a worn item.
     *
     * @return the required level for equipping a worn item
     */
    int getLevel();

    /**
     * Accepts a {@link WornItemRequirementVisitor} to visit an implementation of {@link WornItemRequirement}.
     * <p>
     * {@link WornItemRequirementVisitor} is forwarded to {@link WornItemRequirementVisitor#visit(SkillRequirement)}
     * and the result of invoking the visit method is returned.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitation of a skill requirement
     * @return the value the visitor produces after visiting a skill requirement
     */
    @Override
    default <T> T accept(WornItemRequirementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}