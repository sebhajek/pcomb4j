/**
 * Provides concrete {@link io.github.sebhajek.pcomb4j.ParserInput}
 * implementations and supporting types.
 *
 * <p>The two primary implementations are:
 * <ul>
 *   <li>{@link io.github.sebhajek.pcomb4j.input.StringInput} — an
 *       immutable view over a {@link String} that tracks source position
 *       (line and column) via a {@link
 *       io.github.sebhajek.pcomb4j.input.Position} value.</li>
 *   <li>{@link io.github.sebhajek.pcomb4j.input.ListInput} — an immutable
 *       view over any {@link java.util.List} of elements.</li>
 * </ul>
 *
 * <p>The {@link io.github.sebhajek.pcomb4j.input.ParserInputError} sealed
 * hierarchy defines the errors that can be thrown when the input is
 * exhausted or cannot be read.
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.input;

import org.jspecify.annotations.NullMarked;
