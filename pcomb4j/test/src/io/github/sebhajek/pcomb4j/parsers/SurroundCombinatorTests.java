package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SurroundCombinatorTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(SurroundCombinatorTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void surroundWithMandatoryDelimiters() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().surround(
		  PARSER_FACTORY.literal('('), PARSER_FACTORY.literal(')')
		);
		final var input = ParserInput.fromString("(a)");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void surroundMandatoryMissingLeftDelimiter() {
		final var parser = PARSER_FACTORY.<Character>any().surround(
		  PARSER_FACTORY.literal('('), PARSER_FACTORY.literal(')')
		);
		final var input = ParserInput.fromString("a)");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void surroundMandatoryMissingRightDelimiter() {
		final var parser = PARSER_FACTORY.<Character>any().surround(
		  PARSER_FACTORY.literal('('), PARSER_FACTORY.literal(')')
		);
		final var input = ParserInput.fromString("(a");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void surroundMandatoryWithSymmetricDelimiter() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().surround(PARSER_FACTORY.literal('"'));
		final var input = ParserInput.fromString("\"a\"");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void surroundMandatoryPropagatesInnerParseError() {
		final var parser = PARSER_FACTORY.literal('a').surround(
		  PARSER_FACTORY.literal('('), PARSER_FACTORY.literal(')')
		);
		final var input = ParserInput.fromString("(b)");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void surroundOptionalWithBothDelimiters() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().surroundOptional(
		  PARSER_FACTORY.literal('('), PARSER_FACTORY.literal(')')
		);
		final var input = ParserInput.fromString("(a)");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void surroundOptionalMissingLeftDelimiter() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().surroundOptional(
		  PARSER_FACTORY.literal('('), PARSER_FACTORY.literal(')')
		);
		final var input = ParserInput.fromString("a)");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void surroundOptionalMissingRightDelimiter() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().surroundOptional(
		  PARSER_FACTORY.literal('('), PARSER_FACTORY.literal(')')
		);
		final var input = ParserInput.fromString("(a");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void surroundOptionalMissingBothDelimiters() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().surroundOptional(
		  PARSER_FACTORY.literal('('), PARSER_FACTORY.literal(')')
		);
		final var input = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void surroundOptionalWithSymmetricDelimiter() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().surroundOptional(
		  PARSER_FACTORY.literal('"')
		);
		final var input = ParserInput.fromString("\"a\"");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void surroundOptionalPropagatesInnerParseError() {
		final var parser = PARSER_FACTORY.literal('a').surroundOptional(
		  PARSER_FACTORY.literal('('), PARSER_FACTORY.literal(')')
		);
		final var input = ParserInput.fromString("()");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
