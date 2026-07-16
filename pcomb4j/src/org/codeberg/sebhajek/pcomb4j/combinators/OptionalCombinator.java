package org.codeberg.sebhajek.pcomb4j.combinators;

import java.util.Optional;

import org.codeberg.sebhajek.pcomb4j.ParserCombinator;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface OptionalCombinator<A, B> extends DelegateParser<A, B> {

	public static final Logger LOGGER =
	  LoggerFactory.getLogger(OptionalCombinator.class);

	public default ParserCombinator<Optional<A>, B> optional() {
		LOGGER.debug("building `optional` parser");
		final var logger = getLogger();
		return ParserCombinator.withLogger(logger).from((input) -> {
			logger.info("processing `optional` parser");
			try {
				final var result = getParser().parse(input);
				logger.debug(
				  "`optional` parser succeeded: {}", result.result()
				);
				return result.with(Optional.of(result.result()));
			} catch (final ParserError err) {
				logger.debug("`optional` parser failed");
				return new ParserResult<>(Optional.empty(), input);
			}
		});
	}
}
