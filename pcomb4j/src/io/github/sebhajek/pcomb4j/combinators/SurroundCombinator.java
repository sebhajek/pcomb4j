package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.parsers.CombinatorParser;
import io.github.sebhajek.pcomb4j.parsers.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.sequence.SurroundParserMandatory;
import io.github.sebhajek.pcomb4j.parsers.sequence.SurroundParserOptional;

/**
 * Combinator that wraps this parser's result with surrounding delimiters,
 * discarding the delimiter results.
 *
 * <p>Two modes are available:
 *
 * <ul>
 *   <li>{@link #surround(Parser, Parser)} delimiters are mandatory; if either
 * fails, the entire parse fails. <li>{@link #surroundOptional(Parser, Parser)}
 * delimiters are optional; if a delimiter fails, it is silently skipped.
 * </ul>
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public interface SurroundCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that matches this parser surrounded by {@code
	 * parserLeft} and {@code parserRight}, both of which are
	 * <strong>required</strong>.
	 *
	 * <p>The delimiter results are discarded; only this parser's result is
	 * returned.
	 *
	 * @param parserLeft the required left delimiter; never {@code null}
	 * @param parserRight the required right delimiter; never {@code null}
	 * @param <DiscardLeft> the output type of the left delimiter
	 * @param <DiscardRight> the output type of the right delimiter
	 * @return a new {@link SurroundParserMandatory} wrapping this parser
	 */
	public default<DiscardLeft, DiscardRight> CombinatorParser<Output, Input>
	                                          surround(
	                                            final Parser<DiscardLeft, Input> parserLeft,
	                                            final Parser<DiscardRight, Input> parserRight
	                                          ) {

		final var logger = getLogger();
		logger.info("building `surround` parser");
		return new SurroundParserMandatory<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserLeft),
		  DelegateParser.getDelegate(parserRight),
		  logger
		);
	}

	/**
	 * Creates a parser that matches this parser surrounded by the same
	 * delimiter on both sides, both of which are <strong>required</strong>.
	 *
	 * <p>Convenience overload of {@link #surround(Parser, Parser)} for
	 * symmetric delimiters.
	 *
	 * @param parserOuter the delimiter used on both sides; never {@code null}
	 * @param <Discard> the output type of the delimiter
	 * @return a new {@link SurroundParserMandatory} wrapping this parser
	 */
	public default<Discard> CombinatorParser<Output, Input> surround(
	  final Parser<Discard, Input> parserOuter
	) {
		return surround(parserOuter, parserOuter);
	}

	/**
	 * Creates a parser that matches this parser surrounded by {@code
	 * parserLeft} and {@code parserRight}, both of which are
	 * <strong>optional</strong>.
	 *
	 * <p>If either delimiter fails, it is silently skipped and parsing
	 * continues. The delimiter results are discarded; only this parser's result
	 * is returned.
	 *
	 * @param parserLeft the optional left delimiter; never {@code null}
	 * @param parserRight the optional right delimiter; never {@code null}
	 * @param <DiscardLeft> the output type of the left delimiter
	 * @param <DiscardRight> the output type of the right delimiter
	 * @return a new {@link SurroundParserOptional} wrapping this parser
	 */
	public default<DiscardLeft, DiscardRight> CombinatorParser<Output, Input>
	                                          surroundOptional(
	                                            final Parser<DiscardLeft, Input> parserLeft,
	                                            final Parser<DiscardRight, Input> parserRight
	                                          ) {

		final var logger = getLogger();
		logger.info("building `surroundOptional` parser");
		return new SurroundParserOptional<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserLeft),
		  DelegateParser.getDelegate(parserRight),
		  logger
		);
	}

	/**
	 * Creates a parser that matches this parser surrounded by the same
	 * delimiter on both sides, both of which are <strong>optional</strong>.
	 *
	 * <p>Convenience overload of {@link #surroundOptional(Parser, Parser)} for
	 * symmetric delimiters.
	 *
	 * @param parserOuter the delimiter used on both sides; never {@code null}
	 * @param <Discard> the output type of the delimiter
	 * @return a new {@link SurroundParserOptional} wrapping this parser
	 */
	public default<Discard> CombinatorParser<Output, Input> surroundOptional(
	  final Parser<Discard, Input> parserOuter
	) {
		return surroundOptional(parserOuter, parserOuter);
	}
}
