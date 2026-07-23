package io.github.sebhajek.pcomb4j.parsers.primitive;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.Set;

public final class SetParserNone<Input> extends SetParser<Input> {

	public SetParserNone(Set<Input> inputSet, Logger logger) {
		super(inputSet, logger);
	}

	@Override
	public ParserResult<Input, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `noneOf` parser");
		final var current = parserInput.getCurrent();
		if (getInputSet().contains(current)) {
			logger.trace("`noneOf` parser failed: {}", current);
			throw new SetParserNotMatched();
		} else {
			logger.trace("`noneOf` parser succeeded: {}", current);
			return new ParserResult<Input, Input>(
			  current, parserInput.advance()
			);
		}
	}
}
