package org.codeberg.sebhajek.pcomb4j.combinators;

import java.util.function.Predicate;

import org.codeberg.sebhajek.pcomb4j.ParserCombinator;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface FilterCombinator<A, B> extends DelegateParser<A, B> {

	public static class NotSatisfied extends ParserError {}

	public static final Logger LOGGER =
	  LoggerFactory.getLogger(FilterCombinator.class);

	public default ParserCombinator<A, B> filter(final Predicate<A> predicate) {
		LOGGER.debug("building `filter` parser");
		final var logger = getLogger();
		return ParserCombinator.withLogger(logger).from((input) -> {
			logger.info("processing `filter` parser");
			final var parserResult = getParser().parse(input);
			if (predicate.test(parserResult.result())) {
				logger.debug(
				  "`filter` parser succeeded: {}", parserResult.result()
				);
				return parserResult;
			} else {
				logger.debug("`filter` parser failed: {}", input.getCurrent());
				throw new NotSatisfied();
			}
		});
	}
}
