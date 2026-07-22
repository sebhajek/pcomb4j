package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UntilCardinalParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(UntilCardinalParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	private static final Predicate<Character> SENTINEL_X = c -> c == 'x';

	@Test
	void zeroOrMoreUntilPredicateEmptyInput() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().zeroOrMoreUntil(SENTINEL_X);
		final var input = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreUntilPredicateStopsAtSentinel() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().zeroOrMoreUntil(SENTINEL_X);
		final var input = ParserInput.fromString("abxc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('x');
	}

	@Test
	void zeroOrMoreUntilPredicateNoSentinel() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().zeroOrMoreUntil(SENTINEL_X);
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreUntilPredicateRequiresFirst() {
		final var parser = PARSER_FACTORY.literal('a').oneOrMoreUntil(
		  (Predicate<Character>) (c -> c == 'x')
		);
		final var input = ParserInput.fromString("b");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void oneOrMoreUntilPredicateStopsAtSentinel() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().oneOrMoreUntil(SENTINEL_X);
		final var input = ParserInput.fromString("abxc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('x');
	}

	@Test
	void zeroOrMoreUntilParserEmptyInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMoreUntil(
		  PARSER_FACTORY.literal('x')
		);
		final var input = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreUntilParserStopsAtSentinel() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMoreUntil(
		  PARSER_FACTORY.literal('x')
		);
		final var input = ParserInput.fromString("abxc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('x');
	}

	@Test
	void zeroOrMoreUntilParserNoSentinel() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMoreUntil(
		  PARSER_FACTORY.literal('x')
		);
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreUntilParserRequiresFirst() {
		final var parser = PARSER_FACTORY.literal('a').oneOrMoreUntil(
		  PARSER_FACTORY.literal('x')
		);
		final var input = ParserInput.fromString("b");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void oneOrMoreUntilParserStopsAtSentinel() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMoreUntil(
		  PARSER_FACTORY.literal('x')
		);
		final var input = ParserInput.fromString("abxc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('x');
	}
}
