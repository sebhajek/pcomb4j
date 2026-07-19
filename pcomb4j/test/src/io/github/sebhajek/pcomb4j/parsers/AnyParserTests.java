package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AnyParserTests {
	private static final Logger LOGGER =
	  LoggerFactory.getLogger(AnyParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void parsesFirstCharacterAndAdvances() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any();
		final var input  = ParserInput.fromString("abc");
		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void consumesEveryElementOfAListInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Integer>any();
		final var input  = ParserInput.fromList(java.util.List.of(1, 2, 3));

		final var result1 = parser.parse(input);
		final var result2 = parser.parse(result1.remainder());
		final var result3 = parser.parse(result2.remainder());

		assertThat(
		  List.of(result1.result(), result2.result(), result3.result())
		)
		  .containsExactly(1, 2, 3);
		assertThat(result3.remainder().isEmpty()).isTrue();
	}

	@Test
	void throwsParserErrorOnEmptyInput() {
		final var parser = PARSER_FACTORY.<Character>any();
		final var input  = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
