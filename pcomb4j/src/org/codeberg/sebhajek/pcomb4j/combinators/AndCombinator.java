package org.codeberg.sebhajek.pcomb4j.combinators;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserResult.Sequence;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.codeberg.sebhajek.pcomb4j.parsers.AndParser;
import org.codeberg.sebhajek.pcomb4j.parsers.MapParser;

public interface AndCombinator<OutputLeft, Input>
  extends DelegateParser<OutputLeft, Input> {

	public default<OutputRight> AndParser<OutputLeft, OutputRight, Input> and(
	  final Parser<OutputRight, Input> parserOther
	) {
		final var logger = getLogger();
		logger.debug("building `and` parser");
		return new AndParser<>(getParser(), parserOther, logger);
	}

	public default<OutputRight> MapParser
	  .Transform<OutputLeft, Sequence<OutputLeft, OutputRight>, Input>
	  andFirst(final Parser<OutputRight, Input> parserOther) {
		final var andParser = this.and(parserOther);
		return andParser.map(andParser::discardSecond);
	}

	public default<OutputRight> MapParser
	  .Transform<OutputRight, Sequence<OutputLeft, OutputRight>, Input>
	  andSecond(final Parser<OutputRight, Input> parserOther) {
		final var andParser = this.and(parserOther);
		return andParser.map(andParser::discardFirst);
	}
}
