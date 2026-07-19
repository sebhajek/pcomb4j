package io.github.sebhajek.pcomb4j.factories;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.parsers.FailureParser;

import java.util.function.Supplier;

/**
 * Factory mix-in that creates {@link FailureParser} instances.
 *
 * <p>A {@code failure} parser always throws a {@link ParserError} regardless of
 * the input, using a
 * {@link Supplier} to produce the error on each call.
 */
public interface FailureFactory extends LoggedFactory {

	/**
	 * Creates a {@link FailureParser} that always fails with an error supplied
	 * by {@code supplier}.
	 *
	 * @param supplier a supplier of the {@link ParserError} to throw; never
	 *   {@code null}
	 * @param <Input> the type of element consumed from the input
	 * @return a new {@link FailureParser}
	 */
	public default<Input> FailureParser<Input, Input> alwaysFails(
	  final Supplier<ParserError> supplier
	) {
		final var logger = getLogger();
		logger.info("building `failure` parser");
		return new FailureParser<>(supplier, logger);
	}
}
