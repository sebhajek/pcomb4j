package org.codeberg.sebhajek.pcomb4j;

import org.jspecify.annotations.NonNull;

/**
 * The core abstraction of the pcomb4j parser-combinator library.
 *
 * <p>A {@code Parser} consumes elements of type {@code B} from a {@link
 * ParserInput} and, if successful, produces a value of type {@code A} together
 * with a {@link ParserInput} representing the remaining, unconsumed input.
 *
 * <p>Being a {@link FunctionalInterface}, a {@code Parser} can be implemented
 * with a lambda expression or method reference, and can be composed with other
 * parsers via the combinators found in the {@code
 * org.codeberg.sebhajek.pcomb4j.combinators} package (see {@link
 * ParserCombinator}).
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
@FunctionalInterface
public interface Parser<Output, Input> {

	/**
	 * Attempts to parse a value of type {@code A} from the given input.
	 *
	 * @param parserInput the input to parse
	 * @return a {@link ParserResult} containing the parsed value and the
	 *   remaining input
	 * @throws ParserError if the input cannot be parsed
	 */
	ParserResult<Output, Input> parse(@NonNull ParserInput<Input> parserInput)
	  throws ParserError;
}
