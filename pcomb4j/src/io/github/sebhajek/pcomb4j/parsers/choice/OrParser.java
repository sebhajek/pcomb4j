package io.github.sebhajek.pcomb4j.parsers.choice;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractPairParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * An alternation parser where both branches produce the same output type.
 *
 * <p>If the left parser succeeds its result is returned. If it fails the right
 * parser is tried on the same (unchanged) input. If both fail a
 * {@link NeitherWasSuccessful} error is thrown.
 *
 * @param <Output> the shared output type
 * @param <Input> the type of element consumed from the input
 */
public final class OrParser<Output, Input>
  extends AlternationParser<Output, Output, Output, Input> {

	/**
	 * Creates a new {@code OrParser}.
	 *
	 * @param parserLeft the first (preferred) parser; never {@code null}
	 * @param parserRight the fallback parser; never {@code null}
	 * @param logger the logger for debug output; never {@code null}
	 */
	public OrParser(
	  final Parser<Output, Input> parserLeft,
	  final Parser<Output, Input> parserRight,
	  final Logger                logger
	) {
		super(parserLeft, parserRight, logger);
	}

	/**
	 * Tries the left parser; on failure tries the right parser on the same
	 * input.
	 *
	 * @param input the input to parse; never {@code null}
	 * @return the result of whichever branch succeeded first
	 * @throws NeitherWasSuccessful if both branches fail
	 */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> input
	) throws ParserError {
		getLogger().debug("processing `or` parser");
		try {
			return getResultLeft(input);
		} catch (final ParserError errorLeft) {
			try {
				return getResultRight(input);
			} catch (final ParserError errorRight) {
				throw new NeitherWasSuccessful(errorLeft, errorRight);
			}
		}
	}

	private ParserResult<Output, Input> getResultRight(
	  final ParserInput<Input> input
	) throws ParserError {
		final var result = getParserRight().parse(input);
		getLogger().debug("second `or` parser succeeded: {}", result.result());
		return result;
	}

	private ParserResult<Output, Input> getResultLeft(
	  final ParserInput<Input> input
	) throws ParserError {
		final var result = getParserLeft().parse(input);
		getLogger().debug("first `or` parser succeeded: {}", result.result());
		return result;
	}
}