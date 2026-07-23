package io.github.sebhajek.pcomb4j.parsers.decorator;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.function.BiFunction;

/**
 * Wraps the original error in an {@link ErrorLabeledError} with a dynamically
 * computed message.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input>  the type of element consumed from the input
 */
public final class ErrorParserMessage<Output, Input>
  extends ErrorParser<Output, Input> {

	private final BiFunction<ParserInput<Input>, ParserError, String>
	              messageFactory;

	/**
	 * Creates a new {@code ErrorParserMessage}.
	 *
	 * @param messageFactory function computing the label string
	 * @param parserSource   inner parser to delegate to
	 * @param logger         logger for debug output
	 */
	public ErrorParserMessage(
	  final BiFunction<ParserInput<Input>, ParserError, String> messageFactory,
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
		this.messageFactory = messageFactory;
	}

	/** {@inheritDoc} */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().debug("processing `error` (message) parser");
		try {
			return getParserSource().parse(parserInput);
		} catch (final ParserError errorInner) {
			final var message =
			  getMessageFactory().apply(parserInput, errorInner);
			throw new ErrorLabeledError(message, errorInner);
		}
	}

	/**
	 * Returns the message factory function.
	 *
	 * @return the message factory; never {@code null}
	 */
	BiFunction<ParserInput<Input>, ParserError, String> getMessageFactory() {
		return messageFactory;
	}
}
