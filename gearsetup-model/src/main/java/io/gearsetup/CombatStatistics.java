package io.gearsetup;

import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.gson.Gson;
import org.immutables.value.Value.Immutable;

/**
 * A representation of the <a href="http://oldschoolrunescape.wikia.com/wiki/Equipment_Stats">equipment statistics</a>
 * for a piece of equipment in <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 * <p>
 * <img src="https://vignette.wikia.nocookie.net/2007scape/images/3/38/Equipment_Stats_interface.png" alt="Equipment Stats">
 * </p>
 * {@link CombatStatistics} are the bonuses given by the armour and weapons that a player is wearing. The bonuses an
 * item provides are placed into three different categories: Attack bonus, Defence bonus, and Other bonuses.
 * These bonuses, in addition to level of the player's relevant combat skills and the attack style used, are used to
 * calculate both the damage dealt and received during combat, and the likelihood of an attack being successful.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Immutable
@Gson.TypeAdapters
@ImmutableGearSetupStyle
public interface CombatStatistics {
    /**
     * Represents the offensive combat statistics used in the calculations for damage likelihood between two characters.
     * <p>
     * Attack bonuses are compared against the relevant defence bonuses of an enemy to determine the likelihood of hitting
     * an enemy. Defence bonuses are compared against the relevant attack bonuses of an enemy to determine the likelihood
     * of being hit by an enemy and vice versa, a monsters likelihood of hitting a player.
     *
     * @return the offensive combat bonuses
     */
    AttackTypeStatistics getAttackBonuses();

    /**
     * Represents the defensive combat statistics used in the calculations for damage likelihood between two characters.
     * <p>
     * Attack bonuses are compared against the relevant defence bonuses of an enemy to determine the likelihood of hitting
     * an enemy. Defence bonuses are compared against the relevant attack bonuses of an enemy to determine the likelihood
     * of being hit by an enemy and vice versa, a monsters likelihood of hitting a player.
     *
     * @return the defensive combat bonuses
     */
    AttackTypeStatistics getDefenceBonuses();

    /**
     * Represents the amount in which prayer rate slows from its original rate.
     * <p>
     * The drain rate of a prayer is the amount of time a prayer takes to drain 1 prayer point. A prayer bonus of +15
     * will make all prayers last 50% longer. Comparatively a bonus of +30 will double the length any prayer can be used.
     *
     * @return the bonus the equipment provides for slowing down prayer drain
     */
    int getPrayerBonus();

    /**
     * Represents the strength bonus used in calculating the character's maximum melee damage.
     *
     * @return the melee strength bonus affecting maximum damage
     */
    int getMeleeStrength();

    /**
     * Represents the ranged strength bonus of ammo used in calculating the character's maximum ranged damage.
     *
     * @return the ammo ranged strength bonus
     */
    int getRangedStrength();

    /**
     * Represents the magic strength bonus used in calculating a spell's maximum damage dealt.
     *
     * @return the magic strength bonus affecting spell maximum damage
     */
    int getMagicStrength();
}
