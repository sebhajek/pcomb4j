package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.function.Predicate;

/**
 * A {@link Parser} that delegates to an inner parser and then checks the result
 * against a predicate, failing with {@link NotSatisfied} when the predicate is
 * not met.
 *
 * @param <Output> the type of value produced by both the inner and this parser
 * @param <Input> the type of element consumed from the input
 */
public class FilterParser<Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	/**
	 * Thrown when the inner parser succeeds but its result does not satisfy
	 * the predicate.
	 */
	public static class NotSatisfied extends ParserError.Leaf {

		/** Creates a new {@code NotSatisfied} error. */
		public NotSatisfied() { super("Not satisfied"); }
	}

	public static class LookAhead<Output, Input>
	  extends AbstractSourcedParser<Output, Output, Input> {
		private final Predicate<Input> predicate;

		public LookAhead(
		  final Parser<Output, Input> parserSource,
		  final Predicate<Input> predicate,
		  final Logger           logger
		) {

			super(parserSource, logger);
			this.predicate = predicate;
		}

		@Override
		public ParserResult<Output, Input> parse(
		  @NonNull final ParserInput<Input> parserInput
		) throws ParserError {
			final var logger = getLogger();
			logger.debug("processing `filterLookAhead` parser");
			final var parserResult = getParserSource().parse(parserInput);
			if (getPredicate().test(parserResult.remainder().getCurrent())) {
				logger.trace(
				  "`filterLookAhead` parser succeeded: {}",
				  parserResult.remainder()
				);
				return parserResult;
			} else {
				logger.trace(
				  "`filterLookAhead` parser failed: {}",
				  parserResult.remainder().getCurrent()
				);
				throw new NotSatisfied();
			}
		}

		private Predicate<Input> getPredicate() { return predicate; }
	}

	private final Predicate<Output> predicate;

	/**
	 * Creates a new {@code FilterParser}.
	 *
	 * @param parserSource the inner parser whose result is tested; never {@code
	 *   null}
	 * @param predicate the condition the parsed value must satisfy; never
	 *   {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public FilterParser(
	  final Parser<Output, Input> parserSource,
	  final Predicate<Output> predicate,
	  final Logger            logger
	) {
		super(parserSource, logger);
		this.predicate = predicate;
	}

	/**
	 * Delegates to the inner parser and tests the result with the predicate.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return the inner parser's result unchanged if the predicate holds
	 * @throws ParserError if the inner parser fails or the predicate is not
	 *   satisfied
	 */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `filter` parser");
		final var parserResult = getParserSource().parse(parserInput);
		if (getPredicate().test(parserResult.result())) {
			logger.trace(
			  "`filter` parser succeeded: {}", parserResult.result()
			);
			return parserResult;
		} else {
			logger.trace(
			  "`filter` parser failed: {}", parserInput.getCurrent()
			);
			throw new NotSatisfied();
		}
	}

	/**
	 * Returns the predicate used to test parse results.
	 *
	 * @return the predicate; never {@code null}
	 */
	protected Predicate<Output> getPredicate() { return predicate; }
}
