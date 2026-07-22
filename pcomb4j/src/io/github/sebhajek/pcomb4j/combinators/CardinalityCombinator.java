package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.CardinalParser;
import io.github.sebhajek.pcomb4j.parsers.ExactCountParser;
import io.github.sebhajek.pcomb4j.parsers.SeparatedCardinalParser;
import io.github.sebhajek.pcomb4j.parsers.SkipCardinalParser;
import io.github.sebhajek.pcomb4j.parsers.UntilCardinalParser;

import java.util.List;

import java.util.function.Predicate;

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
	public default CombinatorParser<List<Output>, Input> zeroOrMore() {
		final var logger = getLogger();
		logger.info("building `zeroOrMore` parser");
		return new CardinalParser.ZeroOrMore<>(
		  DelegateParser.getDelegate(getParser()), logger
		);
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
	public default CombinatorParser<List<Output>, Input> oneOrMore() {
		final var logger = getLogger();
		logger.info("building `oneOrMore` parser");
		return new CardinalParser.OneOrMore<>(
		  DelegateParser.getDelegate(getParser()), logger
		);
	}

	public default<OutputSeparator> CombinatorParser<List<Output>, Input>
	zeroOrMoreSeparated(final Parser<OutputSeparator, Input> separator) {
		final var logger = getLogger();
		logger.info("building `zeroOrMoreSeparated` parser");
		return new SeparatedCardinalParser.ZeroOrMore<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(separator),
		  logger
		);
	}

	public default<OutputSeparator> CombinatorParser<List<Output>, Input>
	oneOrMoreSeparated(final Parser<OutputSeparator, Input> separator) {
		final var logger = getLogger();
		logger.info("building `oneOrMoreSeparated` parser");
		return new SeparatedCardinalParser.OneOrMore<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(separator),
		  logger
		);
	}

	public default<OutputSeparator> CombinatorParser<List<Output>, Input>
	                                zeroOrMoreSeparatedTrailing(
	                                  final Parser<OutputSeparator, Input> separator
	                                ) {
		final var logger = getLogger();
		logger.info("building `zeroOrMoreSeparatedTrailing` parser");
		return new SeparatedCardinalParser.ZeroOrMoreTrailing<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(separator),
		  logger
		);
	}

	public default<OutputSeparator> CombinatorParser<List<Output>, Input>
	oneOrMoreSeparatedTrailing(final Parser<OutputSeparator, Input> separator) {
		final var logger = getLogger();
		logger.info("building `oneOrMoreSeparatedTrailing` parser");
		return new SeparatedCardinalParser.OneOrMoreTrailing<>(
		  DelegateParser.getDelegate(getParser()),
		  DelegateParser.getDelegate(separator),
		  logger
		);
	}

	public default CombinatorParser<Void, Input> zeroOrMoreSkip() {
		final var logger = getLogger();
		logger.info("building `zeroOrMoreSkip` parser");
		return new SkipCardinalParser.ZeroOrMore<>(
		  DelegateParser.getDelegate(getParser()), logger
		);
	}

	public default CombinatorParser<Void, Input> oneOrMoreSkip() {
		final var logger = getLogger();
		logger.info("building `oneOrMoreSkip` parser");
		return new SkipCardinalParser.OneOrMore<>(
		  DelegateParser.getDelegate(getParser()), logger
		);
	}

	public default CombinatorParser<List<Output>, Input> zeroOrMoreUntil(
	  final Predicate<Output> predicateSentinel
	) {
		final var logger = getLogger();
		logger.info("building `zeroOrMoreUntil` parser (predicate)");
		return new UntilCardinalParser.ZeroOrMore<>(
		  DelegateParser.getDelegate(getParser()),
		  new UntilCardinalParser.Sentinel.PredicateBased<>(predicateSentinel),
		  logger
		);
	}

	public default CombinatorParser<List<Output>, Input> oneOrMoreUntil(
	  final Predicate<Output> predicateSentinel
	) {
		final var logger = getLogger();
		logger.info("building `oneOrMoreUntil` parser (predicate)");
		return new UntilCardinalParser.OneOrMore<>(
		  DelegateParser.getDelegate(getParser()),
		  new UntilCardinalParser.Sentinel.PredicateBased<>(predicateSentinel),
		  logger
		);
	}

	public default CombinatorParser<List<Output>, Input> zeroOrMoreUntil(
	  final Parser<Output, Input> parserSentinel
	) {
		final var logger = getLogger();
		logger.info("building `zeroOrMoreUntil` parser (parser)");
		return new UntilCardinalParser.ZeroOrMore<>(
		  DelegateParser.getDelegate(getParser()),
		  new UntilCardinalParser.Sentinel.ParserBased<>(
		    DelegateParser.getDelegate(parserSentinel)
		  ),
		  logger
		);
	}

	public default CombinatorParser<List<Output>, Input> oneOrMoreUntil(
	  final Parser<Output, Input> parserSentinel
	) {
		final var logger = getLogger();
		logger.info("building `oneOrMoreUntil` parser (parser)");
		return new UntilCardinalParser.OneOrMore<>(
		  DelegateParser.getDelegate(getParser()),
		  new UntilCardinalParser.Sentinel.ParserBased<>(
		    DelegateParser.getDelegate(parserSentinel)
		  ),
		  logger
		);
	}

	public default CombinatorParser<List<Output>, Input> exactCount(
	  final int count
	) {
		final var logger = getLogger();
		logger.info("building `exactCount({})` parser", count);
		return new ExactCountParser<>(
		  DelegateParser.getDelegate(getParser()), count, logger
		);
	}
}
