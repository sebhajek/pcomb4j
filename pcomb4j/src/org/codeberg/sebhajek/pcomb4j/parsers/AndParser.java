package org.codeberg.sebhajek.pcomb4j.parsers;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.ParserResult.Sequence;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class AndParser<OutputLeft, OutputRight, Input> extends AbstractParser<
  ParserResult.Sequence<OutputLeft, OutputRight>,
  Input> {

	private final Parser<OutputLeft, Input> parserLeft;
	private final Parser<OutputRight, Input> parserRight;

	public AndParser(
	  final Parser<OutputLeft, Input> parserLeft,
	  final Parser<OutputRight, Input> parserRight,
	  final Logger                     logger
	) {
		super(logger);
		this.parserLeft  = parserLeft;
		this.parserRight = parserRight;
	}

	@Override
	public ParserResult<Sequence<OutputLeft, OutputRight>, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().info("processing `and` parser");
		final var resultLeft  = getResultLeft(parserInput);
		final var resultRight = getResultRight(resultLeft);
		return getResult(resultLeft, resultRight);
	}

	private final ParserResult<Sequence<OutputLeft, OutputRight>, Input>
	              getResult(
	                final ParserResult<OutputLeft, Input> resultLeft,
	                final ParserResult<OutputRight, Input> resultRight
	              ) {
		return new ParserResult<>(
		  new ParserResult.Sequence<>(
		    resultLeft.result(), resultRight.result()
		  ),
		  resultRight.remainder()
		);
	}

	private final ParserResult<OutputRight, Input> getResultRight(
	  final ParserResult<OutputLeft, Input> resultLeft
	) throws ParserError {
		final var resultRight = parserRight.parse(resultLeft.remainder());
		getLogger().debug(
		  "second of `and` parser succeeded: {}", resultRight.result()
		);
		return resultRight;
	}

	private final ParserResult<OutputLeft, Input> getResultLeft(
	  final ParserInput<Input> parserInput
	) throws ParserError {
		final var resultLeft = parserLeft.parse(parserInput);
		getLogger().debug(
		  "first of `and` parser succeeded: {}", resultLeft.result()
		);
		return resultLeft;
	}

	public final OutputRight
	discardFirst(final ParserResult.Sequence<OutputLeft, OutputRight> pair) {
		getLogger().debug(
		  "discarding the second part of `and` sequence: {}", pair
		);
		return pair.second();
	}

	public final OutputLeft
	discardSecond(final Sequence<OutputLeft, OutputRight> pair) {
		getLogger().debug(
		  "discarding the second part of `and` sequence: {}", pair
		);
		return pair.first();
	}
}
