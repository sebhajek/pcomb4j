package io.github.sebhajek.pcomb4j;

import org.slf4j.LoggerFactory;

import io.github.sebhajek.pcomb4j.parsers.primitive.LiteralParserNotMatched;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ExampleTest {

	@Test
	public void csvExample() throws ParserError {
		final var logger  = LoggerFactory.getLogger("ParserLogger");
		final var factory = ParserFactory.withLogger(logger);

		// 1. Define primitive parsers
		final var a     = factory.literal('a');
		final var b     = factory.literal('b');
		final var c     = factory.literal('c');
		final var comma = factory.literal(',');

		// 2. Combine them to parse any of the characters 'a', 'b', or 'c'
		final var item = ParserCombinator.withLogger(logger).from(a.or(b, c));

		// 3. Build a CSV parser: one or more items separated by commas
		// (item + comma) zero or more times, followed by a trailing item
		final var csvParser =
		  item.andFirst(comma).zeroOrMore().build().and(item).map(sequence -> {
			  final var list = new ArrayList<>(sequence.first());
			  list.add(sequence.second());
			  return list;
		  });

		// 4. Parse an input string
		final var input  = ParserInput.fromString("a,b,c");
		final var result = csvParser.parse(input);

		// Output: [a, b, c]
		assert result.result().equals(List.of('a', 'b', 'c'));
	}

	@Test
	public void arithmeticExpression() throws ParserError {
		final var logger  = LoggerFactory.getLogger("ParserLogger");
		final var factory = ParserFactory.withLogger(logger);

		// Parser for a single digit: 0-9
		final var term = factory.<Character>any()
		    .filter(Character::isDigit)
		    .map(c -> c - '0');

		// Parser for sum: 1+2+3 evaluates as ((1+2)+3) = 6
		final var sum = term.chainLeft(factory.literal('+'), (a, op, b) -> a + b);

		final var result = sum.parse(ParserInput.fromString("1+2+3"));
		assert result.result() == 6;
	}

	@Test
	public void setBasedParsing() throws ParserError {
		final var logger  = LoggerFactory.getLogger("ParserLogger");
		final var factory = ParserFactory.withLogger(logger);

		// Match any hexadecimal digit
		final var hexDigit = factory.anyOf('0', '1', '2', '3', '4', '5', '6', '7',
		    '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');

		// Match any character that is NOT a vowel
		final var consonant = factory.noneOf('a', 'e', 'i', 'o', 'u');

		final var r1 = hexDigit.parse(ParserInput.fromString("f"));
		assert r1.result() == 'f';

		final var r2 = consonant.parse(ParserInput.fromString("z"));
		assert r2.result() == 'z';
	}

	@Test
	public void bracketedExpressions() throws ParserError {
		final var logger  = LoggerFactory.getLogger("ParserLogger");
		final var factory = ParserFactory.withLogger(logger);

		final var digit = factory.<Character>any().filter(Character::isDigit).map(c -> c - '0');

		// Mandatory parentheses around a digit: (5)
		final var paren = digit.surround(factory.literal('('), factory.literal(')'));

		// Optional square brackets around a digit: [5] or just 5
		final var bracket = digit.surroundOptional(factory.literal('['),
		    factory.literal(']'));

		final var r1 = paren.parse(ParserInput.fromString("(5)"));
		assert r1.result() == 5;

		final var r2 = bracket.parse(ParserInput.fromString("[3]"));
		assert r2.result() == 3;

		final var r3 = bracket.parse(ParserInput.fromString("7"));
		assert r3.result() == 7;
	}

	@Test
	public void contextDependentFlatMap() throws ParserError {
		final var logger  = LoggerFactory.getLogger("ParserLogger");
		final var factory = ParserFactory.withLogger(logger);

		// Parse a leading character, then expect a matching closing bracket
		final var matchingBrackets = factory.<Character>any().flatMap(opener -> {
		    if (opener == '(') return factory.literal(')');
		    if (opener == '[') return factory.literal(']');
		    if (opener == '{') return factory.literal('}');
		    return factory.alwaysFails(() -> new LiteralParserNotMatched());
		});

		final var r1 = matchingBrackets.parse(ParserInput.fromString("()"));
		assert r1.result() == ')';

		final var r2 = matchingBrackets.parse(ParserInput.fromString("[]"));
		assert r2.result() == ']';
	}
}
