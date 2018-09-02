package io.gearsetup.immutables;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

/**
 * An <a href="https://github.com/immutables/immutables">Immutables</a> {@link Style} configuration for the
 * <a href="https://github.com/gearsetup/gearsetup">gearsetup</a> value classes.
 * <p>
 * All <a href="https://github.com/gearsetup/gearsetup">gearsetup</a> value classes are expected to contain the following annotations:
 * <ul>
 * <li>{@link Immutable}</li>
 * <li>{@link ImmutableGearSetupStyle}</li>
 * </ul>
 * {@code @Immutable @ImmutableGearSetupStyle public interface GearValue {}}
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Style(
        defaults = @Immutable(prehash = true),
        get = {"get*", "is*"},
        init = "set*",
        depluralize = true,
        allParameters = true,
        overshadowImplementation = true,
        visibility = Style.ImplementationVisibility.PACKAGE,
        builderVisibility = Style.BuilderVisibility.PACKAGE
)
public @interface ImmutableGearSetupStyle {
}
