package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Parser} that applies an inner parser exactly {@code count} times,
 * collecting all results into an unmodifiable {@link List}.
 *
 * <p>If the inner parser fails before reaching {@code count}, the error is
 * propagated immediately.
 *
 * @param <Output> the type of value produced by each individual parse
 * @param <Input> the type of element consumed from the input
 */
public class ExactCountParser<Output, Input>
  extends AbstractSourcedParser<List<Output>, Output, Input> {

	private final int count;

	/**
	 * Creates a new {@code ExactCountParser}.
	 *
	 * @param parserSource the inner parser to repeat; never {@code null}
	 * @param count the exact number of times to apply the parser; must be
	 *   non-negative
	 * @param logger the logger used for debug output; never {@code null}
	 * @throws IllegalArgumentException if {@code count} is negative
	 */
	public ExactCountParser(
	  final Parser<Output, Input> parserSource,
	  final int                   count,
	  final Logger                logger
	) {
		super(parserSource, logger);
		if (count < 0) {
			throw new IllegalArgumentException(
			  "count must be non-negative, got: " + count
			);
		}
		this.count = count;
	}

	/**
	 * Applies the inner parser exactly {@code count} times, collecting each
	 * result into an unmodifiable list.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} with a list of exactly {@code count}
	 *   elements
	 * @throws ParserError if the inner parser fails before reaching {@code
	 *   count}
	 */
	@Override
	public ParserResult<List<Output>, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger       = getLogger();
		final var results      = new ArrayList<Output>(count);
		var       currentInput = parserInput;
		logger.debug("processing `exactCount({})` parser", count);

		for (int i = 0; i < count; i++) {
			final var result = getParserSource().parse(currentInput);
			results.addLast(result.result());
			currentInput = result.remainder();
		}

		logger.trace(
		  "`exactCount({})` parser succeeded: matched {} elements",
		  count,
		  results.size()
		);
		return new ParserResult<>(
		  Collections.unmodifiableList(results), currentInput
		);
	}
}
