package io.github.sebhajek.pcomb4j.parsers.chain;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Parser} that applies a fixed, ordered list of parsers in sequence,
 * collecting all of their results into a {@link List}.
 *
 * <p>Unlike {@link CardinalParser}, the parsers in the chain may be different
 * instances, but they must all share the same output and input types. The list
 * is built in encounter order and returned as-is (mutable).
 *
 * @param <Output> the output type shared by all parsers in the chain
 * @param <Input> the type of element consumed from the input
 */
public class ChainParser<Output, Input>
  extends AbstractParser<List<Output>, Input> {

	final List<Parser<Output, Input>> parsersList;

	/**
	 * Creates a new {@code ChainParser}.
	 *
	 * @param parsersList an ordered list of parsers to apply; never {@code
	 *   null} and should not be
	 *     empty
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public ChainParser(List<Parser<Output, Input>> parsersList, Logger logger) {
		super(logger);
		this.parsersList = parsersList;
	}

	/**
	 * Applies each parser in {@link #parsersList} in order, collecting results.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} whose result is the list of all parsed
	 *   values in order
	 * @throws ParserError if any parser in the chain fails
	 */
	@Override
	public ParserResult<List<Output>, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		var       logger  = getLogger();
		final var results = new ArrayList<Output>(parsersList.size());
		logger.debug("applying `chain`");
		final var chainResult = applyChain(parserInput, results, parsersList);
		return new ParserResult<>(results, chainResult);
	}

	private static final <A, B> ParserInput<B> applyChain(
	  final ParserInput<B> input,
	  final List<A> results,
	  final List<Parser<A, B>> parsersList
	) throws ParserError {
		var currentInput = input;
		for (final var parser : parsersList) {
			final var result = parser.parse(currentInput);
			results.addLast(result.result());
			currentInput = result.remainder();
		}
		return currentInput;
	}
}
