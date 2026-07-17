package org.codeberg.sebhajek.pcomb4j.combinators;

import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.codeberg.sebhajek.pcomb4j.parsers.CardinalParser;

public interface CardinalityCombinator<Output, Input>
		extends DelegateParser<Output, Input> {

	public default CardinalParser<Output, Input> zeroOrMore() {
		final var logger = getLogger();
		logger.debug("building `zeroOrMore` parser");
		return new CardinalParser.ZeroOrMore<>(getParser(), logger);
	}

	public default CardinalParser<Output, Input> oneOrMore() {
		final var logger = getLogger();
		logger.debug("building `oneOrMore` parser");
		return new CardinalParser.OneOrMore<>(getParser(), logger);
	}
}
