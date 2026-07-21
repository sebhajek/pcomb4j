package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.NotParser;

public interface NotCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default<OutputNegative> CombinatorParser<Output, Input> not(
	  final Parser<OutputNegative, Input> parserNegative
	) {
		final var logger = getLogger();
		logger.info("building `not` parser");
		return new NotParser<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserNegative),
		  logger
		);
	}
}
