/**
 * Provides combinator interfaces for composing {@link
 * io.github.sebhajek.pcomb4j.Parser} instances.
 *
 * <p>Each interface in this package adds one combinator operation as a default
 * method (e.g. {@code map}, {@code flatMap}, {@code filter}, {@code and},
 * {@code or}, {@code optional}, {@code zeroOrMore}). The interfaces are
 * assembled into the unified {@link
 * io.github.sebhajek.pcomb4j.interfaces.CombinatorParser} mixin, which is
 * implemented by {@link io.github.sebhajek.pcomb4j.ParserCombinator} and all
 * concrete parsers in the {@code io.github.sebhajek.pcomb4j.parsers} package.
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.combinators;

import org.jspecify.annotations.NullMarked;
