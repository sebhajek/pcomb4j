package io.github.sebhajek.pcomb4j.parsers.sequence;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractPairParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * A {@link Parser} that applies a source parser surrounded by optional or
 * mandatory left/right delimiters, discarding the delimiter results.
 *
 * <p>Two modes are available:
 *
 * <ul>
 *   <li>{@link Optional} -delimiters are optional; if they fail the parse
 * continues without them. <li>{@link Mandatory} -both delimiters are required;
 * if either fails the entire parse fails.
 * </ul>
 *
 * @param <Output> the type of value produced by the source parser
 * @param <DiscartedLeft> the type of value produced by the left delimiter
 * @param <DiscartedRight> the type of value produced by the right delimiter
 * @param <Input> the type of element consumed from the input
 */
public abstract
  sealed class SurroundParser<Output, DiscartedLeft, DiscartedRight, Input>
  extends AbstractPairParser<Output, DiscartedLeft, DiscartedRight, Input> {

	/**
	 * A {@link SurroundParser} that treats both delimiters as optional.
	 *
	 * <p>If either the left or right delimiter fails to parse, the
	 * corresponding side is silently skipped and parsing continues.
	 *
	 * @param <Output> the type of value produced by the source parser
	 * @param <DiscartedLeft> the type of value produced by the left delimiter
	 * @param <DiscartedRight> the type of value produced by the right delimiter
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class Optional<
	  Output,
	  DiscartedLeft,
	  DiscartedRight,
	  Input>
	  extends SurroundParser<Output, DiscartedLeft, DiscartedRight, Input> {

		/**
		 * Creates a new {@code Optional} surround parser.
		 *
		 * @param parserSource the source parser producing the main result;
		 *   never {@code null}
		 * @param parserLeft the optional left delimiter; never {@code null}
		 * @param parserRight the optional right delimiter; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
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
			final var logger = getLogger();
			logger.debug("processing `surroundOptional` parser");
			final var inputLeft   = getInputLeft(parserInput);
			final var resultInner = getParserSource().parse(inputLeft);
			final var inputRight  = getInputRight(resultInner.remainder());
			logger.trace(
			  "`surroundOptional` parser succeeded: {}", resultInner.result()
			);
			return new ParserResult<>(resultInner.result(), inputRight);
		}

		private @NonNull ParserInput<Input> getInputRight(
		  final ParserInput<Input> inputInner
		) {
			final var logger = getLogger();
			try {
				final var resultRight = getParserRight().parse(inputInner);
				logger.trace(
				  "`surroundOptional` right parser succeeded: {}",
				  resultRight.result()
				);
				return resultRight.remainder();
			} catch (final ParserError _) {
				logger.trace(
				  "`surroundOptional` right parser failed (optional, skipping)"
				);
			}
			return inputInner;
		}

		private @NonNull ParserInput<Input> getInputLeft(
		  @NonNull ParserInput<Input> input
		) {
			final var logger = getLogger();
			try {
				final var resultLeft = getParserLeft().parse(input);
				logger.trace(
				  "`surroundOptional` left parser succeeded: {}",
				  resultLeft.result()
				);
				input = resultLeft.remainder();
			} catch (final ParserError _) {
				logger.trace(
				  "`surroundOptional` left parser failed (optional, skipping)"
				);
			}
			return input;
		}
	}

	/**
	 * A {@link SurroundParser} that requires both delimiters to succeed.
	 *
	 * <p>If either the left or right delimiter fails, the error is propagated
	 * and the entire parse fails.
	 *
	 * @param <Output> the type of value produced by the source parser
	 * @param <DiscartedLeft> the type of value produced by the left delimiter
	 * @param <DiscartedRight> the type of value produced by the right delimiter
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class Mandatory<
	  Output,
	  DiscartedLeft,
	  DiscartedRight,
	  Input>
	  extends SurroundParser<Output, DiscartedLeft, DiscartedRight, Input> {

		/**
		 * Creates a new {@code Mandatory} surround parser.
		 *
		 * @param parserSource the source parser producing the main result;
		 *   never {@code null}
		 * @param parserLeft the required left delimiter; never {@code null}
		 * @param parserRight the required right delimiter; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
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
			final var logger = getLogger();
			logger.debug("processing `surround` parser");
			final var inputLeft =
			  getParserLeft().parse(parserInput).remainder();
			logger.trace("`surround` left parser succeeded");
			final var resultInner = getParserSource().parse(inputLeft);
			logger.trace(
			  "`surround` source parser succeeded: {}", resultInner.result()
			);
			final var inputRight =
			  getParserRight().parse(resultInner.remainder()).remainder();
			logger.trace("`surround` right parser succeeded");
			return new ParserResult<>(resultInner.result(), inputRight);
		}
	}

	private final Parser<Output, Input> parserSource;

	/**
	 * Creates a new {@code SurroundParser}.
	 *
	 * @param parserSource the source parser producing the main result; never
	 *   {@code null}
	 * @param parserLeft the left delimiter parser; never {@code null}
	 * @param parserRight the right delimiter parser; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public SurroundParser(
	  final Parser<Output, Input> parserSource,
	  final Parser<DiscartedLeft, Input> parserLeft,
	  final Parser<DiscartedRight, Input> parserRight,
	  final Logger                        logger
	) {
		super(parserLeft, parserRight, logger);
		this.parserSource = parserSource;
	}

	/**
	 * Returns the source parser.
	 *
	 * @return the source parser; never {@code null}
	 */
	protected Parser<Output, Input> getParserSource() { return parserSource; }
}
