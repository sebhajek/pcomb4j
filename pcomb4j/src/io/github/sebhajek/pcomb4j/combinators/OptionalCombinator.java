package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.OptionalParser;

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
		logger.debug("building `optional` parser");
		return new OptionalParser<>(getParser(), logger);
	}
}
