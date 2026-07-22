package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * A {@link Parser} that succeeds only when the input is exhausted, consuming
 * nothing and producing no meaningful value.
 *
 * <p>This is the parser equivalent of the "end of file" matcher: it checks
 * {@link ParserInput#isEmpty()} and succeeds if the input has no remaining
 * elements.
 *
 * @param <Input> the type of element consumed from the input
 */
public class EofParser<Input> extends AbstractParser<Void, Input> {

	/**
	 * Error thrown when the input is not empty, indicating that the EOF
	 * condition was not matched.
	 */
	public static final class EofNotMatched extends ParserError.Leaf {

		/** Creates a new {@code EofNotMatched} error. */
		public EofNotMatched() { super("Expected EOF."); }
	}

	/**
	 * Creates a new {@code EofParser}.
	 *
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public EofParser(Logger logger) { super(logger); }

	/**
	 * Parses the input, succeeding only if the input is empty.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} with a {@code null} result and unchanged
	 *         remainder when the input is empty
	 * @throws ParserError if the input is not empty
	 */
	@Override
	@SuppressWarnings("NullAway")
	public ParserResult<Void, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `eof` parser");
		if (parserInput.isEmpty()) {
			logger.trace("`eof` parser succeeded");
			return new ParserResult<>(null, parserInput);
		}
		logger.trace("`eof` parser failed: input still contains elements");
		throw new EofNotMatched();
	}
}
