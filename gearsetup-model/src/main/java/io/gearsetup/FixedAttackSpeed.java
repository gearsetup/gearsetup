package io.gearsetup;

import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.gson.Gson;
import org.immutables.value.Value.Immutable;

/**
 * A representation of the <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">attack speed</a> for a weapon
 * which attacks using a fixed rate in <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 *
 * <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">Attack speed</a> for a weapon resolve to a fixed rate
 * before the weapon begins operating. The attack speed can not be altered by any consumables, rather it is determined by
 * the type of weapon being used.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Immutable
@Gson.TypeAdapters
@ImmutableGearSetupStyle
public interface FixedAttackSpeed extends AttackSpeed {
    //Immutables builder stub to hide immutable class dependency
    static Builder builder() {
        return ImmutableFixedAttackSpeed.builder();
    }

    //Immutables factory stub to hide immutable class dependency
    static FixedAttackSpeed of(int speed) {
        return ImmutableFixedAttackSpeed.of(speed);
    }

    /**
     * Represents the fixed rate of speed of attacking during combat.
     *
     * <table summary="Attack speed hit interval and timing breakdown">
     * <tr><th>Attack speed</th><th>Hit interval</th><th>Time</th></tr>
     * <tr><td>7</td><td>3</td><td>1.8s</td></tr>
     * <tr><td>6</td><td>4</td><td>2.4s</td></tr>
     * <tr><td>5</td><td>5</td><td>3.0s</td></tr>
     * <tr><td>4</td><td>6</td><td>3.6s</td></tr>
     * <tr><td>3</td><td>7</td><td>4.2s</td></tr>
     * <tr><td>2</td><td>8</td><td>4.8s</td></tr>
     * <tr><td>1</td><td>9</td><td>5.4s</td></tr>
     * </table>
     *
     * @return the fixed rate of speed for the weapon
     */
    int getSpeed();

    /**
     * Accepts an {@link AttackSpeedVisitor} to visit an implementation of {@link AttackSpeed}.
     * <p>
     * {@link AttackSpeedVisitor} is forwarded to {@link AttackSpeedVisitor#visit(FixedAttackSpeed)}
     * and the result of invoking the visit method is returned.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitation of a fixed attack speed
     * @return the value the visitor produces after visiting a fixed attack speed
     */
    @Override
    default <T> T accept(AttackSpeedVisitor<T> visitor) {
        return visitor.visit(this);
    }

    //Immutables builder stub to hide immutable class dependency
    interface Builder {
        Builder setSpeed(int speed);

        FixedAttackSpeed build();
    }
}
