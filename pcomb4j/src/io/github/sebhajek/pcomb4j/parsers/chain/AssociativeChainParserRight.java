package io.github.sebhajek.pcomb4j.parsers.chain;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link AssociativeChainParser} that parses a right-associative chain of
 * binary operators.
 *
 * <p>All terms and operators are collected into lists, then folded from the
 * right via the {@link AssociativeChainParserCombiner}, producing a right fold:
 * {@code a op (b op c)}.
 *
 * @param <Output> the type of the folded result
 * @param <OutputInfix> the type produced by the operator parser
 * @param <Input> the type of element consumed from the input
 */
public final class AssociativeChainParserRight<Output, OutputInfix, Input>
  extends AssociativeChainParser<Output, OutputInfix, Input> {

	/**
	 * Creates a new {@code AssociativeChainParserRight}.
	 *
	 * @param parserSource the term parser; never {@code null}
	 * @param parserInfix the operator parser; never {@code null}
	 * @param combiner the combining function; never {@code null}
	 * @param logger the logger for debug output; never {@code null}
	 */
	public AssociativeChainParserRight(
	  final Parser<Output, Input> parserSource,
	  final Parser<OutputInfix, Input> parserInfix,
	  final AssociativeChainParserCombiner<Output, OutputInfix> combiner,
	  final Logger                                              logger
	) {
		super(parserSource, parserInfix, combiner, logger);
	}

	/**
	 * Parses a right-associative chain from the given input.
	 *
	 * <p>Parses all {@code term (operator term)*} into lists, then folds from
	 * the rightmost term backward. Stops when the operator parser fails.
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
		logger.debug("processing `chainRight` parser");

		final var result  = getParserSource().parse(parserInput);
		final var terms   = new ArrayList<Output>();
		final var infixes = new ArrayList<OutputInfix>();

		final var currentInput = processInput(result, terms, infixes);

		logger.trace(
		  "right chain accumulated {} terms, {} operators",
		  terms.size(),
		  infixes.size()
		);

		final var folded = getFolded(terms, infixes);

		return new ParserResult<>(folded, currentInput);
	}

	private Output
	getFolded(final List<Output> terms, final List<OutputInfix> infixes) {
		final var logger = getLogger();
		Output    folded = terms.removeLast();
		logger.trace("right chain initial folded value: {}", folded);
		for (int i = infixes.size() - 1; i >= 0; i--) {
			folded =
			  getCombiner().combine(terms.get(i), infixes.get(i), folded);
			logger.trace(
			  "right chain combine: {} {} {} -> {}",
			  terms.get(i),
			  infixes.get(i),
			  folded
			);
		}
		return folded;
	}

	private final @NonNull ParserInput<Input> processInput(
	  final ParserResult<Output, Input> result,
	  final List<Output> terms,
	  final List<OutputInfix> infixes
	) throws ParserError {
		final var logger = getLogger();
		terms.add(result.result());
		logger.trace("right chain initial term: {}", result.result());
		var currentInput = result.remainder();

		while (true) {
			ParserResult<OutputInfix, Input> infixResult;
			try {
				infixResult = getParserInfix().parse(currentInput);
			} catch (final ParserError e) { break; }
			infixes.add(infixResult.result());
			logger.trace("right chain operator: {}", infixResult.result());

			currentInput = getRemainder(terms, infixResult);
		}
		return currentInput;
	}

	private final @NonNull ParserInput<Input> getRemainder(
	  final List<Output> terms,
	  final ParserResult<OutputInfix, Input> infixResult
	) throws ParserError {
		final var logger     = getLogger();
		final var termResult = getParserSource().parse(infixResult.remainder());
		terms.add(termResult.result());
		logger.trace("right chain operand: {}", termResult.result());
		return termResult.remainder();
	}
}