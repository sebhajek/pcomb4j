package io.github.sebhajek.pcomb4j;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ExampleTest {

	@Test
	public void example() throws ParserError {
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
		  item.andFirst(comma).zeroOrMore().and(item).map(sequence -> {
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
}
