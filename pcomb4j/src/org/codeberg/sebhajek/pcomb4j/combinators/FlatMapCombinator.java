package org.codeberg.sebhajek.pcomb4j.combinators;

import java.util.function.Function;

import org.codeberg.sebhajek.pcomb4j.ParserCombinator;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface FlatMapCombinator<A, B> extends DelegateParser<A, B> {
	public static final Logger LOGGER =
	  LoggerFactory.getLogger(FlatMapCombinator.class);

	public default<C> ParserCombinator<C, B> flatMap(
	  final Function<A, ParserCombinator<C, B>> binder
	) {
		LOGGER.debug("building `flatMap` parser");
		final var logger = getLogger();
		return ParserCombinator.withLogger(logger).from(input -> {
			logger.info("processing `flatMap` parser");
			final var resultA = getParser().parse(input);
			logger.debug(
			  "initial `flatMap` parser succeeded: {}", resultA.result()
			);
			final var nextParser = binder.apply(resultA.result()).getParser();
			final var resultB    = nextParser.parse(resultA.remainder());
			logger.debug(
			  "bounded `flatMap` parser succeeded: {}", resultB.result()
			);
			return new ParserResult<>(resultB.result(), resultB.remainder());
		});
	}
}
