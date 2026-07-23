package io.github.sebhajek.pcomb4j.factories;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.parsers.sequence.ChainParser;

import java.util.List;

/**
 * Factory mix-in that creates {@link ChainParser} instances.
 *
 * <p>A {@code chain} parser applies a fixed, ordered list of parsers in
 * sequence, collecting all of their results into a {@link List}.
 */
public interface ChainFactory extends LoggedFactory {

	/**
	 * Creates a {@link ChainParser} that applies each parser in {@code
	 * parsersList} in order, collecting all results.
	 *
	 * @param parsersList the ordered list of parsers to apply; never {@code
	 *   null} and must not be
	 *     empty
	 * @param <Output> the output type shared by all parsers in the chain
	 * @param <Input> the type of element consumed from the input
	 * @return a new {@link ChainParser}
	 */
	public default<Output, Input> ChainParser<Output, Input> chain(
	  final List<Parser<Output, Input>> parsersList
	) {
		final var logger = getLogger();
		logger.info("building `chain` of parsers");
		return new ChainParser<>(parsersList, logger);
	}
}
