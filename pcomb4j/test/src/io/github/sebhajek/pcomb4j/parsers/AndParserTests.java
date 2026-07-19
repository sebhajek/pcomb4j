package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AndParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(AndParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	private static <Input> Parser<Input, Input>
	countingParser(final AtomicInteger calls, final Input valueToReturn) {
		return input -> {
			calls.incrementAndGet();
			return new ParserResult<>(valueToReturn, input.advance());
		};
	}

	@Test
	void collectsSequenceAndAdvancesPastBoth() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().and(PARSER_FACTORY.<Character>any());
		final var input = ParserInput.fromString("ab");

		final var result = parser.parse(input);

		assertThat(result.result().first()).isEqualTo('a');
		assertThat(result.result().second()).isEqualTo('b');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void shortCircuitsBeforeRightRuns() {
		final var parser = PARSER_FACTORY.<Character>any().and(
		  countingParser(new AtomicInteger(), 'z')
		);
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
		assertThat(new AtomicInteger().get()).isZero();
	}

	@Test
	void propagatesAfterLeftSucceeds() {
		final var parser =
		  PARSER_FACTORY.<Character>any().and(PARSER_FACTORY.<Character>any());
		final var input = ParserInput.fromString("a");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void keepsOnlyTheLeftResult() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().andFirst(
		  PARSER_FACTORY.<Character>any()
		);
		final var input = ParserInput.fromString("ab");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void keepsOnlyTheRightResult() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().andSecond(
		  PARSER_FACTORY.<Character>any()
		);
		final var input = ParserInput.fromString("ab");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('b');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void chainingAndProducesNestedSequence() throws ParserError {
		final var any    = PARSER_FACTORY.<Character>any();
		final var parser = any.and(any).and(any);
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result().first().first()).isEqualTo('a');
		assertThat(result.result().first().second()).isEqualTo('b');
		assertThat(result.result().second()).isEqualTo('c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}
}
