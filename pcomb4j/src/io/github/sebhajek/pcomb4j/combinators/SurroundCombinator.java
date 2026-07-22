package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.SurroundParser;

public interface SurroundCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default<DiscardLeft, DiscardRight> CombinatorParser<Output, Input>
	                                          surround(
	                                            final Parser<DiscardLeft, Input> parserLeft,
	                                            final Parser<DiscardRight, Input> parserRight
	                                          ) {

		final var logger = getLogger();
		logger.info("building `surround` parser");
		return new SurroundParser.Mandatory<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserLeft),
		  DelegateParser.getDelegate(parserRight),
		  logger
		);
	}

	public default<Discard> CombinatorParser<Output, Input> surround(
	  final Parser<Discard, Input> parserOuter
	) {
		return surround(parserOuter, parserOuter);
	}

	public default<DiscardLeft, DiscardRight> CombinatorParser<Output, Input>
	                                          surroundOptional(
	                                            final Parser<DiscardLeft, Input> parserLeft,
	                                            final Parser<DiscardRight, Input> parserRight
	                                          ) {

		final var logger = getLogger();
		logger.info("building `surroundOptional` parser");
		return new SurroundParser.Optional<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserLeft),
		  DelegateParser.getDelegate(parserRight),
		  logger
		);
	}

	public default<Discard> CombinatorParser<Output, Input> surroundOptional(
	  final Parser<Discard, Input> parserOuter
	) {
		return surroundOptional(parserOuter, parserOuter);
	}
}
