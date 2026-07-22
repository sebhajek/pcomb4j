package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class AssociativeChainParser<
  Output, OutputTerm extends Output, OutputInfix, Input>
  extends AbstractSourcedParser<Output, OutputTerm, Input> {

	@FunctionalInterface
	public static interface Combiner<Output, OutputInfix> {
		Output combine(Output left, OutputInfix infix, Output right);
	}

	public static final class Left<Output, OutputTerm
	                                 extends Output, OutputInfix, Input>
	  extends AssociativeChainParser<Output, OutputTerm, OutputInfix, Input> {

		public Left(
		  final Parser<OutputTerm, Input> parserSource,
		  final Parser<OutputInfix, Input> parserInfix,
		  final Combiner<Output, OutputInfix> combiner,
		  final Logger                        logger
		) {
			super(parserSource, parserInfix, combiner, logger);
		}

		@Override
		public ParserResult<Output, Input> parse(
		  @NonNull final ParserInput<Input> parserInput
		) throws ParserError {
			final var logger = getLogger();
			logger.debug("processing `chainLeft` parser");

			final var result       = getParserSource().parse(parserInput);
			Output    folded       = result.result();
			var       currentInput = result.remainder();

			while (true) {
				ParserResult<OutputInfix, Input> infixResult;
				try {
					infixResult = getParserInfix().parse(currentInput);
				} catch (final ParserError e) {
					logger.trace("end of left chain: {}", folded);
					break;
				}

				final var termResult =
				  getParserSource().parse(infixResult.remainder());
				folded = getCombiner().combine(
				  folded, infixResult.result(), termResult.result()
				);
				currentInput = termResult.remainder();
			}

			return new ParserResult<>(folded, currentInput);
		}
	}

	public static final class Right<Output, OutputTerm
	                                  extends Output, OutputInfix, Input>
	  extends AssociativeChainParser<Output, OutputTerm, OutputInfix, Input> {

		public Right(
		  final Parser<OutputTerm, Input> parserSource,
		  final Parser<OutputInfix, Input> parserInfix,
		  final Combiner<Output, OutputInfix> combiner,
		  final Logger                        logger
		) {
			super(parserSource, parserInfix, combiner, logger);
		}

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
			final var folded       = getFolded(terms, infixes);

			return new ParserResult<>(folded, currentInput);
		}

		private Output
		getFolded(final List<Output> terms, final List<OutputInfix> infixes) {
			Output folded = terms.removeLast();
			for (int i = infixes.size() - 1; i >= 0; i--) {
				folded =
				  getCombiner().combine(terms.get(i), infixes.get(i), folded);
			}
			return folded;
		}

		private final @NonNull ParserInput<Input> processInput(
		  final ParserResult<OutputTerm, Input> result,
		  final List<Output> terms,
		  final List<OutputInfix> infixes
		) throws ParserError {
			terms.add(result.result());
			var currentInput = result.remainder();

			while (true) {
				ParserResult<OutputInfix, Input> infixResult;
				try {
					infixResult = getParserInfix().parse(currentInput);
				} catch (final ParserError e) { break; }
				infixes.add(infixResult.result());

				currentInput = getRemainder(terms, infixResult);
			}
			return currentInput;
		}

		private final @NonNull ParserInput<Input> getRemainder(
		  final List<Output> terms,
		  final ParserResult<OutputInfix, Input> infixResult
		) throws ParserError {
			final var termResult =
			  getParserSource().parse(infixResult.remainder());
			terms.add(termResult.result());
			return termResult.remainder();
		}
	}

	private final Parser<OutputInfix, Input> parserInfix;

	private final Combiner<Output, OutputInfix> combiner;

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

	protected Combiner<Output, OutputInfix> getCombiner() { return combiner; }

	protected Parser<OutputInfix, Input> getParserInfix() {
		return parserInfix;
	}
}
