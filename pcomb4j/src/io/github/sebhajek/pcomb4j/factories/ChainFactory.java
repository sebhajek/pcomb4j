package io.github.sebhajek.pcomb4j.factories;

import java.util.List;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.parsers.ChainParser;

/**
 * Factory mix-in that creates {@link ChainParser} instances.
 *
 * <p>A {@code chain} parser applies a fixed, ordered list of parsers in
 * sequence, collecting all of their results into a {@link List}.
 */
public interface ChainFactory extends LoggedFactory {

	/**
	 * Creates a {@link ChainParser} that applies each parser in
	 * {@code parsersList} in order, collecting all results.
	 *
	 * @param parsersList  the ordered list of parsers to apply; never
	 *                     {@code null} and must not be empty
	 * @param <A>          the output type shared by all parsers in the chain
	 * @param <B>          the type of element consumed from the input
	 * @return a new {@link ChainParser}
	 */
	public default<A, B> ChainParser<A, B> chain(
	  final List<Parser<A, B>> parsersList
	) {
		final var logger = getLogger();
		logger.debug("building `chain` of parsers");
		return new ChainParser<>(parsersList, logger);
	}
}
