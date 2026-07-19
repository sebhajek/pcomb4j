/**
 * Provides factory interfaces for constructing ready-made {@link
 * io.github.sebhajek.pcomb4j.Parser} instances.
 *
 * <p>Each interface exposes one or more factory methods (e.g. {@code any},
 * {@code literal}, {@code chain}, {@code lazy}) as default methods that are
 * composed into {@link io.github.sebhajek.pcomb4j.factories.AbstractFactory},
 * the base of the public {@link io.github.sebhajek.pcomb4j.ParserFactory} entry
 * point.
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.factories;

import org.jspecify.annotations.NullMarked;
