package io.github.sebhajek.pcomb4j.parsers.decorator;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.slf4j.Logger;

import java.util.Optional;

/**
 * A {@link Parser} that logs a trace message before delegating to an inner
 * parser, useful for debugging parse progress.
 *
 * <p>The trace message is logged at info level using either a dedicated {@link
 * Logger} or the parser's own logger.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public class TraceParser<Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	private final Optional<Logger> loggerDedicated;
	private final Optional<String> message;

	/**
	 * Creates a new {@code TraceParser} that logs a fixed message using the
	 * parser's own logger.
	 *
	 * @param message the message to log; never {@code null}
	 * @param parserSource the inner parser to delegate to; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public TraceParser(
	  final String message,
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		this(message, null, parserSource, logger);
	}

	/**
	 * Creates a new {@code TraceParser} that logs using a dedicated logger.
	 *
	 * @param loggerDedicated the logger to use for trace output; never {@code
	 *   null}
	 * @param parserSource the inner parser to delegate to; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public TraceParser(
	  final Logger loggerDedicated,
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		this(null, loggerDedicated, parserSource, logger);
	}

	/**
	 * Creates a new {@code TraceParser} with full configuration.
	 *
	 * @param message the optional message to log; may be {@code null}
	 * @param loggerDedicated the optional dedicated logger; may be {@code null}
	 * @param parserSource the inner parser to delegate to; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public TraceParser(
	  final @Nullable String message,
	  final @Nullable Logger loggerDedicated,
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
		this.message         = Optional.ofNullable(message);
		this.loggerDedicated = Optional.ofNullable(loggerDedicated);
	}

	/**
	 * Returns the dedicated logger, if any.
	 *
	 * @return an {@link Optional} containing the dedicated logger, or empty
	 */
	protected Optional<Logger> getLoggerDedicated() { return loggerDedicated; }

	/**
	 * Returns the trace message, if any.
	 *
	 * @return an {@link Optional} containing the message, or empty
	 */
	protected Optional<String> getMessage() { return message; }

	/**
	 * Logs the trace message (if configured) and delegates parsing to the inner
	 * parser.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return the result produced by the inner parser
	 * @throws ParserError if the inner parser fails
	 */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		logMessage(parserInput);
		return getParserSource().parse(parserInput);
	}

	private final void logMessage(final ParserInput<Input> parserInput) {
		final var logger  = getLoggerDedicated().orElseGet(this::getLogger);
		final var message = getMessage();
		if (message.isPresent()) {
			logger.trace("parser: `{}` @ {}", message.orElse(""), parserInput);
		} else {
			logger.trace("parser @ {}", parserInput);
		}
	}
}
