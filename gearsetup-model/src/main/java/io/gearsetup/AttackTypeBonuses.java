package io.gearsetup;

import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.gson.Gson;
import org.immutables.value.Value.Immutable;

/**
 * A representation of the <a href="http://oldschoolrunescape.wikia.com/wiki/Equipment_Stats">equipment statistics</a>
 * that pertain to the bonuses used when calculating the likelihood of hitting/taking a hit from an enemy
 * in <a href="https://oldschool.runescape.com/">Old School Runescape</a>.
 * <p>
 * Each piece of equipment has both <a href="http://oldschoolrunescape.wikia.com/wiki/Attack">Attack</a> bonuses
 * and <a href="http://oldschoolrunescape.wikia.com/wiki/Defence">Defence</a> bonuses. Attack bonuses
 * are compared against the relevant defence bonuses of an enemy to determine the likelihood of hitting an enemy. Defence
 * bonuses are compared against the relevant attack bonuses of an enemy to determine the likelihood of being hit
 * by an enemy and vice versa, a monsters likelihood of hitting a player.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Immutable
@Gson.TypeAdapters
@ImmutableGearSetupStyle
public interface AttackTypeBonuses {
    //Immutables builder stub to hide immutable class dependency
    static Builder builder() {
        return ImmutableAttackTypeBonuses.builder();
    }

    //Immutables factory stub to hide immutable class dependency
    static AttackTypeBonuses of(int stabBonus, int slashBonus, int crushBonus, int magicBonus, int rangedBonus) {
        return ImmutableAttackTypeBonuses.of(stabBonus, slashBonus, crushBonus, magicBonus, rangedBonus);
    }

    /**
     * Represents the equipment combat bonus when using {@link AttackType#STAB} in combat.
     * <p>
     * The higher the attack bonus of the desired attack style, the higher the chance the player will hit a {@code 0} during combat.
     * The higher the defensive bonus, the higher the chance the opponent will hit a {@code 0} during combat.
     *
     * @return the equipment stab bonus
     */
    int getStabBonus();

    /**
     * Represents the equipment combat bonus when using {@link AttackType#SLASH} in combat.
     * <p>
     * The higher the attack bonus of the desired attack style, the higher the chance the player will hit a {@code 0} during combat.
     * The higher the defensive bonus, the higher the chance the opponent will hit a {@code 0} during combat.
     *
     * @return the equipment slash bonus
     */
    int getSlashBonus();

    /**
     * Represents the equipment combat bonus when using {@link AttackType#CRUSH} in combat.
     * <p>
     * The higher the attack bonus of the desired attack style, the higher the chance the player will hit a {@code 0} during combat.
     * The higher the defensive bonus, the higher the chance the opponent will hit a {@code 0} during combat.
     *
     * @return the equipment crush bonus
     */
    int getCrushBonus();

    /**
     * Represents the equipment combat bonus when using {@link AttackType#MAGIC} in combat.
     * <p>
     * The higher the attack bonus of the desired attack style, the higher the chance the player will hit a {@code 0} during combat.
     * The higher the defensive bonus, the higher the chance the opponent will hit a {@code 0} during combat.
     *
     * @return the equipment magic bonus
     */
    int getMagicBonus();

    /**
     * Represents the equipment combat bonus when using {@link AttackType#RANGED} in combat.
     * <p>
     * The higher the attack bonus of the desired attack style, the higher the chance the player will hit a {@code 0} during combat.
     * The higher the defensive bonus, the higher the chance the opponent will hit a {@code 0} during combat.
     *
     * @return the equipment ranged bonus
     */
    int getRangedBonus();

    //Immutables builder stub to hide immutable class dependency
    interface Builder {
        Builder setStabBonus(int bonus);

        Builder setSlashBonus(int bonus);

        Builder setCrushBonus(int bonus);

        Builder setMagicBonus(int bonus);

        Builder setRangedBonus(int bonus);

        AttackTypeBonuses build();
    }
}
