package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.decorator.OptionalParser;

import java.util.Optional;

/**
 * Combinator that makes this parser optional, wrapping its result in a {@link
 * java.util.Optional}.
 *
 * <p>If this parser fails, an {@link java.util.Optional#empty()} is returned
 * without consuming any input. If it succeeds, the result is wrapped in {@link
 * java.util.Optional#of(Object)}.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public interface OptionalCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates an {@link OptionalParser} that wraps this parser's result in a
	 * {@link java.util.Optional}, never failing.
	 *
	 * @return a new {@link OptionalParser}
	 */
	public default CombinatorParser<Optional<Output>, Input> optional() {
		final var logger = getLogger();
		logger.info("building `optional` parser");
		return new OptionalParser<>(
		  DelegateParser.getDelegate(getParser()), logger
		);
	}

	/**
	 * Creates an {@link OptionalParser.Default} that returns the given {@code
	 * defaultValue} when this parser fails, never failing.
	 *
	 * @param defaultValue the value to return when this parser fails
	 * @return a new {@link OptionalParser.Default}
	 */
	public default CombinatorParser<Output, Input> optionalDefault(
	  final Output defaultValue
	) {
		final var logger = getLogger();
		logger.info("building `optionalDefault` parser");
		return new OptionalParser.Default<>(
		  DelegateParser.getDelegate(getParser()), defaultValue, logger
		);
	}
}
