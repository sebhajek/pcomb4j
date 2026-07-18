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
 * Abstract base for parsers that apply an inner parser repeatedly, collecting
 * all results into an unmodifiable {@link List}.
 *
 * <p>Two concrete subclasses define the cardinality policy:
 *
 * <ul>
 *   <li>{@link ZeroOrMore} — succeeds even when the inner parser never matches,
 * returning an empty list. <li>{@link OneOrMore} — requires at least one
 * successful match; the first parse is mandatory and any failure there
 * propagates.
 * </ul>
 *
 * @param <Output> the element type produced by each individual parse
 * @param <Input> the type of element consumed from the input
 */
public abstract class CardinalParser<Output, Input>
  extends AbstractSourcedParser<List<Output>, Output, Input> {

	/**
	 * A {@link CardinalParser} that applies the inner parser zero or more
	 * times, collecting all results.
	 *
	 * <p>Parsing stops as soon as the inner parser fails, without itself
	 * failing. If the inner parser never matches, an empty list is returned.
	 *
	 * @param <Output> the element type produced by each parse
	 * @param <Input> the type of element consumed from the input
	 */
	public static class ZeroOrMore<Output, Input>
	  extends CardinalParser<Output, Input> {

		/**
		 * Creates a new {@code ZeroOrMore} parser.
		 *
		 * @param elementParser the inner parser to repeat; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public ZeroOrMore(
		  final Parser<Output, Input> elementParser,
		  final Logger                logger
		) {
			super(elementParser, logger);
		}

		/**
		 * Applies the inner parser as many times as possible, collecting
		 * results into an unmodifiable list.
		 *
		 * @param parserInput the input to parse; never {@code null}
		 * @return a {@link ParserResult} with a (possibly empty) list
		 * @throws ParserError never — this parser always succeeds
		 */
		@Override
		public ParserResult<List<Output>, Input> parse(
		  @NonNull final ParserInput<Input> parserInput
		) throws ParserError {
			final var logger  = getLogger();
			final var results = new java.util.ArrayList<Output>();
			logger.info("processing `zeroOrMore` parser");
			final var lastInput = getLastInput(parserInput, results);
			return new ParserResult<>(
			  Collections.unmodifiableList(results), lastInput
			);
		}
	}

	/**
	 * A {@link CardinalParser} that applies the inner parser one or more times,
	 * collecting all results.
	 *
	 * <p>The first application is mandatory: if it fails, the error is
	 * propagated. Subsequent applications stop as soon as the inner parser
	 * fails.
	 *
	 * @param <Output> the element type produced by each parse
	 * @param <Input> the type of element consumed from the input
	 */
	public static class OneOrMore<Output, Input>
	  extends CardinalParser<Output, Input> {

		/**
		 * Creates a new {@code OneOrMore} parser.
		 *
		 * @param parserElement the inner parser to repeat; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public OneOrMore(
		  final Parser<Output, Input> parserElement,
		  final Logger                logger
		) {
			super(parserElement, logger);
		}

		/**
		 * Applies the inner parser at least once, then as many times as
		 * possible, collecting results into an unmodifiable list.
		 *
		 * @param parserInput the input to parse; never {@code null}
		 * @return a {@link ParserResult} with a non-empty list
		 * @throws ParserError if the first application of the inner parser
		 *   fails
		 */
		@Override
		public ParserResult<List<Output>, Input> parse(
		  @NonNull final ParserInput<Input> parserInput
		) throws ParserError {
			final var logger  = getLogger();
			final var results = new java.util.ArrayList<Output>();
			logger.info("processing `oneOrMore` parser");
			final var firstInput = getFirstInput(parserInput, results);
			final var lastInput  = getLastInput(firstInput, results);
			return new ParserResult<>(
			  Collections.unmodifiableList(results), lastInput
			);
		}

		private @NonNull ParserInput<Input> getFirstInput(
		  final ParserInput<Input> parserInput,
		  final ArrayList<Output> results
		) throws ParserError {
			getLogger().debug("processing a first of a series");
			final var firstResult = getParserSource().parse(parserInput);
			results.addFirst(firstResult.result());
			final var firstInput = firstResult.remainder();
			return firstInput;
		}
	}

	/**
	 * Creates a new {@code CardinalParser}.
	 *
	 * @param parserSource the inner parser to repeat; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public CardinalParser(
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
	}

	/**
	 * Repeatedly applies the inner parser on {@code input} until it fails,
	 * appending each result to
	 * {@code results}.
	 *
	 * @param input the input to start from; never {@code null}
	 * @param results the list to append results to; never {@code null}
	 * @return the input position after the last successful application
	 */
	protected ParserInput<Input> getLastInput(
	  final ParserInput<Input> input,
	  final ArrayList<Output> results
	) {
		var logger       = getLogger();
		var currentInput = input;
		logger.debug("processing a series");
		while (!currentInput.isEmpty()) {
			try {
				currentInput = processNext(results, currentInput);
			} catch (final ParserError err) {
				logger.debug("encountered end of series: {}", results);
				break;
			}
		}
		return currentInput;
	}

	private ParserInput<Input> processNext(
	  final ArrayList<Output> results,
	  ParserInput<Input>      currentInput
	) throws ParserError {
		final var result = getParserSource().parse(currentInput);
		results.addLast(result.result());
		currentInput = result.remainder();
		return currentInput;
	}
}
