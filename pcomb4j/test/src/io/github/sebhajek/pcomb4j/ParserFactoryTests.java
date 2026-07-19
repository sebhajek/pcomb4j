package io.github.sebhajek.pcomb4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ParserFactoryTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(ParserFactoryTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void parserFactoryWithLogger() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any();
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}
}
