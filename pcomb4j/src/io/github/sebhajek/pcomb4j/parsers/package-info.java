/**
 * Provides concrete {@link io.github.sebhajek.pcomb4j.Parser}
 * implementations for primitive and structural parsing operations.
 *
 * <p>The parsers in this package are the building blocks from which larger
 * grammars are assembled via the combinators in the
 * {@code org.codeberg.sebhajek.pcomb4j.combinators} package:
 * <ul>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.AnyParser} — consumes
 *       any single element.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.LiteralParser} —
 *       matches an exact value.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.FilterParser} —
 *       forwards the inner result only if a predicate is satisfied.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.MapParser} — transforms
 *       the parsed value.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.FlatMapParser} —
 *       sequences parsers whose choice depends on a prior parse result.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.AndParser} — sequences
 *       two parsers and collects both results.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.OrParser} — tries the
 *       left parser first, falling back to the right on failure.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.OptionalParser} —
 *       wraps a parser's result in an {@link java.util.Optional}.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.CardinalParser} —
 *       applies a parser zero-or-more or one-or-more times.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.ChainParser} — applies
 *       a fixed sequence of parsers in order.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.LazyParser} — defers
 *       parser construction for recursive grammars.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.ErrorParser} —
 *       intercepts failures and wraps them with a richer error message.</li>
 * </ul>
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.parsers;

import org.jspecify.annotations.NullMarked;
