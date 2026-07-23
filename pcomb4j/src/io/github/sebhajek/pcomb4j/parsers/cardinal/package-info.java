/**
 * Provides the cardinality parser engine and its configuration policies.
 *
 * <ul>
 *   <li>{@link CardinalParser} -- the single unified engine that applies an
 *       element parser repeatedly, governed by a {@link Cardinality} policy
 *       and a {@link Separator} policy.
 *   <li>{@link Cardinality} -- sealed interface with policies {@link
 *       Cardinality.ZeroOrMore}, {@link Cardinality.OneOrMore}, {@link
 *       Cardinality.Exactly}, and {@link Cardinality.Until}.
 *   <li>{@link Separator} -- sealed interface with policies {@link
 *       Separator.None} and {@link Separator.Between}.
 *   <li>{@link Sentinel} -- sealed interface for sentinel-based termination
 *       ({@link Sentinel.PredicateBased}, {@link Sentinel.ParserBased}).
 *   <li>{@link CardinalParserBuilder} -- type-safe staged builder for
 *       constructing {@link CardinalParser} instances.
 * </ul>
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.parsers.cardinal;

import org.jspecify.annotations.NullMarked;