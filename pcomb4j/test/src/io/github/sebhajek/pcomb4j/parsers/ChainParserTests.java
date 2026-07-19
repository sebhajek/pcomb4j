package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ChainParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(ChainParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void chainParsesSingleParser() throws ParserError {
		final var parser =
		  PARSER_FACTORY.chain(List.of(PARSER_FACTORY.<Character>any()));
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void chainParsesMultipleParsers() throws ParserError {
		final var parser = PARSER_FACTORY.chain(List.of(
		  PARSER_FACTORY.<Character>any(), PARSER_FACTORY.<Character>any()
		));
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('c');
	}

	@Test
	void chainFailsWhenFirstParserFails() {
		final var parser =
		  PARSER_FACTORY.chain(List.of(PARSER_FACTORY.<Character>any()));
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void chainFailsWhenIntermediateParserFails() {
		final var parser = PARSER_FACTORY.chain(List.of(
		  PARSER_FACTORY.<Character>any(), PARSER_FACTORY.<Character>any()
		));
		final var input  = ParserInput.fromString("a");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
