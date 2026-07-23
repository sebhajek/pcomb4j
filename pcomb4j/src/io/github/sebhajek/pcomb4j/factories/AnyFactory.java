package io.github.sebhajek.pcomb4j.factories;

import io.github.sebhajek.pcomb4j.parsers.primitive.AnyParser;

/**
 * Factory mix-in that creates {@link AnyParser} instances.
 *
 * <p>An {@code any} parser succeeds on any single element and consumes it,
 * effectively acting as a wildcard match.
 */
public interface AnyFactory extends LoggedFactory {

	/**
	 * Creates an {@link AnyParser} that matches and consumes any single input
	 * element, failing only if the input is empty.
	 *
	 * @param <Input> the type of element to consume
	 * @return a new {@link AnyParser}
	 */
	public default<Input> AnyParser<Input> any() {
		final var logger = getLogger();
		logger.info("building `any` parser");
		return new AnyParser<>(logger);
	}
}
