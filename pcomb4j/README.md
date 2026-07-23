# pcomb4j

______________________________________________________________________

`pcomb4j` is a lightweight, type-safe, and functional **parser combinator library** for modern Java.

Designed for Java 21+ and leveraging features such as records and pattern matching, it provides a simple yet expressive API for constructing parsers through composition.

______________________________________________________________________

## Core Components

The library is built around a few central abstractions:

1. **`ParserInput<Element>`**: An immutable input stream of characters or tokens. Immutability enables backtrackable parsing - a parser can backtrack simply by keeping a reference to a previous input state.
1. **`Parser<Output, Input>`**: The primary functional interface representing a parsing function. It consumes elements of type `Input` from a `ParserInput` and, on success, produces a `ParserResult<Output, Input>` or throws a `ParserError` on failure.
1. **`ParserResult<Output, Input>`**: A record that encapsulates the successfully parsed value (`result`) and the remaining input (`remainder`).
1. **`ParserError`**: An exception hierarchy (consisting of `Leaf`, `Branch`, and `Wrapped` errors) representing parse failures.
1. **`ParserCombinator`**: A decorator that wraps any parser to provide a fluent combinator API for composition, transformation, and debug logging.

______________________________________________________________________

## Usage Examples

### CSV parser

Build and execute a parser that parses a comma-separated list of characters (e.g., `a,b,c`).

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
}
```

### Arithmetic expression parser

Parse simple integer arithmetic with left-associative operators using `chainLeft`:

```java
// Parser for a single digit: 0-9
final var term = factory.<Character>any()
    .filter(Character::isDigit)
    .map(c -> c - '0');

// Parser for sum: 1+2+3 evaluates as ((1+2)+3) = 6
final var sum = term.chainLeft(factory.literal('+'), (a, op, b) -> a + b);

var result = sum.parse(ParserInput.fromString("1+2+3"));
assert result.result() == 6;
```

### Set-based parsing with `anyOf` / `noneOf`

Match characters from a set, or exclude characters using a set:

```java
// Match any hexadecimal digit
final var hexDigit = factory.anyOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');

// Match any character that is NOT a vowel
final var consonant = factory.noneOf('a', 'e', 'i', 'o', 'u');

var r1 = hexDigit.parse(ParserInput.fromString("f"));
assert r1.result() == 'f';

var r2 = consonant.parse(ParserInput.fromString("z"));
assert r2.result() == 'z';
```

### Bracketed expressions with `surround`

Wrap a parser with required or optional delimiters:

```java
final var digit = factory.<Character>any().filter(Character::isDigit).map(c -> c - '0');

// Mandatory parentheses around a digit: (5)
final var paren = digit.surround(factory.literal('('), factory.literal(')'));

// Optional square brackets around a digit: [5] or just 5
final var bracket = digit.surroundOptional(factory.literal('['),
    factory.literal(']'));

var r1 = paren.parse(ParserInput.fromString("(5)"));
assert r1.result() == 5;

var r2 = bracket.parse(ParserInput.fromString("[3]"));
assert r2.result() == 3;

var r3 = bracket.parse(ParserInput.fromString("7"));
assert r3.result() == 7;
```

### Context-dependent parsing with `flatMap`

Use `flatMap` to choose the next parser based on the previous result:

```java
import io.github.sebhajek.pcomb4j.parsers.primitive.LiteralParserNotMatched;

// Parse a leading character, then expect a matching closing bracket
final var matchingBrackets = factory.<Character>any().flatMap(opener -> {
    if (opener == '(') return factory.literal(')');
    if (opener == '[') return factory.literal(']');
    if (opener == '{') return factory.literal('}');
    return factory.alwaysFails(() -> new LiteralParserNotMatched());
});

var r1 = matchingBrackets.parse(ParserInput.fromString("()"));
assert r1.result() == ')';

var r2 = matchingBrackets.parse(ParserInput.fromString("[]"));
assert r2.result() == ']';
```

______________________________________________________________________

## pcomb4j vs funcj.parser

[funcj.parser](https://github.com/typemeta/funcj/tree/master/parser) is a well-established parser combinator library for Java. pcomb4j makes different design trade-offs:

| Aspect | funcj.parser | pcomb4j |
|---|---|---|
| **Java version** | Requires Java 8 | Requires **Java 21+** |
| **Modern Java idioms** | No records, no pattern matching, no sealed classes | Built on **records**, **sealed classes**, and **pattern matching** |
| **Null safety** | No built-in null tracking | Compile-time nullness checking via **`@NullMarked`** + NullAway |
| **Error representation** | Flat failure value | Structured `ParserError` tree (`Leaf` / `Branch` / `Wrapped`) with **traversal** |
| **API design** | Static combinators + `Parser` methods | Fluent **combinator interface** with default methods (`map`, `and`, `or`, `filter`, `flatMap`, etc.) |
| **Input abstraction** | Single `Input<I>` with `Chr` token | Designed for arbitrary token streams (`ParserInput<Element>`) |
| **Result model** | `Result<I, T>` with `Success`/`Failure` | `ParserResult<Output, Input>` record with `Sequence` and `Either` helpers |
| **Dependencies** | Zero dependencies | SLF4J only (logging) |
| **Parsing strategy** | Predictive parsing via FIRST/FOLLOW analysis | **Backtracking** using immutable input snapshots |
| **NullAway integration** | Not available | Built-in Error Prone + NullAway **strict mode** |
| **Cardinality** | Basic repetition combinators | `zeroOrMore`, `oneOrMore`, `exactly(n)`, `until(predicate)`, with optional **separator** and **trailing separator** support |
| **Error labeling** | Position-based failure messages | Label-based errors with `labelError()`, dynamic messages, and **error tree traversal** |

**Key differentiators:**

- **Compile-time nullness checking**: Every package is `@NullMarked` and NullAway enforces null-safety at compile time.
- **Structured error trees**: Instead of a flat failure value, pcomb4j preserves the full failure tree. When an `or` branch fails, both the left and right errors are retained and can be traversed with `ParserError.traverse()`.
- **Backtracking via immutable input**: funcj emphasizes predictive parsing using FIRST/FOLLOW analysis. pcomb4j does not require FIRST/FOLLOW analysis because parsers can freely backtrack by retaining immutable input snapshots.
- **Staged builder for cardinality**: Counting parsers support separators (e.g., comma-separated lists), trailing separators, predicate-based sentinels, and discarding variants - all via a type-safe staged builder.
- **Modern Java**: Records, sealed classes, pattern matching, and fluent interface default methods mean the API feels native to Java 21+.

______________________________________________________________________

## Getting Started

Add the dependency to your build:

**Mill** (already included in this project):

```scala
def ivyDeps = Agg(ivy"io.github.sebhajek::pcomb4j:VERSION")
```

**Maven**:

```xml
<dependency>
	<groupId>io.github.sebhajek</groupId>
	<artifactId>pcomb4j</artifactId>
	<version>VERSION</version>
</dependency>
```

Start by creating a `ParserFactory` with a logger, then compose parsers using the fluent combinator API.
