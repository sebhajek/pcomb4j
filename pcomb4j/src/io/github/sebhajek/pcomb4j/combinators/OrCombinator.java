package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.OrParser;

/**
 * Combinator that tries this parser first and, on failure, falls back to one or
 * more alternative parsers.
 *
 * <p>Two variants are provided:
 *
 * <ul>
 *   <li>{@link #or(Parser)} / {@link #or(Parser[])} — all alternatives must
 * produce the <em>same</em> output type; the result is just {@code Output}.
 *   <li>{@link #orElse(Parser)} — the two branches may produce
 * <em>different</em> output types; the result is wrapped in a {@link
 * ParserResult.Either}.
 * </ul>
 *
 * @param <OutputLeft> the type of value produced by this (left) parser
 * @param <Input> the type of element consumed from the input
 */
public interface OrCombinator<OutputLeft, Input>
  extends DelegateParser<OutputLeft, Input> {

	/**
	 * Creates a parser that tries this parser, then each of {@code
	 * parserOthers} in turn until one succeeds.
	 *
	 * <p>At least one alternative must be supplied.
	 *
	 * @param parserOthers one or more fallback parsers; none may be {@code
	 *   null}
	 * @return a left-associative chain of {@link OrParser.Or} instances
	 * @throws IllegalArgumentException if {@code parserOthers} is empty
	 */
	public default CombinatorParser<OutputLeft, Input> or(
	  @SuppressWarnings("unchecked")
	  final Parser<OutputLeft, Input>... parserOthers
	) {
		if (parserOthers.length <= 0) {
			throw new IllegalArgumentException("Expected at least one parser");
		}
		final var logger = getLogger();
		logger.debug("building `or` parser");
		var orParser = new OrParser.Or<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserOthers[0]),
		  logger
		);
		for (var idx = 1; idx < parserOthers.length; ++idx) {
			orParser = new OrParser.Or<>(
			  orParser, DelegateParser.getDelegate(parserOthers[idx]), logger
			);
		}
		return orParser;
	}

	/**
	 * Creates a parser that tries this parser and, on failure, tries {@code
	 * parserOther}.
	 *
	 * @param parserOther the fallback parser; never {@code null}
	 * @return a new {@link OrParser.Or}
	 */
	public default CombinatorParser<OutputLeft, Input> or(
	  final Parser<OutputLeft, Input> parserOther
	) {
		final var logger = getLogger();
		logger.debug("building `or` parser");
		return new OrParser.Or<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserOther),
		  logger
		);
	}

	/**
	 * Creates a parser that tries this parser and, on failure, tries {@code
	 * parserOther}. Unlike
	 * {@link #or}, the two parsers may produce <em>different</em> types; the
	 * result is a {@link ParserResult.Either}.
	 *
	 * @param parserOther the right (fallback) parser; never {@code null}
	 * @param <OutputRight> the type produced by the right parser
	 * @return a new {@link OrParser.Either}
	 */
	public default<OutputRight>
	  CombinatorParser<ParserResult.Either<OutputLeft, OutputRight>, Input>
	  orElse(final Parser<OutputRight, Input> parserOther) {
		final var logger = getLogger();
		logger.debug("building `orElse` parser");
		return new OrParser.Either<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(parserOther),
		  logger
		);
	}
}
