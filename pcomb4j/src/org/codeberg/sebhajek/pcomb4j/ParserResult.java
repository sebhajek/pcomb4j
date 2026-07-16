package org.codeberg.sebhajek.pcomb4j;

import org.jspecify.annotations.NonNull;

/**
 * The outcome of a successful {@link Parser#parse(ParserInput)} call.
 *
 * <p>A {@code ParserResult} pairs the value produced by the parse ({@code
 * result}) with the {@link ParserInput} representing whatever input remains to
 * be consumed ({@code remainder}). This remainder is typically fed into
 * subsequent parsers when combinators sequence multiple parses together.
 *
 * @param <Output> the type of the parsed result
 * @param <Input> the type of element in the remaining input
 * @param result the value produced by the parse
 * @param remainder the input left over after the parse
 */
public record ParserResult<Output, Input>(
  @NonNull Output result,
  @NonNull ParserInput<Input> remainder
) {

	/**
	 * Returns a copy of this result with the given value substituted for {@link
	 * #result()}, keeping the same {@link #remainder()}.
	 *
	 * <p>This is useful for combinators (such as {@code map}) that need to
	 * transform the parsed value while leaving the remaining input untouched.
	 *
	 * @param newValue the replacement result value
	 * @param <C> the type of the replacement value
	 * @return a new {@code ParserResult} with {@code newValue} as its result
	 *   and this result's
	 *     remainder
	 */
	public <C> ParserResult<C, Input> with(C newValue) {
		return new ParserResult<>(newValue, remainder());
	}

	public static record Sequence<A, B>(A first, B second) {}
}
