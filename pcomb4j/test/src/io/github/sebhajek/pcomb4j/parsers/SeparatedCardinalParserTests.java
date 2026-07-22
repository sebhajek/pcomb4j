package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SeparatedCardinalParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(SeparatedCardinalParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	void zeroOrMoreSeparatedParsesEmptyInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMoreSeparated(
		  PARSER_FACTORY.literal(',')
		);
		final var input = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedParsesSingleElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMoreSeparated(
		  PARSER_FACTORY.literal(',')
		);
		final var input = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedParsesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMoreSeparated(
		  PARSER_FACTORY.literal(',')
		);
		final var input = ParserInput.fromString("a,b,c");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedStopsAfterNoSeparator() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a').zeroOrMoreSeparated(
		  PARSER_FACTORY.literal(',')
		);
		final var input = ParserInput.fromString("ay");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('y');
	}

	@Test
	void oneOrMoreSeparatedRequiresFirstElement() {
		final var parser = PARSER_FACTORY.literal('a').oneOrMoreSeparated(
		  PARSER_FACTORY.literal(',')
		);
		final var input = ParserInput.fromString("b");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void oneOrMoreSeparatedParsesSingleElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMoreSeparated(
		  PARSER_FACTORY.literal(',')
		);
		final var input = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreSeparatedParsesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMoreSeparated(
		  PARSER_FACTORY.literal(',')
		);
		final var input = ParserInput.fromString("a,b,c");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedTrailingAcceptsTrailingSeparator()
	  throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().zeroOrMoreSeparatedTrailing(
		    PARSER_FACTORY.literal(',')
		  );
		final var input = ParserInput.fromString("a,b,");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedTrailingAcceptsNoTrailing() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().zeroOrMoreSeparatedTrailing(
		    PARSER_FACTORY.literal(',')
		  );
		final var input = ParserInput.fromString("a,b");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedTrailingEmptyInput() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().zeroOrMoreSeparatedTrailing(
		    PARSER_FACTORY.literal(',')
		  );
		final var input = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreSeparatedTrailingAcceptsTrailingSeparator()
	  throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().oneOrMoreSeparatedTrailing(
		    PARSER_FACTORY.literal(',')
		  );
		final var input = ParserInput.fromString("a,b,");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreSeparatedTrailingRequiresFirstElement() {
		final var parser =
		  PARSER_FACTORY.literal('a').oneOrMoreSeparatedTrailing(
		    PARSER_FACTORY.literal(',')
		  );
		final var input = ParserInput.fromString("b");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}
}
