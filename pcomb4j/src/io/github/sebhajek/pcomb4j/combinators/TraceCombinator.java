package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.TraceParser;

import org.slf4j.Logger;

/**
 * Combinator that inserts debug tracing around a parse attempt.
 *
 * <p>Three variants are provided:
 *
 * <ul>
 *   <li>{@link #trace(String)} — logs a fixed message at info level.
 *   <li>{@link #trace(Logger)} — logs to a dedicated logger.
 *   <li>{@link #trace(String, Logger)} — logs a fixed message to a dedicated
 * logger.
 * </ul>
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public interface TraceCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that logs {@code message} and delegates to a dedicated
	 * logger on each parse call.
	 *
	 * @param message the message to log
	 * @param loggerDedicated the logger to use for trace output; never {@code
	 *   null}
	 * @return a new {@link TraceParser}
	 */
	public default CombinatorParser<Output, Input>
	trace(String message, Logger loggerDedicated) {
		final var logger = getLogger();
		logger.info("building `trace` parser");
		return new TraceParser<>(
		  message,
		  loggerDedicated,
		  DelegateParser.getDelegate(getParser()),
		  logger
		);
	}

	/**
	 * Creates a parser that logs the current input to a dedicated logger on
	 * each parse call.
	 *
	 * @param loggerDedicated the logger to use for trace output; never {@code
	 *   null}
	 * @return a new {@link TraceParser}
	 */
	public default CombinatorParser<Output, Input> trace(
	  Logger loggerDedicated
	) {
		final var logger = getLogger();
		logger.info("building `trace` parser");
		return new TraceParser<>(
		  loggerDedicated, DelegateParser.getDelegate(getParser()), logger
		);
	}

	/**
	 * Creates a parser that logs {@code message} on each parse call.
	 *
	 * @param message the message to log
	 * @return a new {@link TraceParser}
	 */
	public default CombinatorParser<Output, Input> trace(String message) {
		final var logger = getLogger();
		logger.info("building `trace` parser");
		return new TraceParser<>(
		  message, DelegateParser.getDelegate(getParser()), logger
		);
	}
}
