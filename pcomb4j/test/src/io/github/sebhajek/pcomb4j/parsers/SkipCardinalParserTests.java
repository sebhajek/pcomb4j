package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SkipCardinalParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(SkipCardinalParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void zeroOrMoreSkipParsesEmptyInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMoreSkip();
		final var input  = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSkipConsumesSingleElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMoreSkip();
		final var input  = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSkipConsumesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMoreSkip();
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSkipStopsAfterFailure() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a').zeroOrMoreSkip();
		final var input  = ParserInput.fromString("ayb");

		final var result = parser.parse(input);

		assertThat(result.remainder().getCurrent()).isEqualTo('y');
	}

	@Test
	void oneOrMoreSkipConsumesSingleElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMoreSkip();
		final var input  = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreSkipConsumesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMoreSkip();
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreSkipRequiresFirst() {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMoreSkip();
		final var input  = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void oneOrMoreSkipStopsAfterSubsequentFailure() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a').oneOrMoreSkip();
		final var input  = ParserInput.fromString("aab");

		final var result = parser.parse(input);

		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}
}
