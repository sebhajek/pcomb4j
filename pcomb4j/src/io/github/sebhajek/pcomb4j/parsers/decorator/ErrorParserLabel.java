package io.github.sebhajek.pcomb4j.parsers.decorator;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * Wraps the original error in an {@link ErrorLabeledError} with a fixed static
 * message.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input>  the type of element consumed from the input
 */
public final class ErrorParserLabel<Output, Input>
  extends ErrorParser<Output, Input> {

	private final String message;

	/**
	 * Creates a new {@code ErrorParserLabel}.
	 *
	 * @param message      the static label to attach to any failure
	 * @param parserSource inner parser to delegate to
	 * @param logger       logger for debug output
	 */
	public ErrorParserLabel(
	  final String message,
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
		this.message = message;
	}

	/** {@inheritDoc} */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().debug("processing `error` (label) parser");
		try {
			return getParserSource().parse(parserInput);
		} catch (final ParserError errorInner) {
			throw new ErrorLabeledError(getMessage(), errorInner);
		}
	}

	/**
	 * Returns the static label message.
	 *
	 * @return the label; never {@code null}
	 */
	String getMessage() { return message; }
}
