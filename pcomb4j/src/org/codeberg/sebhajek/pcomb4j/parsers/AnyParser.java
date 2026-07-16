package org.codeberg.sebhajek.pcomb4j.parsers;

import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class AnyParser<Input> extends AbstractParser<Input, Input> {

	public AnyParser(Logger logger) { super(logger); }

	@Override
	public final ParserResult<Input, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		final var current = parserInput.getCurrent();
		getLogger().debug("getting `any`: {}", current);
		return new ParserResult<Input, Input>(current, parserInput.advance());
	}
}
