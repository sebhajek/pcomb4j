package io.github.sebhajek.pcomb4j.parsers.primitive;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.Set;

public final class SetParserAny<Input> extends SetParser<Input> {

	public SetParserAny(Set<Input> inputSet, Logger logger) {
		super(inputSet, logger);
	}

	@Override
	public ParserResult<Input, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `anyOf` parser");
		final var current = parserInput.getCurrent();
		if (getInputSet().contains(current)) {
			logger.trace("`anyOf` parser succeeded: {}", current);
			return new ParserResult<Input, Input>(
			  current, parserInput.advance()
			);
		} else {
			logger.trace("`anyOf` parser failed: {}", current);
			throw new SetParserNotMatched();
		}
	}
}
