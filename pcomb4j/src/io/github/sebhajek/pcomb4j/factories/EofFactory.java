package io.github.sebhajek.pcomb4j.factories;

import io.github.sebhajek.pcomb4j.parsers.EofParser;

/**
 * Factory mix-in that creates {@link EofParser} instances.
 *
 * <p>An {@code eof} parser succeeds only when the input is exhausted, matching
 * the logical end-of-file condition.
 */
public interface EofFactory extends LoggedFactory {

	/**
	 * Creates an {@link EofParser} that succeeds only if the input is empty.
	 *
	 * @param <Input> the type of element consumed from the input
	 * @return a new {@link EofParser}
	 */
	public default<Input> EofParser<Input> eof() {
		final var logger = getLogger();
		logger.info("building `eof` parser");
		return new EofParser<>(logger);
	}
}
