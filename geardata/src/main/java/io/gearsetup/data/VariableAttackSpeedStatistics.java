package io.gearsetup.data;

import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.gson.Gson;
import org.immutables.value.Value.Immutable;

import java.util.Map;

/**
 * A representation of the <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">attack speed</a> statistics
 * for a weapon which varies the attack speed depending on the {@link AttackType} in <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 * <p>
 * Salamanders are currently the only items in <a href="https://oldschool.runescape.com/">Old School Runescape</a> to
 * employ the strategy of differing attack speeds between {@link AttackType}. When initiating combat with a salamander,
 * the game will identify the current {@link AttackType} and begin operating using the corresponding
 * <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">attack speed</a>. When using the Scorch (Slash) and
 * Blaze (Magic) attack styles, the attack speed is 5. When using the Flare (Range) attack style, the attack speed is 6,
 * which is similar in concept to the Rapid style of most ranged weapons.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Immutable
@Gson.TypeAdapters
@ImmutableGearSetupStyle
public interface VariableAttackSpeedStatistics extends AttackSpeedStatistics {
    /**
     * Represents the varying {@link AttackSpeedStatistics} for each {@link AttackType}.
     * <p>
     * A weapon will have a set of {@link AttackType} that each combat style utilizes during combat and each type will
     * have its own <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">attack speed</a>.
     *
     * @return the attack speed statistics for each relevant combat style
     */
    Map<AttackType, AttackSpeedStatistics> getAttackSpeedStatistics();

    /**
     * Accepts an {@link AttackSpeedStatisticsVisitor} to visit an implementation of {@link AttackSpeedStatistics}.
     * <p>
     * {@link AttackSpeedStatisticsVisitor} is forwarded to {@link AttackSpeedStatisticsVisitor#visit(VariableAttackSpeedStatistics)}
     * and the result of invoking the visit method is returned.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitation of a variable attack speed statistics
     * @return the value the visitor produces after visiting a variable attack speed statistics
     */
    @Override
    default <T> T accept(AttackSpeedStatisticsVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
