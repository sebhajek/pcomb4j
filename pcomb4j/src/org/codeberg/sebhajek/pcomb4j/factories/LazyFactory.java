package org.codeberg.sebhajek.pcomb4j.factories;

import java.util.function.Supplier;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.parsers.LazyParser;

public interface LazyFactory extends LoggedFactory {

	public default<A, B> LazyParser<A, B> lazy(
	  final Supplier<Parser<A, B>> supplier
	) {
		final var logger = getLogger();
		logger.info("building `lazy` parser");
		return new LazyParser<>(supplier, logger);
	}
}
