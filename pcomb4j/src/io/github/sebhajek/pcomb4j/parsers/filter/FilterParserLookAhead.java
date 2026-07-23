package io.github.sebhajek.pcomb4j.parsers.filter;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.function.Predicate;

public class FilterParserLookAhead<Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	private final Predicate<Input> predicate;

	public FilterParserLookAhead(
	  final Parser<Output, Input> parserSource,
	  final Predicate<Input> predicate,
	  final Logger           logger
	) {
		super(parserSource, logger);
		this.predicate = predicate;
	}

	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `filterLookAhead` parser");
		final var parserResult = getParserSource().parse(parserInput);
		if (getPredicate().test(parserResult.remainder().getCurrent())) {
			logger.trace(
			  "`filterLookAhead` parser succeeded: {}", parserResult.remainder()
			);
			return parserResult;
		} else {
			logger.trace(
			  "`filterLookAhead` parser failed: {}",
			  parserResult.remainder().getCurrent()
			);
			throw new FilterParserNotSatisfied();
		}
	}

	protected Predicate<Input> getPredicate() { return predicate; }
}
