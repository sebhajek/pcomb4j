package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * A {@link Parser} that applies a source parser to the input and, if that
 * succeeds, validates the remainder against a second "ahead" parser.
 *
 * <p>If the ahead parser fails, a {@link ParseAheadFailed} error is thrown. If
 * it succeeds, the ahead parser's result is discarded: only the source parser's
 * result and remainder are returned. The ahead parser does not consume any input.
 *
 * @param <Output> the output type of this parser (same as the source parser's output)
 * @param <OutputAhead> the output type of the ahead parser
 * @param <Input> the type of element consumed from the input
 */
public class LookAheadParser<Output, OutputAhead, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	/**
	 * Error thrown when the ahead parser fails, indicating that the look-ahead
	 * condition was violated.
	 */
	static final class ParseAheadFailed extends ParserError.Wrapped {
		/**
		 * Creates a new {@code ParseAheadFailed} error.
		 *
		 * @param message a description of the look-ahead failure; never {@code null}
		 * @param errorInner the inner error from the failed ahead parser; never {@code null}
		 */
		public ParseAheadFailed(
		  final String      message,
		  final ParserError errorInner
		) {
			super(message, errorInner);
		}
	}

	private final Parser<OutputAhead, Input> parserAhead;

	/**
	 * Creates a new {@code LookAheadParser}.
	 *
	 * @param parserSource the source parser providing the result; never {@code null}
	 * @param parserAhead the ahead parser that validates the remainder; never
	 *          {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public LookAheadParser(
	  final Parser<Output, Input> parserSource,
	  final Parser<OutputAhead, Input> parserAhead,
	  final Logger                     logger
	) {
		super(parserSource, logger);
		this.parserAhead = parserAhead;
	}

	/**
	 * Parses the input by first applying the source parser, then validating the
	 * remainder with the ahead parser. The ahead parser's result is discarded.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return the result of the source parser if both parsing steps succeed
	 * @throws ParserError if the input is empty, the source parser fails, or
	 *          the ahead parser fails
	 */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `lookAhead` parser");
		final var result = getParserSource().parse(parserInput);
		logger.trace("`lookAhead` source parser succeeded: {}", result.result());
		try {
			final var remainder = result.remainder();
			final var resultAhead = getParserAhead().parse(remainder);
			logger.trace(
			  "`lookAhead` ahead parser succeeded: {}", resultAhead.result());
		} catch (final ParserError error) {
			logger.trace("`lookAhead` ahead parser failed", error);
			throw new ParseAheadFailed("look ahead failed", error);
		}
		logger.debug("`lookAhead` parser returning result");
		return result;
	}

	/**
	 * Returns the ahead parser.
	 *
	 * @return the ahead parser; never {@code null}
	 */
	protected Parser<OutputAhead, Input> getParserAhead() {
		return parserAhead;
	}
}
