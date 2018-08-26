package io.gearsetup.data;

import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.gson.Gson;
import org.immutables.value.Value.Immutable;

/**
 * A representation of the <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">attack speed</a> statistics
 * for a weapon in which the speed varies between players and monsters in <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 * <p>
 * Toxic blowpipe is currently the only item in <a href="https://oldschool.runescape.com/">Old School Runescape</a> to
 * employ the strategy of differing attack speeds between players and monsters. When initiating combat with the Toxic
 * blowpipe, the game will identify the type of the target and begin operating using the corresponding
 * <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">attack speed</a>.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Immutable
@Gson.TypeAdapters
@ImmutableGearSetupStyle
public interface DynamicAttackSpeedStatistics extends AttackSpeedStatistics {
    /**
     * Represents the {@link AttackSpeedStatistics} used when in combat with another player.
     *
     * @return the attack speed statistics for PvP combat
     */
    AttackSpeedStatistics getPlayerStatistics();

    /**
     * Represents the {@link AttackSpeedStatistics} used when in combat with a non-playable character.
     *
     * @return the attack speed statistics for PvM combat
     */
    AttackSpeedStatistics getMonsterStatistics();

    /**
     * Accepts an {@link AttackSpeedStatisticsVisitor} to visit an implementation of {@link AttackSpeedStatistics}.
     * <p>
     * {@link AttackSpeedStatisticsVisitor} is forwarded to {@link AttackSpeedStatisticsVisitor#visit(DynamicAttackSpeedStatistics)}
     * and the result of invoking the visit method is returned.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitation of a dynamic attack speed statistics
     * @return the value the visitor produces after visiting a dynamic attack speed statistics
     */
    @Override
    default <T> T accept(AttackSpeedStatisticsVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
