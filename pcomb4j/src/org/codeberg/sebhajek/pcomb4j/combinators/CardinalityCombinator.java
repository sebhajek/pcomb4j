package org.codeberg.sebhajek.pcomb4j.combinators;

import java.util.ArrayList;
import java.util.List;

import org.codeberg.sebhajek.pcomb4j.ParserCombinator;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CardinalityCombinator<A, B> extends DelegateParser<A, B> {

	public static final Logger LOGGER =
	  LoggerFactory.getLogger(CardinalityCombinator.class);

	public default ParserCombinator<List<A>, B> zeroOrMore() {
		LOGGER.debug("building `zeroOrMore` parser");
		final var logger = getLogger();
		return ParserCombinator.withLogger(logger).from(input -> {
			final var results = new java.util.ArrayList<A>();
			logger.info("processing `zeroOrMore` parser");
			final var lastInput = getLastInput(input, results, logger);
			return new ParserResult<>(results, lastInput);
		});
	}

	public default ParserCombinator<List<A>, B> oneOrMore() {
		LOGGER.debug("building `oneOrMore` parser");
		final var logger = getLogger();
		return ParserCombinator.withLogger(logger)
		  .from(getParser())
		  .flatMap(first -> {
			  logger.info("processed first of `oneOrMore` parser");
			  return ParserCombinator.withLogger(logger)
			    .from(getParser())
			    .zeroOrMore()
			    .map(rest -> {
				    final var list = new ArrayList<A>();
				    list.addFirst(first);
				    list.addAll(rest);
				    return list;
			    });
		  });
	}

	private ParserInput<B> getLastInput(
	  final ParserInput<B> input,
	  final ArrayList<A> results,
	  final Logger       logger
	) {
		var currentInput = input;
		while (!currentInput.isEmpty()) {
			try {
				logger.debug("processing first of a series");
				currentInput = processNext(results, currentInput);
			} catch (final ParserError err) {
				logger.debug("encountered end of series: {}", results);
				break;
			}
		}
		return currentInput;
	}

	private ParserInput<B> processNext(
	  final ArrayList<A> results,
	  ParserInput<B>     currentInput
	) throws ParserError {
		final var result = getParser().parse(currentInput);
		results.addLast(result.result());
		currentInput = result.remainder();
		return currentInput;
	}
}
