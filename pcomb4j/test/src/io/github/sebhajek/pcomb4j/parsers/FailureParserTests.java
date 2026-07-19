package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FailureParserTests {

	private final Logger logger =
	  LoggerFactory.getLogger(FailureParserTests.class);

	@Test
	void alwaysFailsOnEmptyInput() {
		final var parser =
		  ParserFactory.withLogger(logger).<Character>alwaysFails(
		    () -> new LiteralParser.LiteralNotMatched()
		  );
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void alwaysFailsOnNonEmptyInput() {
		final var parser =
		  ParserFactory.withLogger(logger).<Character>alwaysFails(
		    () -> new LiteralParser.LiteralNotMatched()
		  );
		final var input = ParserInput.fromString("abc");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
