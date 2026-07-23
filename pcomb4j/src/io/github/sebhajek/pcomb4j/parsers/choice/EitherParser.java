package io.github.sebhajek.pcomb4j.parsers.choice;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

public final class EitherParser<OutputLeft, OutputRight, Input>
  extends AlternationParser<
    ParserResult.Either<OutputLeft, OutputRight>,
    OutputLeft,
    OutputRight,
    Input> {

	public EitherParser(
	  final Parser<OutputLeft, Input> parserLeft,
	  final Parser<OutputRight, Input> parserRight,
	  final Logger                     logger
	) {
		super(parserLeft, parserRight, logger);
	}

	@Override
	public ParserResult<ParserResult.Either<OutputLeft, OutputRight>, Input>
	parse(@NonNull final ParserInput<Input> input) throws ParserError {
		getLogger().debug("processing `either` parser");
		try {
			final var result = getParserLeft().parse(input);
			getLogger().debug(
			  "first `either` parser succeeded: {}", result.result()
			);
			return new ParserResult<>(
			  new ParserResult.Either.Left<>(result.result()),
			  result.remainder()
			);
		} catch (final ParserError errorLeft) {
			try {
				final var result = getParserRight().parse(input);
				getLogger().debug(
				  "second `either` parser succeeded: {}", result.result()
				);
				return new ParserResult<>(
				  new ParserResult.Either.Right<>(result.result()),
				  result.remainder()
				);
			} catch (final ParserError errorRight) {
				throw new NeitherWasSuccessful(errorLeft, errorRight);
			}
		}
	}
}
