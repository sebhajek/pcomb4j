package io.github.sebhajek.pcomb4j.parsers.sequence;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

public final class SurroundParserMandatory<
  Output,
  DiscartedLeft,
  DiscartedRight,
  Input> extends SurroundParser<Output, DiscartedLeft, DiscartedRight, Input> {

	public SurroundParserMandatory(
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
		logger.debug("processing `surround` parser");
		final var inputLeft = getParserLeft().parse(parserInput).remainder();
		logger.trace("`surround` left parser succeeded");
		final var resultInner = getParserSource().parse(inputLeft);
		logger.trace(
		  "`surround` source parser succeeded: {}", resultInner.result()
		);
		final var inputRight =
		  getParserRight().parse(resultInner.remainder()).remainder();
		logger.trace("`surround` right parser succeeded");
		return new ParserResult<>(resultInner.result(), inputRight);
	}
}
