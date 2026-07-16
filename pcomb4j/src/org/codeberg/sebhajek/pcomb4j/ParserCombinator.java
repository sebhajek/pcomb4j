package org.codeberg.sebhajek.pcomb4j;

import org.codeberg.sebhajek.pcomb4j.interfaces.CombinatorParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

/**
 * A {@link Parser} decorator that wires together all of the combinator
 * interfaces provided by pcomb4j and adds debug logging around each parse
 * attempt.
 *
 * <p>Instances are created either directly via {@link #ParserCombinator(Logger,
 * Parser)} or, more conveniently, through the fluent {@link Builder} obtained
 * from {@link #withLogger(Logger)}:
 *
 * <pre>{@code
 * ParserCombinator<Character, Character> parser =
 *     ParserCombinator.withLogger(logger).from(someParser);
 * }</pre>
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public class ParserCombinator<Output, Input>
  implements CombinatorParser<Output, Input> {

	/**
	 * Fluent builder used to wrap a {@link Parser} in a {@link
	 * ParserCombinator} bound to a particular {@link Logger}.
	 */
	public static final class Builder {
		private final Logger logger;

		private Builder(@NonNull final Logger logger) { this.logger = logger; }

		/**
		 * Wraps the given parser in a new {@link ParserCombinator} that logs to
		 * the {@link Logger} associated with this builder.
		 *
		 * @param parser the parser to wrap
		 * @param <Output> the type of value produced by a successful parse
		 * @param <Input> the type of element consumed from the input
		 * @return a new {@link ParserCombinator} wrapping {@code parser}
		 */
		public final <Output, Input> ParserCombinator<Output, Input> from(
		  @NonNull final Parser<Output, Input> parser
		) {
			return new ParserCombinator<>(logger, parser);
		}
	}

	/**
	 * Creates a {@link Builder} that produces {@link ParserCombinator}
	 * instances logging to the given
	 * {@link Logger}.
	 *
	 * @param logger the logger to use for debug output during parsing
	 * @return a new {@link Builder}
	 */
	public static final ParserCombinator.Builder withLogger(
	  @NonNull final Logger logger
	) {
		return new ParserCombinator.Builder(logger);
	}

	protected final @NonNull Logger logger;

	protected final @NonNull Parser<Output, Input> parser;

	/**
	 * Creates a new {@link ParserCombinator} wrapping the given parser.
	 *
	 * @param logger the logger used to record debug information during parsing
	 * @param parser the underlying parser to delegate to
	 */
	public ParserCombinator(
	  @NonNull final Logger logger,
	  @NonNull final Parser<Output, Input> parser
	) {
		this.logger = logger;
		this.parser = parser;
	}

	/** {@inheritDoc} */
	@Override
	public Parser<Output, Input> getParser() {
		return parser;
	}

	/**
	 * Delegates parsing to the wrapped {@link Parser}, logging the current
	 * input element at debug level before doing so.
	 *
	 * @param parserInput the input to parse
	 * @return the result produced by the wrapped parser
	 * @throws ParserError if the wrapped parser fails to parse the input
	 */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().debug("parsing {}", parserInput.getCurrent());
		return getParser().parse(parserInput);
	}

	/** {@inheritDoc} */
	@Override
	public Logger getLogger() {
		return logger;
	}
}
