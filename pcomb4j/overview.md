# pcomb4j

______________________________________________________________________

`pcomb4j` is a lightweight, type-safe, and functional **parser combinator library** for modern Java.

Designed for Java 21+ and leveraging features such as records and pattern matching, it provides a simple yet expressive API for constructing parsers through composition.

______________________________________________________________________

## Core Components

The library is built around a few central abstractions:

1. **`ParserInput<Element>`**: An immutable input stream of characters or tokens. Immutability enables backtrackable parsing — a parser can backtrack simply by keeping a reference to a previous input state.
1. **`Parser<Output, Input>`**: The primary functional interface representing a parsing function. It consumes elements of type `Input` from a `ParserInput` and, on success, produces a `ParserResult<Output, Input>` or throws a `ParserError` on failure.
1. **`ParserResult<Output, Input>`**: A record that encapsulates the successfully parsed value (`result`) and the remaining input (`remainder`).
1. **`ParserError`**: An exception hierarchy (consisting of `Leaf`, `Branch`, and `Wrapped` errors) representing parse failures.
1. **`ParserCombinator`**: A decorator that wraps any parser to provide a fluent combinator API for composition, transformation, and debug logging.

______________________________________________________________________

## Usage Example

The following example demonstrates how to build and execute a simple parser that parses a comma-separated list of characters (e.g., `a,b,c`).

```java
import io.github.sebhajek.pcomb4j.*;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Main {
	public static void main(String[] args) throws ParserError {
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
```
