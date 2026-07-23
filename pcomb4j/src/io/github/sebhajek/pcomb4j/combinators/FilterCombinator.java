package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.ParserCombinator;
import io.github.sebhajek.pcomb4j.parsers.CombinatorParser;
import io.github.sebhajek.pcomb4j.parsers.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.filter.FilterParser;
import io.github.sebhajek.pcomb4j.parsers.filter.FilterParserLookAhead;

import java.util.function.Predicate;

/**
 * Combinator that forwards this parser's result only when a predicate is
 * satisfied, failing with
 * {@link io.github.sebhajek.pcomb4j.parsers.filter.FilterParserNotSatisfied}
 * otherwise.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public interface FilterCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that succeeds only when this parser succeeds
	 * <em>and</em> the resulting value satisfies {@code predicate}.
	 *
	 * @param predicate the condition the parsed value must satisfy; never
	 *   {@code null}
	 * @return a new {@link FilterParser} wrapping this parser
	 */
	public default CombinatorParser<Output, Input> filter(
	  final Predicate<Output> predicate
	) {
		final var logger = getLogger();
		logger.info("building `filter` parser");
		return new FilterParser<>(
		  DelegateParser.getDelegate(getParser()), predicate, logger
		);
	}

	/**
	 * Creates a parser that succeeds only when this parser succeeds
	 * <em>and</em> the resulting value equals {@code value}.
	 *
	 * @param value the exact value to match against
	 * @return a new {@link FilterParser} wrapping this parser
	 */
	public default CombinatorParser<Output, Input> filter(final Output value) {
		return filter(value::equals);
	}

	/**
	 * Creates a parser that succeeds only when this parser succeeds
	 * <em>and</em> the resulting value is an instance of {@code type}.
	 *
	 * @param type the target type to check via {@code isInstance}; never {@code
	 *   null}
	 * @param <OutputNarrow> the narrowed output type
	 * @return a new {@link FilterParser} wrapping this parser, typed as {@code
	 *   OutputNarrow}
	 */
	public default<OutputNarrow extends Output>
	  CombinatorParser<OutputNarrow, Input> filter(
	    final Class<OutputNarrow> type
	  ) {
		return ParserCombinator.withLogger(getLogger())
		  .from(filter(type::isInstance))
		  .cast(type);
	}

	/**
	 * Creates a parser that succeeds when this parser succeeds <em>and</em> the
	 * <strong>next</strong> input element (the first unconsumed element after
	 * this parser's match) satisfies {@code predicate}.
	 *
	 * <p>The look-ahead element is <strong>not</strong> consumed; only the
	 * inner parser's match is consumed from the input.
	 *
	 * @param predicate the condition the next input element must satisfy; never
	 *   {@code null}
	 * @return a new {@link FilterParserLookAhead} wrapping this parser
	 */
	public default CombinatorParser<Output, Input> filterLookAhead(
	  final Predicate<Input> predicate
	) {
		final var logger = getLogger();
		logger.info("building `filterLookAhead` parser");
		return new FilterParserLookAhead<>(
		  DelegateParser.getDelegate(getParser()), predicate, logger
		);
	}

	/**
	 * Creates a parser that succeeds when this parser succeeds <em>and</em> the
	 * <strong>next</strong> input element equals {@code value}.
	 *
	 * <p>Convenience overload of {@link #filterLookAhead(Predicate)} for
	 * equality checks.
	 *
	 * @param value the exact value the next input element must match
	 * @return a new {@link FilterParserLookAhead} wrapping this parser
	 */
	public default CombinatorParser<Output, Input> filterLookAhead(
	  final Input value
	) {
		final var logger = getLogger();
		logger.info("building `filterLookAhead` parser");
		return new FilterParserLookAhead<>(
		  DelegateParser.getDelegate(getParser()), value::equals, logger
		);
	}
}
