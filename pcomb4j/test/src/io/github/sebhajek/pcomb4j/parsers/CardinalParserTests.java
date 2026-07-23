package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CardinalParserTests {

	private static final Logger logger =
	  LoggerFactory.getLogger(CardinalParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(logger);

	private static final Predicate<Character> SENTINEL_X = c -> c == 'x';

	@Test
	void zeroOrMoreParsesEmptyInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMore().build();
		final var input  = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreParsesSingleElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMore().build();
		final var input  = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreParsesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().zeroOrMore().build();
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreStopsAfterFailure() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a').zeroOrMore().build();
		final var input  = ParserInput.fromString("ayb");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('y');
	}

	@Test
	void oneOrMoreParsesSingleElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMore().build();
		final var input  = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreParsesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMore().build();
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreRequiresFirst() {
		final var parser = PARSER_FACTORY.<Character>any().oneOrMore().build();
		final var input  = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void oneOrMoreStopsAfterSubsequentFailure() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a').oneOrMore().build();
		final var input  = ParserInput.fromString("aab");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'a');
		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void exactCountZeroReturnsEmptyList() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().exactly(0).build();
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().getCurrent()).isEqualTo('a');
	}

	@Test
	void exactCountParsesOneElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().exactly(1).build();
		final var input  = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void exactCountParsesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().exactly(3).build();
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void exactCountFailsOnShortInput() {
		final var parser = PARSER_FACTORY.<Character>any().exactly(3).build();
		final var input  = ParserInput.fromString("ab");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void exactCountLeavesRemainderOnLongInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().exactly(2).build();
		final var input  = ParserInput.fromString("abcd");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('c');
	}

	@Test
	void zeroOrMoreSeparatedParsesEmptyInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any()
		                     .zeroOrMore()
		                     .separatedBy(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedParsesSingleElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any()
		                     .zeroOrMore()
		                     .separatedBy(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedParsesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any()
		                     .zeroOrMore()
		                     .separatedBy(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("a,b,c");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedStopsAfterNoSeparator() throws ParserError {
		final var parser = PARSER_FACTORY.literal('a')
		                     .zeroOrMore()
		                     .separatedBy(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("ay");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().getCurrent()).isEqualTo('y');
	}

	@Test
	void oneOrMoreSeparatedRequiresFirstElement() {
		final var parser = PARSER_FACTORY.literal('a')
		                     .oneOrMore()
		                     .separatedBy(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("b");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void oneOrMoreSeparatedParsesSingleElement() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any()
		                     .oneOrMore()
		                     .separatedBy(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreSeparatedParsesMultipleElements() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any()
		                     .oneOrMore()
		                     .separatedBy(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("a,b,c");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedTrailingAcceptsTrailingSeparator()
	  throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any()
		                     .zeroOrMore()
		                     .separatedByTrailing(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("a,b,");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedTrailingAcceptsNoTrailing() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any()
		                     .zeroOrMore()
		                     .separatedByTrailing(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("a,b");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSeparatedTrailingEmptyInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any()
		                     .zeroOrMore()
		                     .separatedByTrailing(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreSeparatedTrailingAcceptsTrailingSeparator()
	  throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any()
		                     .oneOrMore()
		                     .separatedByTrailing(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("a,b,");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreSeparatedTrailingRequiresFirstElement() {
		final var parser = PARSER_FACTORY.literal('a')
		                     .oneOrMore()
		                     .separatedByTrailing(PARSER_FACTORY.literal(','))
		                     .build();
		final var input  = ParserInput.fromString("b");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void zeroOrMoreSkipParsesEmptyInput() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().zeroOrMore().buildDiscarding();
		final var input = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSkipConsumesSingleElement() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().zeroOrMore().buildDiscarding();
		final var input = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSkipConsumesMultipleElements() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().zeroOrMore().buildDiscarding();
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreSkipStopsAfterFailure() throws ParserError {
		final var parser =
		  PARSER_FACTORY.literal('a').zeroOrMore().buildDiscarding();
		final var input = ParserInput.fromString("ayb");

		final var result = parser.parse(input);

		assertThat(result.remainder().getCurrent()).isEqualTo('y');
	}

	@Test
	void oneOrMoreSkipConsumesSingleElement() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().oneOrMore().buildDiscarding();
		final var input = ParserInput.fromString("a");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreSkipConsumesMultipleElements() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().oneOrMore().buildDiscarding();
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreSkipRequiresFirst() {
		final var parser =
		  PARSER_FACTORY.<Character>any().oneOrMore().buildDiscarding();
		final var input = ParserInput.fromString("");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void oneOrMoreSkipStopsAfterSubsequentFailure() throws ParserError {
		final var parser =
		  PARSER_FACTORY.literal('a').oneOrMore().buildDiscarding();
		final var input = ParserInput.fromString("aab");

		final var result = parser.parse(input);

		assertThat(result.remainder().getCurrent()).isEqualTo('b');
	}

	@Test
	void zeroOrMoreUntilPredicateEmptyInput() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().until(SENTINEL_X).build();
		final var input = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreUntilPredicateStopsAtSentinel() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().until(SENTINEL_X).build();
		final var input = ParserInput.fromString("abxc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('x');
	}

	@Test
	void zeroOrMoreUntilPredicateNoSentinel() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().until(SENTINEL_X).build();
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreUntilPredicateRequiresFirst() {
		final var parser = PARSER_FACTORY.literal('a').atLeastOne(
		  (Predicate<Character>) (c -> c == 'x')
		).build();
		final var input = ParserInput.fromString("b");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void oneOrMoreUntilPredicateStopsAtSentinel() throws ParserError {
		final var parser =
		  PARSER_FACTORY.<Character>any().atLeastOne(SENTINEL_X).build();
		final var input = ParserInput.fromString("abxc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('x');
	}

	@Test
	void zeroOrMoreUntilParserEmptyInput() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().until(
		  PARSER_FACTORY.literal('x')
		).build();
		final var input = ParserInput.fromString("");

		final var result = parser.parse(input);

		assertThat(result.result()).isEmpty();
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void zeroOrMoreUntilParserStopsAtSentinel() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().until(
		  PARSER_FACTORY.literal('x')
		).build();
		final var input = ParserInput.fromString("abxc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('x');
	}

	@Test
	void zeroOrMoreUntilParserNoSentinel() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().until(
		  PARSER_FACTORY.literal('x')
		).build();
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b', 'c');
		assertThat(result.remainder().isEmpty()).isTrue();
	}

	@Test
	void oneOrMoreUntilParserRequiresFirst() {
		final var parser = PARSER_FACTORY.literal('a').atLeastOne(
		  PARSER_FACTORY.literal('x')
		).build();
		final var input = ParserInput.fromString("b");

		assertThatExceptionOfType(ParserError.class)
		  .isThrownBy(() -> parser.parse(input));
	}

	@Test
	void oneOrMoreUntilParserStopsAtSentinel() throws ParserError {
		final var parser = PARSER_FACTORY.<Character>any().atLeastOne(
		  PARSER_FACTORY.literal('x')
		).build();
		final var input = ParserInput.fromString("abxc");

		final var result = parser.parse(input);

		assertThat(result.result()).containsExactly('a', 'b');
		assertThat(result.remainder().getCurrent()).isEqualTo('x');
	}
}
