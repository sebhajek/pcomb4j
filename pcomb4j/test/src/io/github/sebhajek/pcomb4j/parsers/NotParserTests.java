package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class NotParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(NotParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void notAcceptsWhenNegativeFails() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().not(PARSER_FACTORY.literal('a'));
		final var input = ParserInput.fromString("bcd");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('b');
		assertThat(result.remainder().getCurrent()).isEqualTo('c');
	}

	@Test
	void notRejectsWhenNegativeSucceeds() {
		final var parser =
		  PARSER_FACTORY.<Character>any().not(PARSER_FACTORY.literal('a'));
		final var input = ParserInput.fromString("abc");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void notRejectsWhenSourceFails() {
		final var parser =
		  PARSER_FACTORY.<Character>any().not(PARSER_FACTORY.literal('a'));
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
