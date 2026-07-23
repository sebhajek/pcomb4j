package io.github.sebhajek.pcomb4j.parsers.cardinal;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;

import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

import java.util.function.Predicate;

public final class CardinalParserBuilder {

	private CardinalParserBuilder() {}

	public static <Output, Input> Start<Output, Input> of(
	  final Parser<Output, Input> element
	) {
		Objects.requireNonNull(element, "element must not be null");
		return new Start<>(element);
	}

	// --- stage 1: Start ------------------------------------------------

	public static final class Start<Output, Input> {
		private final Parser<Output, Input> element;

		private Start(final Parser<Output, Input> element) {
			this.element = element;
		}

		public CardinalityStage<Output, Input> logger(final Logger logger) {
			Objects.requireNonNull(logger, "logger must not be null");
			return new CardinalityStage<>(element, logger);
		}
	}

	// --- stage 2: CardinalityStage -------------------------------------

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

		public SeparatorOptional<Output, Void, Input> zeroOrMore() {
			return new SeparatorOptional<>(
			  element, new Cardinality.ZeroOrMore<>(),
			  new Separator.None<>(), logger
			);
		}

		public SeparatorOptional<Output, Void, Input> oneOrMore() {
			return new SeparatorOptional<>(
			  element, new Cardinality.OneOrMore<>(),
			  new Separator.None<>(), logger
			);
		}

		public SeparatorOptional<Output, Void, Input> exactly(final int count) {
			return new SeparatorOptional<>(
			  element, new Cardinality.Exactly<>(count),
			  new Separator.None<>(), logger
			);
		}

		public SeparatorOptional<Output, Void, Input> until(
		  final Predicate<Output> predicate
		) {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.Until<>(
			    new Sentinel.PredicateBased<>(predicate), false
			  ),
			  new Separator.None<>(), logger
			);
		}

		public SeparatorOptional<Output, Void, Input> atLeastOne(
		  final Predicate<Output> predicate
		) {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.Until<>(
			    new Sentinel.PredicateBased<>(predicate), true
			  ),
			  new Separator.None<>(), logger
			);
		}

		public SeparatorOptional<Output, Void, Input> until(
		  final Parser<Output, Input> sentinelParser
		) {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.Until<>(
			    new Sentinel.ParserBased<>(sentinelParser), false
			  ),
			  new Separator.None<>(), logger
			);
		}

		public SeparatorOptional<Output, Void, Input> atLeastOne(
		  final Parser<Output, Input> sentinelParser
		) {
			return new SeparatorOptional<>(
			  element,
			  new Cardinality.Until<>(
			    new Sentinel.ParserBased<>(sentinelParser), true
			  ),
			  new Separator.None<>(), logger
			);
		}
	}

	// --- stage 3: SeparatorOptional ------------------------------------

	public static final class SeparatorOptional<Output, OutputSeparator, Input> {
		private final Parser<Output, Input>             element;
		private final Cardinality<Output, Input>        cardinality;
		private final Separator<OutputSeparator, Input>  separator;
		private final Logger                            logger;

		private SeparatorOptional(
		  final Parser<Output, Input>             element,
		  final Cardinality<Output, Input>        cardinality,
		  final Separator<OutputSeparator, Input>  separator,
		  final Logger                            logger
		) {
			this.element     = element;
			this.cardinality = cardinality;
			this.separator   = separator;
			this.logger      = logger;
		}

		public <NewSeparator> SeparatorFixed<Output, NewSeparator, Input>
		separatedBy(final Parser<NewSeparator, Input> separatorParser) {
			return separatedBy(separatorParser, false);
		}

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
			  element, cardinality,
			  new Separator.Between<>(separatorParser, trailingAccepted),
			  logger
			);
		}

		public CombinatorParser<List<Output>, Input> build() {
			return new CardinalParser<>(
			  element, cardinality, separator, logger
			);
		}

		public CombinatorParser<Void, Input> buildDiscarding() {
			return new DiscardingParser<>(
			  new CardinalParser<>(element, cardinality, separator, logger),
			  logger
			);
		}
	}

	// --- stage 4: SeparatorFixed ---------------------------------------

	public static final class SeparatorFixed<Output, OutputSeparator, Input> {
		private final Parser<Output, Input>             element;
		private final Cardinality<Output, Input>        cardinality;
		private final Separator<OutputSeparator, Input>  separator;
		private final Logger                            logger;

		private SeparatorFixed(
		  final Parser<Output, Input>             element,
		  final Cardinality<Output, Input>        cardinality,
		  final Separator<OutputSeparator, Input>  separator,
		  final Logger                            logger
		) {
			this.element     = element;
			this.cardinality = cardinality;
			this.separator   = separator;
			this.logger      = logger;
		}

		public CombinatorParser<List<Output>, Input> build() {
			return new CardinalParser<>(
			  element, cardinality, separator, logger
			);
		}

		public CombinatorParser<Void, Input> buildDiscarding() {
			return new DiscardingParser<>(
			  new CardinalParser<>(element, cardinality, separator, logger),
			  logger
			);
		}
	}

	// --- shared discarding parser --------------------------------------

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
			final var result = delegate.parse(parserInput);
			return new ParserResult<>((Void) null, result.remainder());
		}

		@Override
		public Parser<Void, Input> getParser() { return this; }

		@Override
		public Logger getLogger() { return logger; }
	}
}
