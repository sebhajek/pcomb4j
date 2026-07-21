package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.NotParser;

/**
 * Combinator that succeeds only when its inner parser succeeds <em>and</em> a
 * given <em>negative</em> parser fails on the same input.
 *
 * <p>This is the logical "not" operation for parsers. It is expressed via the
 * {@link #not(Parser)} method, which accepts a parser to reject.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public interface NotCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that succeeds only when this parser succeeds
	 * <em>and</em> {@code parserNegative} fails on the same input.
	 *
	 * <p>If the negative parser succeeds, a {@link
	 * NotParser.NegativeParserSuccess} error is thrown.
	 *
	 * @param parserNegative the parser that must fail; never {@code null}
	 * @param <OutputNegative> the output type of the negative parser
	 * @return a new {@link NotParser} wrapping this parser
	 */
	public default<OutputNegative> CombinatorParser<Output, Input> not(
	  final Parser<OutputNegative, Input> parserNegative
	) {
		final var logger = getLogger();
		logger.info("building `not` parser");
		return new NotParser<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserNegative),
		  logger
		);
	}
}
