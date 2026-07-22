package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AssociativeChainParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(AssociativeChainParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void chainLeftParsesSingleTerm() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainLeft(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("5");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(5);
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void chainLeftParsesTwoTerms() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainLeft(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("3+4");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(7);
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void chainLeftParsesThreeTerms() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainLeft(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("1+2+3");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(6);
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void chainLeftDemonstratesLeftAssociativity() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainLeft(
		      PARSER_FACTORY.literal('^'), (a, op, b) -> (int) Math.pow(a, b)
		    );
		final var input = ParserInput.fromString("2^3^2");

		final var result = parser.parse(input);

		//  (2^3)^2 = 8^2 = 64, not 2^(3^2) = 512
		assertThat(result.result()).isEqualTo(64);
	}

	@Test
	void chainLeftRejectsTrailingOperator() {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainLeft(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("1+");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void chainLeftFailsOnEmptyInput() {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainLeft(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void chainLeftFailsWhenTermCannotParse() {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainLeft(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("a");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void chainRightParsesSingleTerm() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainRight(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("5");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(5);
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void chainRightParsesTwoTerms() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainRight(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("3+4");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(7);
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void chainRightParsesThreeTerms() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainRight(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("1+2+3");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(6);
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void chainRightDemonstratesRightAssociativity() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainRight(
		      PARSER_FACTORY.literal('^'), (a, op, b) -> (int) Math.pow(a, b)
		    );
		final var input = ParserInput.fromString("2^3^2");

		final var result = parser.parse(input);

		//  2^(3^2) = 2^9 = 512, not (2^3)^2 = 64
		assertThat(result.result()).isEqualTo(512);
	}

	@Test
	void chainRightRejectsTrailingOperator() {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainRight(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("1+");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void chainRightFailsOnEmptyInput() {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainRight(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void chainRightFailsWhenTermCannotParse() {
		final var parser =
		  PARSER_FACTORY.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0')
		    .chainRight(PARSER_FACTORY.literal('+'), (a, op, b) -> a + b);
		final var input = ParserInput.fromString("a");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
