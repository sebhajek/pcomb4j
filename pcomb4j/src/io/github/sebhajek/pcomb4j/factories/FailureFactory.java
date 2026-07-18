package io.github.sebhajek.pcomb4j.factories;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.parsers.FailureParser;

import java.util.function.Supplier;

public interface FailureFactory extends LoggedFactory {

	public default<Output, Input> FailureParser<Output, Input> alwaysFails(
	  final Supplier<ParserError> supplier
	) {
		final var logger = getLogger();
		logger.info("building `failure` parser");
		return new FailureParser<>(supplier, logger);
	}
}
