package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * A {@link Parser} that applies an inner parser zero or more (or one or more)
 * times, discarding all results.
 *
 * <p>Two variants are available:
 *
 * <ul>
 *   <li>{@link ZeroOrMore} — applies the inner parser zero or more times
 *   <li>{@link OneOrMore} — applies the inner parser one or more times
 * </ul>
 *
 * @param <Output> the type of value produced by each individual parse
 *   (discarded)
 * @param <Input> the type of element consumed from the input
 */
public abstract sealed class SkipCardinalParser<Output, Input>
  extends       AbstractParser<Void, Input> {

	/**
	 * A {@link SkipCardinalParser} that applies the inner parser zero or more
	 * times.
	 *
	 * @param <Output> the type of value produced by each individual parse
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class ZeroOrMore<Output, Input>
	  extends SkipCardinalParser<Output, Input> {

		/**
		 * Creates a new {@code ZeroOrMore} skip parser.
		 *
		 * @param elementParser the inner parser to repeat; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public ZeroOrMore(
		  final Parser<Output, Input> elementParser,
		  final Logger                logger
		) {
			super(elementParser, logger);
		}

		@Override
		protected boolean isOneOrMore() {
			return false;
		}

		@Override
		protected String getParserName() {
			return "zeroOrMoreSkip";
		}
	}

	/**
	 * A {@link SkipCardinalParser} that applies the inner parser one or more
	 * times.
	 *
	 * @param <Output> the type of value produced by each individual parse
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class OneOrMore<Output, Input>
	  extends SkipCardinalParser<Output, Input> {

		/**
		 * Creates a new {@code OneOrMore} skip parser.
		 *
		 * @param elementParser the inner parser to repeat; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public OneOrMore(
		  final Parser<Output, Input> elementParser,
		  final Logger                logger
		) {
			super(elementParser, logger);
		}

		@Override
		protected boolean isOneOrMore() {
			return true;
		}

		@Override
		protected String getParserName() {
			return "oneOrMoreSkip";
		}
	}

	private final Parser<Output, Input> elementParser;

	/**
	 * Creates a new {@code SkipCardinalParser}.
	 *
	 * @param elementParser the inner parser to repeat; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public SkipCardinalParser(
	  final Parser<Output, Input> elementParser,
	  final Logger                logger
	) {
		super(logger);
		this.elementParser = elementParser;
	}

	/**
	 * Returns whether at least one match is required.
	 *
	 * @return {@code true} if at least one match is required
	 */
	protected abstract boolean isOneOrMore();

	/**
	 * Returns the human-readable name of this parser variant for logging.
	 *
	 * @return the parser name
	 */
	protected abstract String getParserName();

	/**
	 * Returns the element parser.
	 *
	 * @return the element parser; never {@code null}
	 */
	protected Parser<Output, Input> getElementParser() { return elementParser; }

	/**
	 * Applies the inner parser repeatedly according to the cardinality policy,
	 * discarding all results.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} with a {@code null} result and the
	 *   remaining input
	 * @throws ParserError if the cardinality is {@code OneOrMore} and the
	 *   first application fails
	 */
	@Override
	@SuppressWarnings("NullAway")
	public ParserResult<Void, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger       = getLogger();
		var       currentInput = parserInput;
		logger.debug("processing `{}` parser", getParserName());

		if (isOneOrMore()) {
			currentInput = getElementParser().parse(currentInput).remainder();
		}

		while (!currentInput.isEmpty()) {
			try {
				currentInput =
				  getElementParser().parse(currentInput).remainder();
			} catch (final ParserError err) {
				logger.trace("encountered end of skip series");
				break;
			}
		}

		logger.trace("`{}` parser succeeded", getParserName());
		return new ParserResult<>(null, currentInput);
	}
}
