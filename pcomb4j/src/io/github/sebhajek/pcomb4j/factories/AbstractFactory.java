package io.github.sebhajek.pcomb4j.factories;

import io.github.sebhajek.pcomb4j.ParserFactory;

/**
 * Composite factory interface that aggregates all concrete factory mix-ins
 * into a single entry-point type.
 *
 * <p>{@link ParserFactory} implements this
 * interface to provide a one-stop shop for constructing all built-in parsers.
 */
public interface AbstractFactory
  extends AnyFactory, ChainFactory, LazyFactory, LiteralFactory {}
