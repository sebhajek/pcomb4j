package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(OrParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void orElseSucceedsWithLeft() throws ParserError {
		final var parser =
		  PARSER_FACTORY.literal('a').orElse(PARSER_FACTORY.literal('b'));
		final var input = ParserInput.fromString("ac");

		final var result = parser.parse(input);

		assertThat(result.result())
		  .isInstanceOf(ParserResult.Either.Left.class);
		assertThat(
		  ((ParserResult.Either.Left<Character, Character>) result.result())
		    .value()
		)
		  .isEqualTo('a');
	}

	@Test
	void orElseSucceedsWithRight() throws ParserError {
		final var parser =
		  PARSER_FACTORY.literal('x').orElse(PARSER_FACTORY.literal('y'));
		final var input = ParserInput.fromString("yc");

		final var result = parser.parse(input);

		assertThat(result.result())
		  .isInstanceOf(ParserResult.Either.Right.class);
		assertThat(
		  ((ParserResult.Either.Right<Character, Character>) result.result())
		    .value()
		)
		  .isEqualTo('y');
	}

	@Test
	void orElseFailsWhenBothFail() {
		final var parser =
		  PARSER_FACTORY.literal('x').orElse(PARSER_FACTORY.literal('y'));
		final var input = ParserInput.fromString("z");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input))
		  .isInstanceOf(ParserError.Branch.class);
	}

	@Test
	void orSucceedsWithLeftParser() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().or(PARSER_FACTORY.literal('z'));
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void orSucceedsWithRightParser() throws ParserError {
		final var parser =
		  PARSER_FACTORY.literal('x').or(PARSER_FACTORY.<Character>any());
		final var input = ParserInput.fromString("yz");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('y');
		assertThat(result.remainder().getCurrent()).isEqualTo('z');
	}

	@Test
	void orFailsWhenBothFail() {
		final var orParser =
		  PARSER_FACTORY.literal('x').or(PARSER_FACTORY.literal('y'));
		final var input = ParserInput.fromString("z");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> orParser.parse(input));
	}
}
