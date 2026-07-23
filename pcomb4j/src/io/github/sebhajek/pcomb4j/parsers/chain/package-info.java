/**
 * Provides parsers that apply a fixed sequence of parsers in order and parsers
 * for left- or right-associative chains of binary operators.
 *
 * <ul>
 *   <li>{@link ChainParser} -- applies an ordered list of parsers, collecting
 * all results. <li>{@link AssociativeChainParser} -- base for {@link
 * ChainParserLeft} and
 *       {@link ChainParserRight} which parse {@code term (operator term)*} and
 *       fold left or right respectively.
 * </ul>
 *
 * <p>All types in this package are null-marked; see {@link
 * org.jspecify.annotations.NullMarked}.
 */
@NullMarked
package io.github.sebhajek.pcomb4j.parsers.chain;

import org.jspecify.annotations.NullMarked;