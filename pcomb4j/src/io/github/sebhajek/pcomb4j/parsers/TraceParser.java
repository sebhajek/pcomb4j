package io.github.sebhajek.pcomb4j.parsers;

import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;

public class TraceParser<Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	private final Optional<Logger> loggerDedicated;
	private final Optional<String> message;

	public TraceParser(
	  final String message,
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		this(message, null, parserSource, logger);
	}

	public TraceParser(
	  final Logger loggerDedicated,
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		this(null, loggerDedicated, parserSource, logger);
	}

	public TraceParser(
	  final String message,
	  final Logger loggerDedicated,
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
		this.message         = Optional.ofNullable(message);
		this.loggerDedicated = Optional.ofNullable(loggerDedicated);
	}

	protected Optional<Logger> getLoggerDedicated() { return loggerDedicated; }

	protected Optional<String> getMessage() { return message; }

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
			logger.info("parser: `{}` @ {}", message.orElse(""), parserInput);
		} else {
			logger.info("parser @ {}", parserInput);
		}
	}
}
