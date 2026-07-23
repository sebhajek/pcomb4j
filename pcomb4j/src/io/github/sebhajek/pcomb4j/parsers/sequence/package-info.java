/**
 * Provides parsers for sequencing and surrounding parsers with delimiters.
 *
 * <ul>
 *   <li>{@link AndParser} -- sequences two parsers, collecting both results as
 *       a {@link io.github.sebhajek.pcomb4j.ParserResult.Sequence}.
 *   <li>{@link SurroundParser} -- base for {@link SurroundParserMandatory} and
 *       {@link SurroundParserOptional} which wrap a source parser with
 *       required or optional left/right delimiters.
 * </ul>
 *
 * <p>All types in this package are null-marked; see
 * {@link org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.parsers.sequence;

import org.jspecify.annotations.NullMarked;