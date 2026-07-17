package org.codeberg.sebhajek.pcomb4j.parsers;

import java.util.Comparator;
import java.util.Optional;

import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class LiteralParser<Input> extends AbstractParser<Input, Input> {

	public static final class LiteralNotMatched extends ParserError.Leaf {

		public LiteralNotMatched() {
			super("Literal not matched.");
		}
	}

	private final Input literal;

	private final Optional<Comparator<Input>> comparator;

	public LiteralParser(
			Input literal,
			Comparator<Input> comparator,
			Logger logger) {
		super(logger);
		this.literal = literal;
		this.comparator = Optional.ofNullable(comparator);
	}

	public LiteralParser(Input literal, Logger logger) {
		super(logger);
		this.literal = literal;
		this.comparator = Optional.empty();
	}

	@Override
	public ParserResult<Input, Input> parse(
			@NonNull ParserInput<Input> parserInput) throws ParserError {
		final var current = parserInput.getCurrent();
		var logger = getLogger();
		var comparison = comparator.isPresent()
				? comparator.orElseThrow().compare(current, literal) == 0
				: literal.equals(current);
		if (comparison) {
			logger.debug("getting `literal`: {} == {}", current, literal);
			return new ParserResult<>(current, parserInput.advance());
		} else {
			logger.debug("getting `literal`: {} != {}", current, literal);
			throw new LiteralNotMatched();
		}
	}
}
