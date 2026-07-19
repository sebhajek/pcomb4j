package io.github.sebhajek.pcomb4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ParserCombinatorTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(ParserCombinatorTests.class);
	private static final ParserCombinator.Builder PARSER_COMBINATOR =
	  ParserCombinator.withLogger(LOGGER);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void parserCombinatorWrapsParser() throws ParserError {
		final var parser     = PARSER_FACTORY.<Character>any();
		final var combinator = PARSER_COMBINATOR.from(parser);
		final var input      = ParserInput.fromString("abc");

		final var result = combinator.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}
}

