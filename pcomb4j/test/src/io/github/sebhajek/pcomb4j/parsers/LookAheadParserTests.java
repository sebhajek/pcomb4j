package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LookAheadParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(LookAheadParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void lookAheadAcceptsMatchingAhead() throws ParserError {
		final var parser =
		  PARSER_FACTORY.literal('a').lookAhead(PARSER_FACTORY.literal('b'));
		final var input = ParserInput.fromString("abcd");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void lookAheadDoesNotConsumeAheadElement() throws ParserError {
		final var parser =
		  PARSER_FACTORY.literal('a').lookAhead(PARSER_FACTORY.literal('b'));
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void lookAheadRejectsNonMatchingAhead() {
		final var parser =
		  PARSER_FACTORY.literal('a').lookAhead(PARSER_FACTORY.literal('c'));
		final var input = ParserInput.fromString("abc");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void lookAheadRejectsWhenAheadFails() {
		final var parser =
		  PARSER_FACTORY.literal('a').lookAhead(PARSER_FACTORY.literal('b'));
		final var input = ParserInput.fromString("a");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void lookAheadRejectsWhenSourceFails() {
		final var parser =
		  PARSER_FACTORY.literal('a').lookAhead(PARSER_FACTORY.literal('b'));
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
