package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import io.github.sebhajek.pcomb4j.parsers.primitive.LiteralParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ErrorParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(ErrorParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void labelParserAttachesStaticLabel() {
		final var parser =
		  PARSER_FACTORY.<Character>any().labelError("Input too short");
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input))
		  .satisfies(
		    e -> assertThat(e.getMessage()).contains("Input too short")
		  );
	}

	@Test
	void labelParserPreservesInnerResultOnSuccess() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().labelError("Failed");
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void messageParserAttachesDynamicLabel() {
		final var parser = PARSER_FACTORY.<Character>any().labelError(
		  (input, err) -> "Expected char, got: " + err.getMessage()
		);
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input))
		  .satisfies(e -> {
			  assertThat(e.getMessage()).contains("Expected char, got");
			  assertThat(e).isInstanceOf(ErrorParser.LabeledError.class);
		  });
	}

	@Test
	void messageParserPreservesInnerResultOnSuccess() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().labelError(
		  (input, err) -> "Dynamic error"
		);
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void suppliedParserReplacesError() {
		final var parser = PARSER_FACTORY.<Character>any().withError(
		  (input, err) -> new LiteralParser.LiteralNotMatched()
		);
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input))
		  .satisfies(e -> {
			  assertThat(e.getMessage()).contains("Literal not matched");
			  assertThat(e)
			    .isExactlyInstanceOf(LiteralParser.LiteralNotMatched.class);
		  });
	}

	@Test
	void suppliedParserPreservesInnerResultOnSuccess() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().withError(
		  (input, err) -> new LiteralParser.LiteralNotMatched()
		);
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}
}
