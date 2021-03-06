package io.gearsetup;

import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.gson.Gson;
import org.immutables.value.Value.Immutable;

/**
 * A representation of an in-game requirement of a character to complete a
 * <a href="http://oldschoolrunescape.wikia.com/wiki/Quests">quest</a> in order to use a piece of equipment in
 * <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 * <p>
 * Completion of a <a href="http://oldschoolrunescape.wikia.com/wiki/Quests">quest</a> can unlock a piece of equipment
 * for use by a character or even reward them with the item directly. Equipment can be purchased from other players or
 * through the <a href="http://oldschoolrunescape.wikia.com/wiki/Grand_Exchange">Grand Exchange</a> but cannot be equipped
 * until the quest is completed.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Immutable
@Gson.TypeAdapters
@ImmutableGearSetupStyle
public interface QuestRequirement extends EquipmentRequirement {
    //Immutables builder stub to hide immutable class dependency
    static Builder builder() {
        return ImmutableQuestRequirement.builder();
    }

    //Immutables factory stub to hide immutable class dependency
    static QuestRequirement of(String questName) {
        return ImmutableQuestRequirement.of(questName);
    }

    /**
     * Represents the name of the <a href="http://oldschoolrunescape.wikia.com/wiki/Quests">quest</a> required to be
     * completed for an item to be equipped.
     *
     * @return the name of the quest to complete to equip an item
     */
    String getQuestName();

    /**
     * Accepts a {@link EquipmentRequirementVisitor} to visit an implementation of {@link EquipmentRequirement}.
     * <p>
     * {@link EquipmentRequirementVisitor} is forwarded to {@link EquipmentRequirementVisitor#visit(QuestRequirement)}
     * and the result of invoking the visit method is returned.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitation of a quest requirement
     * @return the value the visitor produces after visiting a quest requirement
     */
    @Override
    default <T> T accept(EquipmentRequirementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    //Immutables builder stub to hide immutable class dependency
    interface Builder {
        Builder setQuestName(String questName);

        QuestRequirement build();
    }
}
