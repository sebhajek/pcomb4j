package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.cardinal.CardinalParserBuilder;
import io.github.sebhajek.pcomb4j.parsers.cardinal.CardinalParserBuilder.SeparatorOptional;

import java.util.function.Predicate;

public interface CardinalityCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default SeparatorOptional<Output, Void, Input> zeroOrMore() {
		final var logger = getLogger();
		logger.info("building `zeroOrMore` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .zeroOrMore();
	}

	public default SeparatorOptional<Output, Void, Input> oneOrMore() {
		final var logger = getLogger();
		logger.info("building `oneOrMore` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .oneOrMore();
	}

	public default SeparatorOptional<Output, Void, Input> exactly(int count) {
		final var logger = getLogger();
		logger.info("building `exactly` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .exactly(count);
	}

	public default SeparatorOptional<Output, Void, Input> until(
	  final Predicate<Output> predicate
	) {
		final var logger = getLogger();
		logger.info("building `until` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .until(predicate);
	}

	public default SeparatorOptional<Output, Void, Input> atLeastOne(
	  final Predicate<Output> predicate
	) {
		final var logger = getLogger();
		logger.info("building `atLeastOne` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .atLeastOne(predicate);
	}

	public default SeparatorOptional<Output, Void, Input> until(
	  final Parser<Output, Input> sentinelParser
	) {
		final var logger = getLogger();
		logger.info("building `until` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .until(sentinelParser);
	}

	public default SeparatorOptional<Output, Void, Input> atLeastOne(
	  final Parser<Output, Input> sentinelParser
	) {
		final var logger = getLogger();
		logger.info("building `atLeastOne` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .atLeastOne(sentinelParser);
	}
}
