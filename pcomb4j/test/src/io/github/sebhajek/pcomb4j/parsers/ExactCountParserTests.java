package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ExactCountParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(ExactCountParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void exactCountZeroReturnsEmptyList() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().exactCount(0);
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().getCurrent()).isEqualTo('a');
	}

	@Test
	void exactCountParsesOneElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().exactCount(1);
		final var input  = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void exactCountParsesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().exactCount(3);
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void exactCountFailsOnShortInput() {
		final var parser = PARSER_FACTORY.<Character>any().exactCount(3);
		final var input  = ParserInput.fromString("ab");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void exactCountLeavesRemainderOnLongInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().exactCount(2);
		final var input  = ParserInput.fromString("abcd");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('c');
	}
}
