package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.LookAheadParser;

/**
 * Combinator that first succeeds a source parser, then validates the remainder
 * of the input against an ahead parser (discarding its result).
 *
 * <p>This is the look-ahead semantic for parsers. It is expressed via the
 * {@link #lookAhead(Parser)} method, which accepts a parser to validate the
 * the remaining input.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public interface LookAheadCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that succeeds when this parser succeeds and the given
	 * ahead parser also succeeds on the remaining input.
	 *
	 * <p>The ahead parser's result is discarded; only this parser's result is
	 * returned.
	 *
	 * @param parserAhead the parser that validates the remaining input; never
	 *          {@code null}
	 * @param <OutputAhead> the output type of the ahead parser
	 * @return a new {@link LookAheadParser} wrapping this parser
	 */
	public default<OutputAhead> CombinatorParser<Output, Input> lookAhead(
	  final Parser<OutputAhead, Input> parserAhead
	) {
		final var logger = getLogger();
		logger.info("building `lookAhead` parser");
		return new LookAheadParser<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserAhead),
		  logger
		);
	}
}
