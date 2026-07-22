package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractPairParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

public abstract
  sealed class SurroundParser<Output, DiscartedLeft, DiscartedRight, Input>
  extends AbstractPairParser<Output, DiscartedLeft, DiscartedRight, Input> {

	public static final class Optional<
	  Output,
	  DiscartedLeft,
	  DiscartedRight,
	  Input>
	  extends SurroundParser<Output, DiscartedLeft, DiscartedRight, Input> {

		public Optional(
		  final Parser<Output, Input> parserSource,
		  final Parser<DiscartedLeft, Input> parserLeft,
		  final Parser<DiscartedRight, Input> parserRight,
		  final Logger                        logger
		) {
			super(parserSource, parserLeft, parserRight, logger);
		}

		@Override
		public ParserResult<Output, Input> parse(
		  @NonNull final ParserInput<Input> parserInput
		) throws ParserError {
			final var inputLeft   = getInputLeft(parserInput);
			final var resultInner = getParserSource().parse(inputLeft);
			final var inputRight  = getInputRight(resultInner.remainder());
			return new ParserResult<>(resultInner.result(), inputRight);
		}

		private @NonNull ParserInput<Input> getInputRight(
		  final ParserInput<Input> inputInner
		) {
			try {
				final var resultRight = getParserRight().parse(inputInner);
				return resultRight.remainder();
			} catch (final ParserError _) {}
			return inputInner;
		}

		private @NonNull ParserInput<Input> getInputLeft(
		  @NonNull ParserInput<Input> input
		) {
			try {
				final var resultLeft = getParserLeft().parse(input);
				input                = resultLeft.remainder();
			} catch (final ParserError _) {}
			return input;
		}
	}

	public static final class Mandatory<
	  Output,
	  DiscartedLeft,
	  DiscartedRight,
	  Input>
	  extends SurroundParser<Output, DiscartedLeft, DiscartedRight, Input> {

		public Mandatory(
		  final Parser<Output, Input> parserSource,
		  final Parser<DiscartedLeft, Input> parserLeft,
		  final Parser<DiscartedRight, Input> parserRight,
		  final Logger                        logger
		) {
			super(parserSource, parserLeft, parserRight, logger);
		}

		@Override
		public ParserResult<Output, Input> parse(
		  @NonNull final ParserInput<Input> parserInput
		) throws ParserError {
			final var inputLeft =
			  getParserLeft().parse(parserInput).remainder();
			final var resultInner = getParserSource().parse(inputLeft);
			final var inputRight =
			  getParserRight().parse(resultInner.remainder()).remainder();
			return new ParserResult<>(resultInner.result(), inputRight);
		}
	}

	private final Parser<Output, Input> parserSource;

	public SurroundParser(
	  final Parser<Output, Input> parserSource,
	  final Parser<DiscartedLeft, Input> parserLeft,
	  final Parser<DiscartedRight, Input> parserRight,
	  final Logger                        logger
	) {
		super(parserLeft, parserRight, logger);
		this.parserSource = parserSource;
	}

	protected Parser<Output, Input> getParserSource() { return parserSource; }
}
