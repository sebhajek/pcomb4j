package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MapParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(MapParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void mapTransformsResult() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a').map(Object::toString);
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo("a");
	}

	@Test
	void pureReturnsConstant() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().pure("constant");
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo("constant");
	}

	@Test
	void castWidensType() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a').cast(Object.class);
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
	}
}

