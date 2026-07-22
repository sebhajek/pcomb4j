package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.LookAheadParser;

public interface LookAheadCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default<OutputAhead> CombinatorParser<Output, Input> lookAhead(
	  Parser<OutputAhead, Input> parserAhead
	) {
		final var logger = getLogger();
		logger.info("building `lookAhead` parser");
		return new LookAheadParser<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserAhead),
		  logger
		);
	}
}
