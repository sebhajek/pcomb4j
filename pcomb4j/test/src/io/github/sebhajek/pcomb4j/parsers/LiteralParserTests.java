package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LiteralParserTests {

	private static final Logger logger =
	  LoggerFactory.getLogger(LiteralParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(logger);

	@Test
	void literalParsesExactMatch() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a');
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void literalRejectsMismatch() {
		final var parser = PARSER_FACTORY.literal('a');
		final var input  = ParserInput.fromString("bcd");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void literalParsesWhenCurrentElementMatches() throws ParserError {
		final var parser = PARSER_FACTORY.literal(42);
		final var input  = ParserInput.fromList(List.of(42, 1, 2, 3));

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(42);
		assertThat(result.remainder().getCurrent()).isEqualTo(1);
	}
}
