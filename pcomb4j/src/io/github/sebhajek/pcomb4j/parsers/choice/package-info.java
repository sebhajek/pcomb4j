/**
 * Provides parsers for ordered choice (alternation).
 *
 * <ul>
 *   <li>{@link OrParser} -- tries the left parser first; on failure tries the
 *       right parser on the same input; both branches produce the same output
 *       type.
 *   <li>{@link EitherParser} -- like {@link OrParser} but the branches may
 *       produce different output types; the result is wrapped in a {@link
 *       io.github.sebhajek.pcomb4j.ParserResult.Either}.
 *   <li>{@link NeitherWasSuccessful} -- error thrown when both branches fail,
 *       preserving both child errors.
 * </ul>
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.parsers.choice;

import org.jspecify.annotations.NullMarked;