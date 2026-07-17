package org.codeberg.sebhajek.pcomb4j.parsers;

import java.util.function.Predicate;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class FilterParser<Output, Input>
		extends AbstractSourcedParser<Output, Output, Input> {

	public static class NotSatisfied extends ParserError.Leaf {

		public NotSatisfied() {
			super("Not satisfied");
		}
	}

	private final Predicate<Output> predicate;

	public FilterParser(
			final Parser<Output, Input> parserSource,
			final Predicate<Output> predicate,
			final Logger logger) {
		super(parserSource, logger);
		this.predicate = predicate;
	}

	@Override
	public ParserResult<Output, Input> parse(
			@NonNull final ParserInput<Input> parserInput) throws ParserError {
		final var logger = getLogger();
		logger.info("processing `filter` parser");
		final var parserResult = getParserSource().parse(parserInput);
		if (getPredicate().test(parserResult.result())) {
			logger.debug(
					"`filter` parser succeeded: {}", parserResult.result());
			return parserResult;
		} else {
			logger.debug(
					"`filter` parser failed: {}", parserInput.getCurrent());
			throw new NotSatisfied();
		}
	}

	protected Predicate<Output> getPredicate() {
		return predicate;
	}
}
