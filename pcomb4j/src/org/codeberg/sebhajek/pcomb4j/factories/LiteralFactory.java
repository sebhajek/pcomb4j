package org.codeberg.sebhajek.pcomb4j.factories;

import java.util.Comparator;

import org.codeberg.sebhajek.pcomb4j.parsers.LiteralParser;

public interface LiteralFactory extends LoggedFactory {

	public default<A> LiteralParser<A> literal(final A value) {
		var logger = getLogger();
		logger.info("building `literal` parser");
		return new LiteralParser<>(value, logger);
	}

	public default<A> LiteralParser<A>
	                  literal(final A value, final Comparator<A> comparator) {
		var logger = getLogger();
		logger.info("building `literal` parser");
		return new LiteralParser<>(value, comparator, logger);
	}
}
