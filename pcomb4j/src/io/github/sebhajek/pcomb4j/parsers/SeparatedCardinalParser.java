package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Parser} that applies an inner element parser repeatedly, with each
 * pair of elements separated by a separator parser, collecting all element
 * results into an unmodifiable {@link List}.
 *
 * <p>Four variants control whether the first element is mandatory and whether a
 * trailing separator is accepted:
 *
 * <ul>
 *   <li>{@link ZeroOrMore} — {@code element (separator element)*}
 *   <li>{@link OneOrMore} — {@code element (separator element)*}
 *   <li>{@link ZeroOrMoreTrailing} — {@code element (separator element)*
 * separator?} <li>{@link OneOrMoreTrailing} — {@code element (separator
 * element)* separator?}
 * </ul>
 *
 * @param <Output> the type of value produced by the element parser
 * @param <OutputSeparator> the type of value produced by the separator parser
 * @param <Input> the type of element consumed from the input
 */
public abstract
          sealed class SeparatedCardinalParser<Output, OutputSeparator, Input>
  extends AbstractParser<List<Output>, Input> {

	/**
	 * A {@link SeparatedCardinalParser} where the first element is optional;
	 * trailing separators are not accepted.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <OutputSeparator> the type of value produced by the separator
	 *   parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class ZeroOrMore<Output, OutputSeparator, Input>
	  extends SeparatedCardinalParser<Output, OutputSeparator, Input> {

		/**
		 * Creates a new {@code ZeroOrMore} separated parser.
		 *
		 * @param elementParser the parser for each element; never {@code null}
		 * @param separatorParser the parser for separators; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public ZeroOrMore(
		  final Parser<Output, Input> elementParser,
		  final Parser<OutputSeparator, Input> separatorParser,
		  final Logger                         logger
		) {
			super(elementParser, separatorParser, logger);
		}

		@Override
		protected boolean isFirstMandatory() {
			return false;
		}

		@Override
		protected boolean isTrailingAccepted() {
			return false;
		}

		@Override
		protected String getParserName() {
			return "zeroOrMoreSeparated";
		}
	}

	/**
	 * A {@link SeparatedCardinalParser} where the first element is mandatory;
	 * trailing separators are not accepted.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <OutputSeparator> the type of value produced by the separator
	 *   parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class OneOrMore<Output, OutputSeparator, Input>
	  extends SeparatedCardinalParser<Output, OutputSeparator, Input> {

		/**
		 * Creates a new {@code OneOrMore} separated parser.
		 *
		 * @param elementParser the parser for each element; never {@code null}
		 * @param separatorParser the parser for separators; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public OneOrMore(
		  final Parser<Output, Input> elementParser,
		  final Parser<OutputSeparator, Input> separatorParser,
		  final Logger                         logger
		) {
			super(elementParser, separatorParser, logger);
		}

		@Override
		protected boolean isFirstMandatory() {
			return true;
		}

		@Override
		protected boolean isTrailingAccepted() {
			return false;
		}

		@Override
		protected String getParserName() {
			return "oneOrMoreSeparated";
		}
	}

	/**
	 * A {@link SeparatedCardinalParser} where the first element is optional;
	 * trailing separators are accepted.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <OutputSeparator> the type of value produced by the separator
	 *   parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class ZeroOrMoreTrailing<Output, OutputSeparator, Input>
	  extends SeparatedCardinalParser<Output, OutputSeparator, Input> {

		/**
		 * Creates a new {@code ZeroOrMoreTrailing} separated parser.
		 *
		 * @param elementParser the parser for each element; never {@code null}
		 * @param separatorParser the parser for separators; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public ZeroOrMoreTrailing(
		  final Parser<Output, Input> elementParser,
		  final Parser<OutputSeparator, Input> separatorParser,
		  final Logger                         logger
		) {
			super(elementParser, separatorParser, logger);
		}

		@Override
		protected boolean isFirstMandatory() {
			return false;
		}

		@Override
		protected boolean isTrailingAccepted() {
			return true;
		}

		@Override
		protected String getParserName() {
			return "zeroOrMoreSeparatedTrailing";
		}
	}

	/**
	 * A {@link SeparatedCardinalParser} where the first element is mandatory;
	 * trailing separators are accepted.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <OutputSeparator> the type of value produced by the separator
	 *   parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class OneOrMoreTrailing<Output, OutputSeparator, Input>
	  extends SeparatedCardinalParser<Output, OutputSeparator, Input> {

		/**
		 * Creates a new {@code OneOrMoreTrailing} separated parser.
		 *
		 * @param elementParser the parser for each element; never {@code null}
		 * @param separatorParser the parser for separators; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public OneOrMoreTrailing(
		  final Parser<Output, Input> elementParser,
		  final Parser<OutputSeparator, Input> separatorParser,
		  final Logger                         logger
		) {
			super(elementParser, separatorParser, logger);
		}

		@Override
		protected boolean isFirstMandatory() {
			return true;
		}

		@Override
		protected boolean isTrailingAccepted() {
			return true;
		}

		@Override
		protected String getParserName() {
			return "oneOrMoreSeparatedTrailing";
		}
	}

	private final Parser<Output, Input> elementParser;

	private final Parser<OutputSeparator, Input> separatorParser;

	/**
	 * Creates a new {@code SeparatedCardinalParser}.
	 *
	 * @param elementParser the parser for each element; never {@code null}
	 * @param separatorParser the parser for separators; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public SeparatedCardinalParser(
	  final Parser<Output, Input> elementParser,
	  final Parser<OutputSeparator, Input> separatorParser,
	  final Logger                         logger
	) {
		super(logger);
		this.elementParser   = elementParser;
		this.separatorParser = separatorParser;
	}

	/**
	 * Returns whether the first element is mandatory.
	 *
	 * @return {@code true} if at least one element is required
	 */
	protected abstract boolean isFirstMandatory();

	/**
	 * Returns whether a trailing separator is accepted at the end of the
	 * sequence.
	 *
	 * @return {@code true} if a trailing separator is allowed
	 */
	protected abstract boolean isTrailingAccepted();

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
	 * Returns the separator parser.
	 *
	 * @return the separator parser; never {@code null}
	 */
	protected Parser<OutputSeparator, Input> getSeparatorParser() {
		return separatorParser;
	}

	/**
	 * Implements the separated parsing loop using the abstract policy methods.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} with the collected elements
	 * @throws ParserError if the first element is mandatory and cannot be
	 *   parsed, or if a separator
	 *     is found without a following element
	 */
	@Override
	public ParserResult<List<Output>, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger  = getLogger();
		final var results = new ArrayList<Output>();
		logger.debug("processing `{}` parser", getParserName());

		var currentInput = parseFirstElement(logger, results, parserInput);

		if (results.isEmpty()) {
			logger.trace("`{}` parser: no elements matched", getParserName());
			return new ParserResult<>(
			  Collections.unmodifiableList(results), parserInput
			);
		}

		currentInput = parseLoop(logger, results, currentInput);

		logger.trace(
		  "`{}` parser succeeded: matched {} elements",
		  getParserName(),
		  results.size()
		);
		return new ParserResult<>(
		  Collections.unmodifiableList(results), currentInput
		);
	}

	private @NonNull ParserInput<Input> parseFirstElement(
	  final Logger logger,
	  final List<Output> results,
	  final ParserInput<Input> parserInput
	) throws ParserError {
		try {
			final var firstResult = getElementParser().parse(parserInput);
			results.addLast(firstResult.result());
			return firstResult.remainder();
		} catch (final ParserError err) {
			if (isFirstMandatory()) { throw err; }
			logger.trace("first element failed (zero-or-more, skipping)");
			return parserInput;
		}
	}

	private @NonNull ParserInput<Input> parseLoop(
	  final Logger logger,
	  final List<Output> results,
	  final ParserInput<Input> currentInput
	) throws ParserError {
		var input = currentInput;
		while (!input.isEmpty()) {
			final ParserInput<Input> afterSep;
			try {
				final var sepResult = getSeparatorParser().parse(input);
				afterSep            = sepResult.remainder();
				logger.trace("separator matched, continuing");
			} catch (final ParserError err) {
				logger.trace("end of separated sequence (no separator)");
				break;
			}

			try {
				final var elemResult = getElementParser().parse(afterSep);
				results.addLast(elemResult.result());
				input = elemResult.remainder();
				logger.trace("element after separator matched");
			} catch (final ParserError err) {
				logger.trace("element after separator failed");
				if (isTrailingAccepted()) {
					logger.trace("accepted trailing separator");
					input = afterSep;
				} else {
					logger.trace("separator without element propagated");
					throw err;
				}
				break;
			}
		}
		return input;
	}
}
