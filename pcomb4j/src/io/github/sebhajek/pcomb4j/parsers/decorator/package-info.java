/**
 * Provides decorator parsers that wrap or modify the behavior of other parsers.
 *
 * <ul>
 *   <li>{@link OptionalParser} -- wraps the inner parser's result in an {@link
 *       java.util.Optional}, never failing.
 *   <li>{@link OptionalDefaultParser} -- falls back to a default value when the
 *       inner parser fails, never failing.
 *   <li>{@link ErrorParserLabel}, {@link ErrorParserMessage}, {@link
 *       ErrorParserSupplied} -- intercept failures and replace or enrich the
 *       thrown {@link io.github.sebhajek.pcomb4j.ParserError}.
 *   <li>{@link ErrorLabeledError} -- wrapper error carrying a contextual label.
 *   <li>{@link TraceParser} -- logs a trace message before delegating.
 *   <li>{@link LazyParser} -- defers parser creation for recursive grammars.
 *   <li>{@link LazyParserMemoized} -- memoizes the inner parser after first
 *       evaluation.
 * </ul>
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.parsers.decorator;

import org.jspecify.annotations.NullMarked;
