package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FilterParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(FilterParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void filterAcceptsMatchingElement() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().filter((c) -> c == 'a');
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void filterRejectsNonMatchingElement() {
		final var parser =
		  PARSER_FACTORY.<Character>any().filter((c) -> c == 'a');
		final var input = ParserInput.fromString("bcd");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void filterByValueMatching() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().filter('a');
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void filterByValueNonMatching() {
		final var parser = PARSER_FACTORY.<Character>any().filter('a');
		final var input  = ParserInput.fromString("bcd");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void filterByTypeMatching() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().filter(Character.class);
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void filterByTypeNonMatching() {
		final var parser = PARSER_FACTORY.<Character>literal('a')
		                     .cast(Object.class)
		                     .filter(Integer.class);
		final var input  = ParserInput.fromString("a");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
