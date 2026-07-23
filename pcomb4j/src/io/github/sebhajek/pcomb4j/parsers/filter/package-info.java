/**
 * Provides parsers that filter or validate parse results.
 *
 * <ul>
 *   <li>{@link FilterParser} -- forwards the inner result only if a predicate
 *       over the parsed value is satisfied.
 *   <li>{@link FilterParserLookAhead} -- validates the next input element
 *       (without consuming it) after the inner parser succeeds.
 *   <li>{@link NotParser} -- succeeds only when a negative parser fails on the
 *       same input (negative lookahead).
 *   <li>{@link LookAheadParser} -- validates the remainder against a second
 *       ahead parser.
 *   <li>{@link FilterParserNotSatisfied} -- error thrown when a filter
 * predicate is not met. <li>{@link NotParserNegativeParserSuccess} -- error
 * thrown when the negative parser unexpectedly succeeds.
 * </ul>
 *
 * <p>All types in this package are null-marked; see
 * {@link org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.parsers.filter;

import org.jspecify.annotations.NullMarked;