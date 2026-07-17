package org.codeberg.sebhajek.pcomb4j.combinators;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.codeberg.sebhajek.pcomb4j.parsers.OrParser;

public interface OrCombinator<OutputLeft, Input>
  extends DelegateParser<OutputLeft, Input> {

	public default OrParser.Or<OutputLeft, Input> or(
	  @SuppressWarnings("unchecked")
	  final Parser<OutputLeft, Input>... parserOthers
	) {
		if (parserOthers.length <= 0) {
			throw new IllegalArgumentException("Expected at least one parser");
		}
		final var logger = getLogger();
		logger.debug("building `or` parser");
		var orParser = new OrParser.Or<>(getParser(), parserOthers[0], logger);
		for (var idx = 1; idx < parserOthers.length; ++idx) {
			orParser = new OrParser.Or<>(orParser, parserOthers[idx], logger);
		}
		return orParser;
	}

	public default OrParser.Or<OutputLeft, Input> or(
	  final Parser<OutputLeft, Input> parserOther
	) {
		final var logger = getLogger();
		logger.debug("building `or` parser");
		return new OrParser.Or<>(getParser(), parserOther, logger);
	}

	public default<OutputRight> OrParser.Either<OutputLeft, OutputRight, Input>
	orElse(final Parser<OutputRight, Input> parserOther) {
		final var logger = getLogger();
		logger.debug("building `orElse` parser");
		return new OrParser.Either<>(getParser(), parserOther, logger);
	}
}
