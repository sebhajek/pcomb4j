package org.codeberg.sebhajek.pcomb4j.parsers;

import java.util.ArrayList;
import java.util.List;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class ChainParser<Output, Input>
  extends AbstractParser<List<Output>, Input> {

	final List<Parser<Output, Input>> parsersList;

	public ChainParser(List<Parser<Output, Input>> parsersList, Logger logger) {
		super(logger);
		this.parsersList = parsersList;
	}

	@Override
	public ParserResult<List<Output>, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		var       logger  = getLogger();
		final var results = new ArrayList<Output>(parsersList.size());
		logger.info("applying `chain`");
		final var chainResult = applyChain(parserInput, results, parsersList);
		return new ParserResult<>(results, chainResult);
	}

	private static final <A, B> ParserInput<B> applyChain(
	  final ParserInput<B> input,
	  final ArrayList<A> results,
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
