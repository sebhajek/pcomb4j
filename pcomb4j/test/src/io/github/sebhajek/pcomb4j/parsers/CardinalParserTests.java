package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CardinalParserTests {

	private static final Logger logger =
	  LoggerFactory.getLogger(CardinalParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(logger);

	@Test
	void zeroOrMoreParsesEmptyInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMore();
		final var input  = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreParsesSingleElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMore();
		final var input  = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreParsesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMore();
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreStopsAfterFailure() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a').zeroOrMore();
		final var input  = ParserInput.fromString("ayb");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('y');
	}

	@Test
	void oneOrMoreParsesSingleElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMore();
		final var input  = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreParsesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMore();
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreRequiresFirst() {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMore();
		final var input  = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void oneOrMoreStopsAfterSubsequentFailure() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a').oneOrMore();
		final var input  = ParserInput.fromString("aab");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}
}
