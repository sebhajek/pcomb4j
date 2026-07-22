package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EofParserTests {
	private static final Logger LOGGER =
	  LoggerFactory.getLogger(EofParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void succeedsOnEmptyString() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>eof();
		final var input  = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void failsOnNonEmptyString() {
		final var parser = PARSER_FACTORY.<Character>eof();
		final var input  = ParserInput.fromString("a");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void succeedsOnEmptyList() throws ParserError {
		final var parser = PARSER_FACTORY.<Integer>eof();
		final var input  = ParserInput.<Integer>fromList(java.util.List.of());

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void failsOnNonEmptyList() {
		final var parser = PARSER_FACTORY.<Integer>eof();
		final var input  = ParserInput.fromList(java.util.List.of(1, 2, 3));

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
