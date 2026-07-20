package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.FlatMapParser;

import java.util.function.Function;

/**
 * Combinator that sequences this parser with a second parser whose
 * <em>identity</em> depends on the first parser's result (monadic bind).
 *
 * <p>This is the parser-combinator equivalent of {@code flatMap} / {@code
 * bind}: the {@code binder} function is called with the result of this parser
 * and must return the next parser to apply.
 *
 * @param <Output> the type of value produced by this (source) parser
 * @param <Input> the type of element consumed from the input
 */
public interface FlatMapCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that first applies this parser, then feeds the result to
	 * {@code binder} to obtain the next parser, and finally applies that next
	 * parser to the remaining input.
	 *
	 * @param binder a function from this parser's output to the next parser;
	 *   never {@code null}
	 * @param <OutputNext> the type produced by the second parser
	 * @return a new {@link FlatMapParser}
	 */
	public default<OutputNext> CombinatorParser<OutputNext, Input> flatMap(
	  final Function<Output, Parser<OutputNext, Input>> binder
	) {
		final var logger = getLogger();
		logger.info("building `flatMap` parser");
		return new FlatMapParser<>(
		  DelegateParser.getDelegate(getParser()), binder, logger
		);
	}
}
