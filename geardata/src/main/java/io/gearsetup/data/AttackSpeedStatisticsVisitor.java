package io.gearsetup.data;

/**
 * A representation of a <a href="https://en.wikipedia.org/wiki/Visitor_pattern">visitor</a> to the
 * {@link AttackSpeedStatistics} class hierarchy.
 * <p>
 * Java does not support <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> natively so an
 * interface with knowledge of all implementations of {@link AttackSpeedStatistics} is provided with overloaded
 * implementations of {@code AttackSpeedStatisticsVisitor#visit(T)}to invoke for each different implementation of
 * {@link AttackSpeedStatistics} while the implementation handles the dispatching to the appropriate method call through
 * {@link AttackSpeedStatistics#accept(AttackSpeedStatisticsVisitor)}.
 *
 * @param <T> the type of value returned from visiting an implementation class
 * @author Ian Caffey
 * @since 1.0
 */
public interface AttackSpeedStatisticsVisitor<T> {
    /**
     * Visits the {@link DynamicAttackSpeedStatistics} implementation of {@link AttackSpeedStatistics}.
     * <p>
     * This method is not intended to be invoked directly, but by calling
     * {@link AttackSpeedStatistics#accept(AttackSpeedStatisticsVisitor)} by which
     * <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> can be accomplished by each
     * implementation of {@link AttackSpeedStatistics} forwarding the method call to the appropriate
     * {@code AttackSpeedStatisticsVisitor#visit(T)} variant.
     * <p>
     * {@link DynamicAttackSpeedStatistics} will forward to this method in {@link AttackSpeedStatistics#accept(AttackSpeedStatisticsVisitor)}.
     *
     * @param statistics the dynamic attack speed statistics to visit
     * @return the value after visiting the dynamic attack speed statistics
     */
    T visit(DynamicAttackSpeedStatistics statistics);

    /**
     * Visits the {@link FixedAttackSpeedStatistics} implementation of {@link AttackSpeedStatistics}.
     * <p>
     * This method is not intended to be invoked directly, but by calling
     * {@link AttackSpeedStatistics#accept(AttackSpeedStatisticsVisitor)} by which
     * <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> can be accomplished by each
     * implementation of {@link AttackSpeedStatistics} forwarding the method call to the appropriate
     * {@code AttackSpeedStatisticsVisitor#visit(T)} variant.
     * <p>
     * {@link FixedAttackSpeedStatistics} will forward to this method in {@link AttackSpeedStatistics#accept(AttackSpeedStatisticsVisitor)}.
     *
     * @param statistics the fixed attack speed statistics to visit
     * @return the value after visiting the fixed attack speed statistics
     */
    T visit(FixedAttackSpeedStatistics statistics);

    /**
     * Visits the {@link VariableAttackSpeedStatistics} implementation of {@link AttackSpeedStatistics}.
     * <p>
     * This method is not intended to be invoked directly, but by calling
     * {@link AttackSpeedStatistics#accept(AttackSpeedStatisticsVisitor)} by which
     * <a href="https://en.wikipedia.org/wiki/Double_dispatch">double dispatch</a> can be accomplished by each
     * implementation of {@link AttackSpeedStatistics} forwarding the method call to the appropriate
     * {@code AttackSpeedStatisticsVisitor#visit(T)} variant.
     * <p>
     * {@link VariableAttackSpeedStatistics} will forward to this method in {@link AttackSpeedStatistics#accept(AttackSpeedStatisticsVisitor)}.
     *
     * @param statistics the variable attack speed statistics to visit
     * @return the value after visiting the variable attack speed statistics
     */
    T visit(VariableAttackSpeedStatistics statistics);
}
