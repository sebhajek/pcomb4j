/**
 * Provides primitive parser implementations.
 *
 * <ul>
 *   <li>{@link AnyParser} — consumes any single input element unconditionally.
 *   <li>{@link EofParser} — succeeds only when the input is exhausted.
 *   <li>{@link FailureParser} — always fails with a supplied error.
 *   <li>{@link LiteralParser} — matches an exact value.
 *   <li>{@link SetParser} — base for {@link SetParserAny} and {@link
 *       SetParserNone} which match based on set membership.
 * </ul>
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.parsers.primitive;

import org.jspecify.annotations.NullMarked;