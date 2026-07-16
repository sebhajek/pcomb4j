package org.codeberg.sebhajek.pcomb4j.combinators;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserCombinator;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.codeberg.sebhajek.pcomb4j.parsers.AndParser;

public interface AndCombinator<A, B> extends DelegateParser<A, B> {

	public default<C> AndParser<A, C, B> and(final Parser<C, B> parserOther) {
		final var logger = getLogger();
		logger.debug("building `and` parser");
		return new AndParser<A, C, B>(getParser(), parserOther, logger);
	}

	public default<C> ParserCombinator<A, B> andFirst(
	  final Parser<C, B> parserOther
	) {
		final var andParser = this.and(parserOther);
		return andParser.map(andParser::discardSecond);
	}

	public default<C> ParserCombinator<C, B> andSecond(
	  final Parser<C, B> parserOther
	) {
		final var andParser = this.and(parserOther);
		return andParser.map(andParser::discardFirst);
	}
}
