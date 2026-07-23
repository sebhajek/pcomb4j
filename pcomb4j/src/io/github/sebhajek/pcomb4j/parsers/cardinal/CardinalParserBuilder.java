package io.github.sebhajek.pcomb4j.parsers.cardinal;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.CombinatorParser;

import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

import java.util.function.Predicate;

/**
 * A type-driven staged builder that enforces method-call ordering at compile
 * time.
 *
 * <p>Usage: {@code
 * CardinalParserBuilder.of(element).logger(logger).zeroOrMore()…}
 *
 * <p>The stages are:
 * <ol>
 *   <li>{@link Start} — receive the element parser
 *   <li>{@link CardinalityStage} — choose cardinality
 *   <li>{@link SeparatorOptional} — optionally add a separator, then build
 *   <li>{@link SeparatorFixed} — separator already chosen, only build remains
 * </ol>
 */
public final class CardinalParserBuilder {

	private CardinalParserBuilder() {}

	/**
	 * Begins building a cardinal parser with the given element parser.
	 *
	 * @param element the parser for each individual element; never
	 *   {@code null}
	 * @param <Output> the type of value produced by the element parser
	 * @param <Input> the type of element consumed from the input
	 * @return the next stage, expecting a logger
	 */
	public static <Output, Input> Start<Output, Input> of(
	  final Parser<Output, Input> element
	) {
		Objects.requireNonNull(element, "element must not be null");
		return new Start<>(element);
	}

