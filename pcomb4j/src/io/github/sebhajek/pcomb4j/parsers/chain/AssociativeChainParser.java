package io.github.sebhajek.pcomb4j.parsers.chain;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base for parsers that handle left- or right-associative chains of
 * binary operators.
 *
 * <p>Parses a <em>term</em> followed by zero or more {@code (operator term)}
 * pairs, combining them via a user-supplied {@link Combiner}. Two concrete
 * subclasses define the associativity:
 *
 * <ul>
 *   <li>{@link Left} — left-associative: combines terms as they are parsed
 * (left fold). <li>{@link Right} — right-associative: collects all terms and
 * operators, then folds from the right.
 * </ul>
 *
 * @param <Output> the type of the final folded result
 * @param <OutputTerm> the type produced by the term parser (a subtype of {@code
 *   Output})
 * @param <OutputInfix> the type produced by the operator parser
 * @param <Input> the type of element consumed from the input
 */
public abstract sealed class AssociativeChainParser<
  Output, OutputTerm extends Output, OutputInfix, Input>
  extends AbstractSourcedParser<Output, OutputTerm, Input> {

	/**
	 * A function that combines a left operand, an operator, and a right operand
	 * into a new result.
	 *
	 * @param <Output> the type of the operands and the result
	 * @param <OutputInfix> the type of the operator value
	 */
	@FunctionalInterface
	public static interface Combiner<Output, OutputInfix> {

		/**
		 * Combines two operands with an operator into a single result.
		 *
		 * @param left the left operand (the accumulated fold so far)
		 * @param infix the operator value produced by the infix parser
		 * @param right the right operand (the next term or right fold)
		 * @return the combined result
		 */
		Output combine(Output left, OutputInfix infix, Output right);
	}

	/**
	 * A {@link AssociativeChainParser} that parses a left-associative chain of
	 * binary operators.
	 *
	 * <p>The first term is parsed, then each {@code (operator term)} pair is
	 * combined immediately via the {@link Combiner}, producing a left fold:
	 * {@code ((a op b) op c)}.
	 *
	 * @param <Output> the type of the folded result
	 * @param <OutputTerm> the type produced by the term parser
	 * @param <OutputInfix> the type produced by the operator parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class Left<Output, OutputTerm
	                                 extends Output, OutputInfix, Input>
	  extends AssociativeChainParser<Output, OutputTerm, OutputInfix, Input> {

		/**
		 * Creates a new {@code Left} chain parser.
		 *
		 * @param parserSource the term parser; never {@code null}
		 * @param parserInfix the operator parser; never {@code null}
		 * @param combiner the combining function; never {@code null}
		 * @param logger the logger for debug output; never {@code null}
		 */
		public Left(
		  final Parser<OutputTerm, Input> parserSource,
		  final Parser<OutputInfix, Input> parserInfix,
		  final Combiner<Output, OutputInfix> combiner,
		  final Logger                        logger
		) {
			super(parserSource, parserInfix, combiner, logger);
		}

		/**
		 * Parses a left-associative chain from the given input.
		 *
		 * <p>Parses the first term, then repeatedly tries {@code (operator,
		 * term)} pairs. Each pair is immediately combined with the current fold
		 * result. Stops when the operator parser fails.
		 *
		 * @param parserInput the input to parse; never {@code null}
		 * @return the folded result and the remaining input
		 * @throws ParserError if the first term cannot be parsed, or if an
		 *   operator is found but the
		 *     following term is missing
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

	/**
	 * A {@link AssociativeChainParser} that parses a right-associative chain of
	 * binary operators.
	 *
	 * <p>All terms and operators are collected into lists, then folded from the
	 * right via the {@link Combiner}, producing a right fold: {@code a op (b op
	 * c)}.
	 *
	 * @param <Output> the type of the folded result
	 * @param <OutputTerm> the type produced by the term parser
	 * @param <OutputInfix> the type produced by the operator parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class Right<Output, OutputTerm
	                                  extends Output, OutputInfix, Input>
	  extends AssociativeChainParser<Output, OutputTerm, OutputInfix, Input> {

		/**
		 * Creates a new {@code Right} chain parser.
		 *
		 * @param parserSource the term parser; never {@code null}
		 * @param parserInfix the operator parser; never {@code null}
		 * @param combiner the combining function; never {@code null}
		 * @param logger the logger for debug output; never {@code null}
		 */
		public Right(
		  final Parser<OutputTerm, Input> parserSource,
		  final Parser<OutputInfix, Input> parserInfix,
		  final Combiner<Output, OutputInfix> combiner,
		  final Logger                        logger
		) {
			super(parserSource, parserInfix, combiner, logger);
		}

		/**
		 * Parses a right-associative chain from the given input.
		 *
		 * <p>Parses all {@code term (operator term)*} into lists, then folds
		 * from the rightmost term backward. Stops when the operator parser
		 * fails.
		 *
		 * @param parserInput the input to parse; never {@code null}
		 * @return the folded result and the remaining input
		 * @throws ParserError if the first term cannot be parsed, or if an
		 *   operator is found but the
		 *     following term is missing
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
		  final ParserResult<OutputTerm, Input> result,
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
			final var logger = getLogger();
			final var termResult =
			  getParserSource().parse(infixResult.remainder());
			terms.add(termResult.result());
			logger.trace("right chain operand: {}", termResult.result());
			return termResult.remainder();
		}
	}

	private final Parser<OutputInfix, Input> parserInfix;

	private final Combiner<Output, OutputInfix> combiner;

	/**
	 * Creates a new {@code AssociativeChainParser}.
	 *
	 * @param parserSource the term parser; never {@code null}
	 * @param parserInfix the operator parser; never {@code null}
	 * @param combiner the combining function; never {@code null}
	 * @param logger the logger for debug output; never {@code null}
	 */
	public AssociativeChainParser(
	  final Parser<OutputTerm, Input> parserSource,
	  final Parser<OutputInfix, Input> parserInfix,
	  final Combiner<Output, OutputInfix> combiner,
	  final Logger                        logger
	) {
		super(parserSource, logger);
		this.parserInfix = parserInfix;
		this.combiner    = combiner;
	}

	/**
	 * Returns the combiner used to fold terms and operators.
	 *
	 * @return the combiner; never {@code null}
	 */
	protected Combiner<Output, OutputInfix> getCombiner() { return combiner; }

	/**
	 * Returns the parser used to parse the infix operator.
	 *
	 * @return the infix parser; never {@code null}
	 */
	protected Parser<OutputInfix, Input> getParserInfix() {
		return parserInfix;
	}
}
