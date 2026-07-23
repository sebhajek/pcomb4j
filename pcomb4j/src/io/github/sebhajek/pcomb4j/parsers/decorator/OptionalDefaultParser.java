package io.github.sebhajek.pcomb4j.parsers.decorator;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * A {@link Parser} that falls back to a default value when the inner parser
 * fails, never failing.
 *
 * <p>If the inner parser succeeds, its result is returned directly. If the
 * inner parser throws a {@link ParserError}, the error is silently swallowed
 * and {@code defaultValue} is returned instead — leaving the input unchanged.
 *
 * @param <Output> the type of value produced by the inner parser
 * @param <Input>  the type of element consumed from the input
 */
public class OptionalDefaultParser<Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	private final Output defaultValue;

	/**
	 * Creates a new {@code OptionalDefaultParser}.
	 *
	 * @param parserSource the inner parser to try; never {@code null}
	 * @param defaultValue the value to return when the inner parser fails
	 * @param logger       the logger used for debug output; never {@code null}
	 */
	public OptionalDefaultParser(
	  final Parser<Output, Input> parserSource,
	  final Output                defaultValue,
	  final Logger                logger
	) {
		super(parserSource, logger);
		this.defaultValue = defaultValue;
	}

	/**
	 * Tries the inner parser; on success returns the result directly, on
	 * failure returns {@code defaultValue} without consuming any input.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} whose value is always non-null
	 * @throws ParserError never — this parser always succeeds
	 */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `optionalDefault` parser");
		try {
			final var result = getParserSource().parse(parserInput);
			logger.trace(
			  "`optionalDefault` parser succeeded: {}", result.result()
			);
			return result;
		} catch (final ParserError err) {
			logger.trace("`optionalDefault` parser failed");
			return new ParserResult<>(getDefaultValue(), parserInput);
		}
	}

	/**
	 * Returns the default value used when the inner parser fails.
	 *
	 * @return the default value
	 */
	protected Output getDefaultValue() { return defaultValue; }
}
