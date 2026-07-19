package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FlatMapParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(FlatMapParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void flatMapChainsTwoParsers() throws Exception {
		final var parser = PARSER_FACTORY.<Character>any().flatMap(
		  (c) -> PARSER_FACTORY.literal('b')
		);
		final var input = ParserInput.fromString("ab");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('b');
		assertThat(result.remainder().isEmpty()).isTrue();
	}
}

