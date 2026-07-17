package org.codeberg.sebhajek.pcomb4j.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public abstract class CardinalParser<Output, Input>
  extends AbstractSourcedParser<List<Output>, Output, Input> {

	public static class ZeroOrMore<Output, Input>
	  extends CardinalParser<Output, Input> {

		public ZeroOrMore(
		  final Parser<Output, Input> elementParser,
		  final Logger                logger
		) {
			super(elementParser, logger);
		}

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

	public static class OneOrMore<Output, Input>
	  extends CardinalParser<Output, Input> {

		public OneOrMore(
		  final Parser<Output, Input> parserElement,
		  final Logger                logger
		) {
			super(parserElement, logger);
		}

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

	public CardinalParser(
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
	}

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
