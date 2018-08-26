package io.gearsetup;

/**
 * A representation of a <a href="https://en.wikipedia.org/wiki/Visitor_pattern">visitor</a> to the
 * {@link AttackSpeed} class hierarchy.
 * <p>
 * Java does not support <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> natively so an
 * interface with knowledge of all implementations of {@link AttackSpeed} is provided with overloaded
 * implementations of {@code AttackSpeedVisitor#visit(T)}to invoke for each different implementation of
 * {@link AttackSpeed} while the implementation handles the dispatching to the appropriate method call through
 * {@link AttackSpeed#accept(AttackSpeedVisitor)}.
 *
 * @param <T> the type of value returned from visiting an implementation class
 * @author Ian Caffey
 * @since 1.0
 */
public interface AttackSpeedVisitor<T> {
    /**
     * Visits the {@link FixedAttackSpeed} implementation of {@link AttackSpeed}.
     * <p>
     * This method is not intended to be invoked directly, but by calling
     * {@link AttackSpeed#accept(AttackSpeedVisitor)} by which
     * <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> can be accomplished by each
     * implementation of {@link AttackSpeed} forwarding the method call to the appropriate
     * {@code AttackSpeedVisitor#visit(T)} variant.
     * <p>
     * {@link FixedAttackSpeed} will forward to this method in {@link AttackSpeed#accept(AttackSpeedVisitor)}.
     *
     * @param speed the fixed attack speed to visit
     * @return the value after visiting the fixed attack speed
     */
    T visit(FixedAttackSpeed speed);

    /**
     * Visits the {@link TargetDependentAttackSpeed} implementation of {@link AttackSpeed}.
     * <p>
     * This method is not intended to be invoked directly, but by calling
     * {@link AttackSpeed#accept(AttackSpeedVisitor)} by which
     * <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> can be accomplished by each
     * implementation of {@link AttackSpeed} forwarding the method call to the appropriate
     * {@code AttackSpeedVisitor#visit(T)} variant.
     * <p>
     * {@link TargetDependentAttackSpeed} will forward to this method in {@link AttackSpeed#accept(AttackSpeedVisitor)}.
     *
     * @param speed the target dependent attack speed to visit
     * @return the value after visiting the target dependent attack speed
     */
    T visit(TargetDependentAttackSpeed speed);

    /**
     * Visits the {@link TypeDependentAttackSpeed} implementation of {@link AttackSpeed}.
     * <p>
     * This method is not intended to be invoked directly, but by calling
     * {@link AttackSpeed#accept(AttackSpeedVisitor)} by which
     * <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> can be accomplished by each
     * implementation of {@link AttackSpeed} forwarding the method call to the appropriate
     * {@code AttackSpeedVisitor#visit(T)} variant.
     * <p>
     * {@link TypeDependentAttackSpeed} will forward to this method in {@link AttackSpeed#accept(AttackSpeedVisitor)}.
     *
     * @param speed the type dependent attack speed to visit
     * @return the value after visiting the type dependent attack speed
     */
    T visit(TypeDependentAttackSpeed speed);
}
