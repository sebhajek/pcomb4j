package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OptionalParserTests {
	private static final Logger LOGGER =
	  LoggerFactory.getLogger(OptionalParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void optionalReturnsSomeOnSuccess() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().optional();
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(Optional.of('a'));
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void optionalReturnsEmptyOnFailure() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().optional();
		final var input  = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo(Optional.empty());
		assertThat(result.remainder().isEmpty()).isTrue();
	}
}