	/**
	 * Stage 1 — holds the element parser; the only method exposes the logger.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class Start<Output, Input> {
		private final Parser<Output, Input> element;

		private Start(final Parser<Output, Input> element) {
			this.element = element;
		}

		/**
		 * Supplies the logger and advances to the cardinality-selection stage.
		 *
		 * @param logger the logger for debug output; never {@code null}
		 * @return the next stage
		 */
		public CardinalityStage<Output, Input> logger(final Logger logger) {
			Objects.requireNonNull(logger, "logger must not be null");
			return new CardinalityStage<>(element, logger);
		}
	}

	/**
	 * Stage 2 — choose the repetition cardinality.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class CardinalityStage<Output, Input> {
		private final Parser<Output, Input> element;
		private final Logger                logger;

		private CardinalityStage(
		  final Parser<Output, Input> element,
		  final Logger                logger
		) {
			this.element = element;
			this.logger  = logger;
		}

		/**
		 * Zero or more repetitions (like {@code *} in regex).
		 *
		 * @return the next stage, where a separator may be added
		 */
		public SeparatorOptional<Output, Void, Input> zeroOrMore() {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.ZeroOrMore<>(),
			  new Separator.None<>(),
			  logger
			);
		}

		/**
		 * One or more repetitions (like {@code +} in regex).
		 *
		 * @return the next stage, where a separator may be added
		 */
		public SeparatorOptional<Output, Void, Input> oneOrMore() {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.OneOrMore<>(),
			  new Separator.None<>(),
			  logger
			);
		}

		/**
		 * Exactly {@code count} repetitions.
		 *
		 * @param count the number of repetitions to require
		 * @return the next stage, where a separator may be added
		 */
		public SeparatorOptional<Output, Void, Input> exactly(final int count) {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.Exactly<>(count),
			  new Separator.None<>(),
			  logger
			);
		}

		/**
		 * Zero or more repetitions, stopping when a parsed element satisfies
		 * the given predicate.
		 *
		 * @param predicate the condition that marks the sentinel; never
		 *   {@code null}
		 * @return the next stage, where a separator may be added
		 */
		public SeparatorOptional<Output, Void, Input> until(
		  final Predicate<Output> predicate
		) {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.Until<>(
			    new Sentinel.PredicateBased<>(predicate), false
			  ),
			  new Separator.None<>(),
			  logger
			);
		}

		/**
		 * One or more repetitions, stopping when a parsed element satisfies
		 * the given predicate.
		 *
		 * @param predicate the condition that marks the sentinel; never
		 *   {@code null}
		 * @return the next stage, where a separator may be added
		 */
		public SeparatorOptional<Output, Void, Input> atLeastOne(
		  final Predicate<Output> predicate
		) {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.Until<>(
			    new Sentinel.PredicateBased<>(predicate), true
			  ),
			  new Separator.None<>(),
			  logger
			);
		}

		/**
		 * Zero or more repetitions, stopping when the sentinel parser matches
		 * on the <em>next</em> input position.
		 *
		 * @param sentinelParser the parser that recognises the sentinel; never
		 *   {@code null}
		 * @return the next stage, where a separator may be added
		 */
		public SeparatorOptional<Output, Void, Input> until(
		  final Parser<Output, Input> sentinelParser
		) {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.Until<>(
			    new Sentinel.ParserBased<>(sentinelParser), false
			  ),
			  new Separator.None<>(),
			  logger
			);
		}

		/**
		 * One or more repetitions, stopping when the sentinel parser matches
		 * on the <em>next</em> input position.
		 *
		 * @param sentinelParser the parser that recognises the sentinel; never
		 *   {@code null}
		 * @return the next stage, where a separator may be added
		 */
		public SeparatorOptional<Output, Void, Input> atLeastOne(
		  final Parser<Output, Input> sentinelParser
		) {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.Until<>(
			    new Sentinel.ParserBased<>(sentinelParser), true
			  ),
			  new Separator.None<>(),
			  logger
			);
		}
	}

	/**
	 * Stage 3 — separator choice is still optional; you may call {@code
	 * separatedBy}, {@code separatedByTrailing}, or go straight to {@code
	 * build} / {@code buildDiscarding}.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <OutputSeparator> the type of value produced by the separator
	 *   parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class SeparatorOptional<
	  Output,
	  OutputSeparator,
	  Input> {
		private final Parser<Output, Input> element;
		private final Cardinality<Output, Input> cardinality;
		private final Separator<OutputSeparator, Input> separator;
		private final Logger                            logger;

		private SeparatorOptional(
		  final Parser<Output, Input> element,
		  final Cardinality<Output, Input> cardinality,
		  final Separator<OutputSeparator, Input> separator,
		  final Logger                            logger
		) {
			this.element     = element;
			this.cardinality = cardinality;
			this.separator   = separator;
			this.logger      = logger;
		}

		/**
		 * Elements must be separated by the given parser (trailing separator
		 * not accepted).
		 *
		 * @param separatorParser the parser that matches the delimiter; never
		 *   {@code null}
		 * @param <NewSeparator> the type produced by the separator parser
		 * @return the final stage, with only build / buildDiscarding available
		 */
		public <NewSeparator> SeparatorFixed<Output, NewSeparator, Input>
		separatedBy(final Parser<NewSeparator, Input> separatorParser) {
			return separatedBy(separatorParser, false);
		}

		/**
		 * Elements must be separated by the given parser; a trailing
		 * separator is accepted.
		 *
		 * @param separatorParser the parser that matches the delimiter; never
		 *   {@code null}
		 * @param <NewSeparator> the type produced by the separator parser
		 * @return the final stage, with only build / buildDiscarding available
		 */
		public <NewSeparator> SeparatorFixed<Output, NewSeparator, Input>
		separatedByTrailing(final Parser<NewSeparator, Input> separatorParser) {
			return separatedBy(separatorParser, true);
		}

		private <NewSeparator> SeparatorFixed<Output, NewSeparator, Input>
		                       separatedBy(
		                         final Parser<NewSeparator, Input> separatorParser,
		                         final boolean                     trailingAccepted
		                       ) {
			return new SeparatorFixed<>(
			  element,
			  cardinality,
			  new Separator.Between<>(separatorParser, trailingAccepted),
			  logger
			);
		}

		/**
		 * Builds the parser, collecting results into a {@link List}.
		 *
		 * @return the finished parser
		 */
		public CombinatorParser<List<Output>, Input> build() {
			return new CardinalParser<>(
			  element, cardinality, separator, logger
			);
		}

		/**
		 * Builds the parser and discards the collected results, returning
		 * {@code Void}.
		 *
		 * @return the finished parser that discards the matched values
		 */
		public CombinatorParser<Void, Input> buildDiscarding() {
			return new DiscardingParser<>(
			  new CardinalParser<>(element, cardinality, separator, logger),
			  logger
			);
		}
	}

	/**
	 * Stage 4 — separator already chosen; only {@link #build} and {@link
	 * #buildDiscarding} remain.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <OutputSeparator> the type of value produced by the separator
	 *   parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class SeparatorFixed<Output, OutputSeparator, Input> {
		private final Parser<Output, Input> element;
		private final Cardinality<Output, Input> cardinality;
		private final Separator<OutputSeparator, Input> separator;
		private final Logger                            logger;

		private SeparatorFixed(
		  final Parser<Output, Input> element,
		  final Cardinality<Output, Input> cardinality,
		  final Separator<OutputSeparator, Input> separator,
		  final Logger                            logger
		) {
			this.element     = element;
			this.cardinality = cardinality;
			this.separator   = separator;
			this.logger      = logger;
		}

		/**
		 * Builds the parser, collecting results into a {@link List}.
		 *
		 * @return the finished parser
		 */
		public CombinatorParser<List<Output>, Input> build() {
			return new CardinalParser<>(
			  element, cardinality, separator, logger
			);
		}

		/**
		 * Builds the parser and discards the collected results, returning
		 * {@code Void}.
		 *
		 * @return the finished parser that discards the matched values
		 */
		public CombinatorParser<Void, Input> buildDiscarding() {
			return new DiscardingParser<>(
			  new CardinalParser<>(element, cardinality, separator, logger),
			  logger
			);
		}
	}

	/**
	 * Wraps a {@link CardinalParser} and discards the parsed result, returning
	 * only the remainder.
	 */
	private static final class DiscardingParser<Input>
	  implements CombinatorParser<Void, Input> {

		private final CardinalParser<?, ?, Input> delegate;
		private final Logger                      logger;

		DiscardingParser(
		  final CardinalParser<?, ?, Input> delegate,
		  final Logger                      logger
		) {
			this.delegate = delegate;
			this.logger   = logger;
		}

		@Override
		public ParserResult<Void, Input> parse(
		  final ParserInput<Input> parserInput
		) throws ParserError {
			final var logger = getLogger();
			logger.debug("processing `discard` parser");
			final var result = delegate.parse(parserInput);
			logger.trace("`discard` parser succeeded");
			@SuppressWarnings("NullAway")
			final var voidResult = new ParserResult<>((Void) null, result.remainder());
			return voidResult;
		}

		@Override
		public Parser<Void, Input> getParser() {
			return this;
		}

		@Override
		public Logger getLogger() {
			return logger;
		}
	}
}
