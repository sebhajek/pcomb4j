/**
 * Provides concrete {@link io.github.sebhajek.pcomb4j.Parser} implementations
 * for primitive and structural parsing operations.
 *
 * <p>The parsers in this package are the building blocks from which larger
 * grammars are assembled via the combinators in the {@code
 * io.github.sebhajek.pcomb4j.combinators} package:
 *
 * <ul>
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.primitive.AnyParser} — consumes any
 * single element. <li>{@link io.github.sebhajek.pcomb4j.parsers.primitive.LiteralParser}
 * — matches an exact value. <li>{@link
 * io.github.sebhajek.pcomb4j.parsers.FilterParser} — forwards the inner result
 * only if a predicate is satisfied. <li>{@link
 * io.github.sebhajek.pcomb4j.parsers.map.MapParser} — transforms the parsed value.
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.map.FlatMapParser} — sequences
 * parsers whose choice depends on a prior parse result. <li>{@link
 * io.github.sebhajek.pcomb4j.parsers.sequence.AndParser} — sequences two parsers and
 * collects both results. <li>{@link
 * io.github.sebhajek.pcomb4j.parsers.OrParser} — tries the left parser first,
 * falling back to the right on failure. <li>{@link
 * io.github.sebhajek.pcomb4j.parsers.decorator.OptionalParser} — wraps a parser's result
 * in an
 *       {@link java.util.Optional}.
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.CardinalParser} — applies a
 * parser zero-or-more or one-or-more times. <li>{@link
 * io.github.sebhajek.pcomb4j.parsers.chain.ChainParser} — applies a fixed sequence of
 *       parsers in order.
 *   <li>{@link io.github.sebhajek.pcomb4j.parsers.decorator.LazyParser} — defers parser
 * construction for recursive grammars. <li>{@link
 * io.github.sebhajek.pcomb4j.parsers.decorator.ErrorParser} — intercepts failures and
 * wraps them with a richer error message. <li>{@link
 * io.github.sebhajek.pcomb4j.parsers.primitive.FailureParser} — always fails with a
 * supplied error, regardless of input. <li>{@link
 * io.github.sebhajek.pcomb4j.parsers.decorator.TraceParser} — logs a trace message before
 *       delegating to an inner parser.
 * </ul>
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.parsers;

import org.jspecify.annotations.NullMarked;
