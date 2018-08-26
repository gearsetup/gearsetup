package io.gearsetup.data;

import org.immutables.gson.Gson.ExpectedSubtypes;

/**
 * A representation of the <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">attack speed</a> for a weapon
 * in <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 * <p>
 * <a href="http://oldschoolrunescape.wikia.com/wiki/Weapons">Weapons</a> operate at a fixed interval when equipped and
 * their <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">attack speed</a> can be represented as hit intervals.
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
 * The determined <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">attack speed</a> for a weapon can vary
 * depending on the type of weapon.
 * <ul>
 * <li>Ranged weapons increase their attack speed when using the rapid combat style.</li>
 * <li>Salamanders use different attack speeds for each combat style.</li>
 * <li>Toxic blowpipe has a different attack speed between players and for monsters.</li>
 * </ul>
 * More detailed information regarding <a href="http://oldschoolrunescape.wikia.com/wiki/Attack_speed">attack speed</a>
 * for a weapon can be found <a href="http://oldschoolrunescape.wikia.com/wiki/Weapon_slot_table">here</a> for
 * single-handed weapons and <a href="http://oldschoolrunescape.wikia.com/wiki/Two-handed_slot_table">here</a>
 * for two-handed weapons.
 *
 * @author Ian Caffey
 * @since 1.0
 */
//required for gson adapters to support polymorphism
@ExpectedSubtypes({FixedAttackSpeed.class, TargetDependentAttackSpeed.class, TypeDependentAttackSpeed.class})
public interface AttackSpeed {
    /**
     * Accepts an {@link AttackSpeedVisitor} to visit an implementation of {@link AttackSpeed}.
     * <p>
     * Implementations of {@link AttackSpeed} are responsible for forwarding calls to the respective
     * {@code AttackSpeedVisitor#visit(T)} method for their implementation.
     * <p>
     * {@link AttackSpeedVisitor} defines a separate {@code AttackSpeedVisitor#visit(T)} method for each
     * expected implementation of {@link AttackSpeed}. It is considered an error to create an implementation of
     * {@link AttackSpeed} that is not covered by the visitor or for the {@link AttackSpeed} to do
     * nothing in this method.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitation of an implementation class
     * @return the value the visitor produces after visiting an implementation class
     */
    <T> T accept(AttackSpeedVisitor<T> visitor);
}
