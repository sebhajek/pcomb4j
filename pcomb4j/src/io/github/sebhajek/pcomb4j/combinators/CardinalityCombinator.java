package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.cardinal.CardinalParserBuilder;
import io.github.sebhajek.pcomb4j.parsers.cardinal.CardinalParserBuilder.SeparatorOptional;

import java.util.function.Predicate;

/**
 * Provides default methods for building cardinal (repeating) parsers.
 *
 * <p>Methods such as {@link #zeroOrMore}, {@link #oneOrMore}, {@link #exactly},
 * {@link #until}, and {@link #atLeastOne} delegate to the type-driven {@link
 * CardinalParserBuilder} and return a {@link SeparatorOptional} stage, allowing
 * an optional separator before finalising with {@code .build()} or {@code
 * .buildDiscarding()}.
 *
 * @param <Output> the type of value produced by the underlying parser
 * @param <Input> the type of element consumed from the input
 */
public interface CardinalityCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that matches zero or more repetitions of the delegate.
	 *
	 * @return a builder stage that optionally accepts a separator
	 */
	public default SeparatorOptional<Output, Void, Input> zeroOrMore() {
		final var logger = getLogger();
		logger.info("building `zeroOrMore` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .zeroOrMore();
	}

	/**
	 * Creates a parser that matches one or more repetitions of the delegate.
	 *
	 * @return a builder stage that optionally accepts a separator
	 */
	public default SeparatorOptional<Output, Void, Input> oneOrMore() {
		final var logger = getLogger();
		logger.info("building `oneOrMore` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .oneOrMore();
	}

	/**
	 * Creates a parser that matches exactly {@code count} repetitions of the
	 * delegate.
	 *
	 * @param count the exact number of repetitions
	 * @return a builder stage that optionally accepts a separator
	 */
	public default SeparatorOptional<Output, Void, Input> exactly(int count) {
		final var logger = getLogger();
		logger.info("building `exactly` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .exactly(count);
	}

	/**
	 * Creates a parser that matches zero or more repetitions of the delegate,
	 * stopping when a parsed element satisfies the given predicate.
	 *
	 * @param predicate the condition that marks the sentinel; never
	 *   {@code null}
	 * @return a builder stage that optionally accepts a separator
	 */
	public default SeparatorOptional<Output, Void, Input> until(
	  final Predicate<Output> predicate
	) {
		final var logger = getLogger();
		logger.info("building `until` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .until(predicate);
	}

	/**
	 * Creates a parser that matches one or more repetitions of the delegate,
	 * stopping when a parsed element satisfies the given predicate.
	 *
	 * @param predicate the condition that marks the sentinel; never
	 *   {@code null}
	 * @return a builder stage that optionally accepts a separator
	 */
	public default SeparatorOptional<Output, Void, Input> atLeastOne(
	  final Predicate<Output> predicate
	) {
		final var logger = getLogger();
		logger.info("building `atLeastOne` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .atLeastOne(predicate);
	}

	/**
	 * Creates a parser that matches zero or more repetitions of the delegate,
	 * stopping when the sentinel parser matches on the <em>next</em> input
	 * position.
	 *
	 * @param sentinelParser the parser that recognises the sentinel; never
	 *   {@code null}
	 * @return a builder stage that optionally accepts a separator
	 */
	public default SeparatorOptional<Output, Void, Input> until(
	  final Parser<Output, Input> sentinelParser
	) {
		final var logger = getLogger();
		logger.info("building `until` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .until(sentinelParser);
	}

	/**
	 * Creates a parser that matches one or more repetitions of the delegate,
	 * stopping when the sentinel parser matches on the <em>next</em> input
	 * position.
	 *
	 * @param sentinelParser the parser that recognises the sentinel; never
	 *   {@code null}
	 * @return a builder stage that optionally accepts a separator
	 */
	public default SeparatorOptional<Output, Void, Input> atLeastOne(
	  final Parser<Output, Input> sentinelParser
	) {
		final var logger = getLogger();
		logger.info("building `atLeastOne` parser");
		return CardinalParserBuilder.of(DelegateParser.getDelegate(getParser()))
		  .logger(logger)
		  .atLeastOne(sentinelParser);
	}
}
