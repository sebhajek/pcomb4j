package io.github.sebhajek.pcomb4j.parsers.sequence;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

public final class SurroundParserOptional<
  Output,
  DiscartedLeft,
  DiscartedRight,
  Input> extends SurroundParser<Output, DiscartedLeft, DiscartedRight, Input> {

	public SurroundParserOptional(
	  final Parser<Output, Input> parserSource,
	  final Parser<DiscartedLeft, Input> parserLeft,
	  final Parser<DiscartedRight, Input> parserRight,
	  final Logger                        logger
	) {
		super(parserSource, parserLeft, parserRight, logger);
	}

	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `surroundOptional` parser");
		final var inputLeft   = getInputLeft(parserInput);
		final var resultInner = getParserSource().parse(inputLeft);
		final var inputRight  = getInputRight(resultInner.remainder());
		logger.trace(
		  "`surroundOptional` parser succeeded: {}", resultInner.result()
		);
		return new ParserResult<>(resultInner.result(), inputRight);
	}

	private @NonNull ParserInput<Input> getInputRight(
	  final ParserInput<Input> inputInner
	) {
		final var logger = getLogger();
		try {
			final var resultRight = getParserRight().parse(inputInner);
			logger.trace(
			  "`surroundOptional` right parser succeeded: {}",
			  resultRight.result()
			);
			return resultRight.remainder();
		} catch (final ParserError _) {
			logger.trace(
			  "`surroundOptional` right parser failed (optional, skipping)"
			);
		}
		return inputInner;
	}

	private @NonNull ParserInput<Input> getInputLeft(
	  @NonNull ParserInput<Input> input
	) {
		final var logger = getLogger();
		try {
			final var resultLeft = getParserLeft().parse(input);
			logger.trace(
			  "`surroundOptional` left parser succeeded: {}",
			  resultLeft.result()
			);
			input = resultLeft.remainder();
		} catch (final ParserError _) {
			logger.trace(
			  "`surroundOptional` left parser failed (optional, skipping)"
			);
		}
		return input;
	}
}
