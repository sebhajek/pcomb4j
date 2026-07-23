package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.ParserResult.Sequence;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.sequence.AndParser;
import io.github.sebhajek.pcomb4j.parsers.MapParser;

/**
 * Combinator that sequences two parsers, collecting both of their results.
 *
 * <p>The {@link #and(Parser)} method creates an {@link AndParser} that runs
 * this parser first and then runs {@code parserOther} on the remaining input,
 * returning a {@link Sequence} of both values.
 *
 * <p>{@link #andFirst(Parser)} and {@link #andSecond(Parser)} are convenience
 * variants that automatically discard one of the two results.
 *
 * @param <OutputLeft> the type of value produced by this (left) parser
 * @param <Input> the type of element consumed from the input
 */
public interface AndCombinator<OutputLeft, Input>
  extends DelegateParser<OutputLeft, Input> {

	/**
	 * Sequences this parser with {@code parserOther}, collecting both results
	 * as a {@link Sequence}.
	 *
	 * @param parserOther the parser to run after this one; never {@code null}
	 * @param <OutputRight> the type of value produced by the second parser
	 * @return a new {@link AndParser} that produces a {@link Sequence}{@code
	 *   <OutputLeft,
	 *     OutputRight>}
	 */
	public default<OutputRight>
	  CombinatorParser<ParserResult.Sequence<OutputLeft, OutputRight>, Input>
	  and(final Parser<OutputRight, Input> parserOther) {
		final var logger = getLogger();
		logger.info("building `and` parser");
		return new AndParser<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserOther),
		  logger
		);
	}

	/**
	 * Sequences this parser with {@code parserOther} and discards the
	 * <em>right</em> (second) result, returning only this parser's value.
	 *
	 * @param parserOther the parser to run after this one; never {@code null}
	 * @param <OutputRight> the type of value produced by the second parser
	 *   (discarded)
	 * @return a {@link MapParser.Transform} that produces {@code OutputLeft}
	 */
	public default<OutputRight> CombinatorParser<OutputLeft, Input> andFirst(
	  final Parser<OutputRight, Input> parserOther
	) {
		final var andParser = this.and(parserOther);
		if (
		  andParser instanceof AndParser<OutputLeft, OutputRight, Input> parser
		) {
			return parser.map(parser::discardSecond);
		} else throw new IllegalStateException();
	}

	/**
	 * Sequences this parser with {@code parserOther} and discards the
	 * <em>left</em> (first) result, returning only {@code parserOther}'s value.
	 *
	 * @param parserOther the parser to run after this one; never {@code null}
	 * @param <OutputRight> the type of value produced by the second parser
	 * @return a {@link MapParser.Transform} that produces {@code OutputRight}
	 */
	public default<OutputRight> CombinatorParser<OutputRight, Input> andSecond(
	  final Parser<OutputRight, Input> parserOther
	) {
		final var andParser = this.and(parserOther);
		if (
		  andParser instanceof AndParser<OutputLeft, OutputRight, Input> parser
		) {
			return parser.map(parser::discardFirst);
		} else throw new IllegalStateException();
	}
}
