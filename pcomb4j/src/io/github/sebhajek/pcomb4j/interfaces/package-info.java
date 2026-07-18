/**
 * Provides abstract base classes and mixin interfaces for building parser
 * implementations.
 *
 * <p>The central element is {@link
 * io.github.sebhajek.pcomb4j.interfaces.CombinatorParser}, a mixin that
 * assembles all combinator interfaces ({@code map}, {@code flatMap}, {@code
 * filter}, {@code and}, {@code or}, etc.) into a single type.
 *
 * <p>Concrete parsers typically extend one of the abstract classes:
 *
 * <ul>
 *   <li>{@link io.github.sebhajek.pcomb4j.interfaces.AbstractParser} — for
 * standalone parsers. <li>{@link
 * io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser} — for parsers
 * that delegate to a single inner parser. <li>{@link
 * io.github.sebhajek.pcomb4j.interfaces.AbstractPairParser} — for parsers that
 * combine two inner parsers.
 * </ul>
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.interfaces;

import org.jspecify.annotations.NullMarked;
