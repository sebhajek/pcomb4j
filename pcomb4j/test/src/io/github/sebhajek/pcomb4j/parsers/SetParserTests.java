package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import io.github.sebhajek.pcomb4j.parsers.primitive.SetParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SetParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(SetParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void anyOfParsesWhenElementIsInSet() throws ParserError {
		final var parser = PARSER_FACTORY.anyOf('a', 'b', 'c');
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void anyOfRejectsWhenElementIsNotInSet() {
		final var parser = PARSER_FACTORY.anyOf('a', 'b', 'c');
		final var input  = ParserInput.fromString("def");

		assertThatExceptionOfType(SetParser.SetNotMatched.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void anyOfParsesIntegerElementInSet() throws ParserError {
		final var parser = PARSER_FACTORY.anyOf(1, 2, 3);
		final var input  = ParserInput.fromList(List.of(1, 4, 5));

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(1);
		assertThat(result.remainder().getCurrent()).isEqualTo(4);
	}

	@Test
	void anyOfWithCollectionParsesMatch() throws ParserError {
		final var parser = PARSER_FACTORY.anyOf(List.of('x', 'y', 'z'));
		final var input  = ParserInput.fromString("xyz");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('x');
		assertThat(result.remainder().getCurrent()).isEqualTo('y');
	}

	@Test
	void anyOfWithCollectionRejectsNonMatch() {
		final var parser = PARSER_FACTORY.anyOf(List.of('a', 'b', 'c'));
		final var input  = ParserInput.fromString("def");

		assertThatExceptionOfType(SetParser.SetNotMatched.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void noneOfParsesWhenElementIsNotInSet() throws ParserError {
		final var parser = PARSER_FACTORY.noneOf('a', 'b', 'c');
		final var input  = ParserInput.fromString("def");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('d');
		assertThat(result.remainder().getCurrent()).isEqualTo('e');
	}

	@Test
	void noneOfRejectsWhenElementIsInSet() {
		final var parser = PARSER_FACTORY.noneOf('a', 'b', 'c');
		final var input  = ParserInput.fromString("abc");

		assertThatExceptionOfType(SetParser.SetNotMatched.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void noneOfParsesIntegerElementNotInSet() throws ParserError {
		final var parser = PARSER_FACTORY.noneOf(1, 2, 3);
		final var input  = ParserInput.fromList(List.of(4, 1, 5));

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(4);
		assertThat(result.remainder().getCurrent()).isEqualTo(1);
	}

	@Test
	void noneOfWithCollectionParsesNonMatch() throws ParserError {
		final var parser = PARSER_FACTORY.noneOf(List.of('a', 'b', 'c'));
		final var input  = ParserInput.fromString("def");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('d');
		assertThat(result.remainder().getCurrent()).isEqualTo('e');
	}

	@Test
	void noneOfWithCollectionRejectsMatch() {
		final var parser = PARSER_FACTORY.noneOf(List.of('x', 'y', 'z'));
		final var input  = ParserInput.fromString("xyz");

		assertThatExceptionOfType(SetParser.SetNotMatched.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
