/**
 * Provides parsers that transform parse results.
 *
 * <ul>
 *   <li>{@link MapParserTransform} — applies a {@link
 * java.util.function.Function} to the inner parser's result. <li>{@link
 * MapParserPure} — ignores the inner result and returns a constant. <li>{@link
 * MapParserCast} — casts the inner result to a supertype. <li>{@link
 * FlatMapParser} — sequences parsers where the second parser depends on the
 * first parser's result (monadic bind).
 * </ul>
 *
 * <p>All types in this package are null-marked; see
 * {@link org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.parsers.map;

import org.jspecify.annotations.NullMarked;