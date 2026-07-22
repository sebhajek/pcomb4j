package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.AssociativeChainParser;

public interface AssociativeChainCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default<OutputInfix> CombinatorParser<Output, Input> chainLeft(
	  final Parser<OutputInfix, Input> parserInfix,
	  final AssociativeChainParser.Combiner<Output, OutputInfix> combiner
	) {
		final var logger = getLogger();
		logger.info("building `chainLeft` parser");
		return new AssociativeChainParser.Left<>(
		  DelegateParser.getDelegate(getParser()), parserInfix, combiner, logger
		);
	}

	public default<OutputInfix> CombinatorParser<Output, Input> chainRight(
	  final Parser<OutputInfix, Input> parserInfix,
	  final AssociativeChainParser.Combiner<Output, OutputInfix> combiner
	) {
		final var logger = getLogger();
		logger.info("building `chainRight` parser");
		return new AssociativeChainParser.Right<>(
		  DelegateParser.getDelegate(getParser()), parserInfix, combiner, logger
		);
	}
}
