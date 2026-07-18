package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.CardinalParser;

import java.util.List;

/**
 * Combinator that applies a parser a variable number of times, collecting all
 * results into a {@link java.util.List}.
 *
 * @param <Output> the type of value produced by each individual parse
 * @param <Input> the type of element consumed from the input
 */
public interface CardinalityCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that applies this parser <em>zero or more</em> times,
	 * collecting each result into an unmodifiable {@link java.util.List}.
	 *
	 * <p>Parsing stops (without failure) as soon as the underlying parser
	 * throws a {@link ParserError}.
	 *
	 * @return a new {@link CardinalParser} that always succeeds
	 */
	public default Parser<List<Output>, Input> zeroOrMore() {
		final var logger = getLogger();
		logger.debug("building `zeroOrMore` parser");
		return new CardinalParser.ZeroOrMore<>(getParser(), logger);
	}

	/**
	 * Creates a parser that applies this parser <em>one or more</em> times,
	 * collecting each result into an unmodifiable {@link java.util.List}.
	 *
	 * <p>The first application is mandatory; if it fails a {@link ParserError}
	 * is propagated. Subsequent applications stop as soon as the underlying
	 * parser fails.
	 *
	 * @return a new {@link CardinalParser} that requires at least one match
	 */
	public default Parser<List<Output>, Input> oneOrMore() {
		final var logger = getLogger();
		logger.debug("building `oneOrMore` parser");
		return new CardinalParser.OneOrMore<>(getParser(), logger);
	}
}
