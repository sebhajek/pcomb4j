package org.codeberg.sebhajek.pcomb4j.parsers;

import java.util.Optional;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class OptionalParser<Output, Input>
  extends AbstractSourcedParser<Optional<Output>, Output, Input> {

	public OptionalParser(Parser<Output, Input> parserSource, Logger logger) {
		super(parserSource, logger);
	}

	@Override
	public ParserResult<Optional<Output>, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		var logger = getLogger();
		logger.info("processing `optional` parser");
		try {
			final var result = getParserSource().parse(parserInput);
			logger.debug("`optional` parser succeeded: {}", result.result());
			return result.with(Optional.of(result.result()));
		} catch (final ParserError err) {
			logger.debug("`optional` parser failed");
			return new ParserResult<>(Optional.empty(), parserInput);
		}
	}
}
