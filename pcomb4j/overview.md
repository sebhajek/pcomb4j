# pcomb4j

______________________________________________________________________

`pcomb4j` is a lightweight, type-safe, and functional **parser combinator library** for modern Java.

Designed for Java 21+ and leveraging features such as records and pattern matching, it provides a simple yet expressive API for constructing parsers through composition.

---

## Core Components

The library is built around a few central abstractions:

1. **`ParserInput<Element>`**: An immutable input stream of characters or tokens. Immutable inputs enable backtrackable parsing since a parser can backtrack simply by keeping a reference to a previous input state.
2. **`Parser<Output, Input>`**: The primary functional interface representing a parsing function. It consumes elements of type `Input` from a `ParserInput` and produces a `ParserResult<Output, Input>` or throws a `ParserError`.
3. **`ParserResult<Output, Input>`**: A record that encapsulates the successfully parsed value (`result`) and the remaining input (`remainder`).
4. **`ParserError`**: An exception hierarchy (consisting of `Leaf`, `Branch`, and `Wrapped` errors) representing parse failures.
5. **`ParserCombinator`**: A decorator that wraps any parser to add support for fluent composition and debug logging.

---

## Usage Example

The following example demonstrates how to build and execute a simple parser that parses a comma-separated list of characters (e.g., `a,b,c`).

```java
import java.util.ArrayList;
import java.util.List;

import io.github.sebhajek.pcomb4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) throws ParserError {
        Logger logger = LoggerFactory.getLogger("ParserLogger");
        ParserFactory factory = ParserFactory.withLogger(logger);

        // 1. Define primitive parsers
        Parser<Character, Character> a = factory.literal('a');
        Parser<Character, Character> b = factory.literal('b');
        Parser<Character, Character> c = factory.literal('c');
        Parser<Character, Character> comma = factory.literal(',');

        // 2. Combine them to parse any of the characters 'a', 'b', or 'c'
        ParserCombinator<Character, Character> item = ParserCombinator.withLogger(logger)
                .from(a.or(b, c));

        // 3. Build a CSV parser: one or more items separated by commas
        // (item + comma) zero or more times, followed by a trailing item
        ParserCombinator<List<Character>, Character> csvParser = ParserCombinator.withLogger(logger)
                .from(
                        item.andFirst(comma).zeroOrMore().and(item)
                                .map(sequence -> {
                                    List<Character> list = new ArrayList<>(sequence.first());
                                    list.add(sequence.second());
                                    return list;
                                })
                );

        // 4. Parse an input string
        ParserInput<Character> input = ParserInput.fromString("a,b,c");
        ParserResult<List<Character>, Character> result = csvParser.parse(input);

        // Output: [a, b, c]
        System.out.println(result.result());
    }
}
```
