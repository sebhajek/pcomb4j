package org.codeberg.sebhajek.pcomb4j.factories;

import java.util.List;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.parsers.ChainParser;

public interface ChainFactory extends LoggedFactory {

	public default<A, B> ChainParser<A, B> chain(
	  final List<Parser<A, B>> parsersList
	) {
		final var logger = getLogger();
		logger.debug("building `chain` of parsers");
		return new ChainParser<>(parsersList, logger);
	}
}
