package io.github.sebhajek.pcomb4j.parsers.chain;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * A {@link AssociativeChainParser} that parses a left-associative chain of
 * binary operators.
 *
 * <p>The first term is parsed, then each {@code (operator term)} pair is
 * combined immediately via the {@link AssociativeChainParserCombiner},
 * producing a left fold: {@code ((a op b) op c)}.
 *
 * @param <Output> the type of the folded result
 * @param <OutputInfix> the type produced by the operator parser
 * @param <Input> the type of element consumed from the input
 */
public final class AssociativeChainParserLeft<Output, OutputInfix, Input>
  extends AssociativeChainParser<Output, OutputInfix, Input> {

	/**
	 * Creates a new {@code AssociativeChainParserLeft}.
	 *
	 * @param parserSource the term parser; never {@code null}
	 * @param parserInfix the operator parser; never {@code null}
	 * @param combiner the combining function; never {@code null}
	 * @param logger the logger for debug output; never {@code null}
	 */
	public AssociativeChainParserLeft(
	  final Parser<Output, Input> parserSource,
	  final Parser<OutputInfix, Input> parserInfix,
	  final AssociativeChainParserCombiner<Output, OutputInfix> combiner,
	  final Logger                                              logger
	) {
		super(parserSource, parserInfix, combiner, logger);
	}

	/**
	 * Parses a left-associative chain from the given input.
	 *
	 * <p>Parses the first term, then repeatedly tries {@code (operator, term)}
	 * pairs. Each pair is immediately combined with the current fold result.
	 * Stops when the operator parser fails.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return the folded result and the remaining input
	 * @throws ParserError if the first term cannot be parsed, or if an
	 *   operator is found but the following term is missing
	 */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `chainLeft` parser");

		final var result       = getParserSource().parse(parserInput);
		Output    folded       = result.result();
		var       currentInput = result.remainder();
		logger.trace("left chain initial term: {}", folded);

		while (true) {
			ParserResult<OutputInfix, Input> infixResult;
			try {
				infixResult = getParserInfix().parse(currentInput);
			} catch (final ParserError e) {
				logger.trace("end of left chain: {}", folded);
				break;
			}

			logger.trace("left chain operator: {}", infixResult.result());

			final var termResult =
			  getParserSource().parse(infixResult.remainder());

			logger.trace(
			  "left chain operand: {}; combining {} {} {}",
			  termResult.result(),
			  folded,
			  infixResult.result(),
			  termResult.result()
			);

			folded = getCombiner().combine(
			  folded, infixResult.result(), termResult.result()
			);
			currentInput = termResult.remainder();
		}

		return new ParserResult<>(folded, currentInput);
	}
}