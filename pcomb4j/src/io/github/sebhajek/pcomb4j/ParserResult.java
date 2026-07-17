package io.github.sebhajek.pcomb4j;

import org.jspecify.annotations.NonNull;

/**
 * The outcome of a successful {@link Parser#parse(ParserInput)} call.
 *
 * <p>A {@code ParserResult} pairs the value produced by the parse
 * ({@link #result()}) with the {@link ParserInput} representing whatever
 * input remains to be consumed ({@link #remainder()}). This remainder is
 * typically fed into subsequent parsers when combinators sequence multiple
 * parses together.
 *
 * @param <Output>    the type of the parsed result
 * @param <Input>     the type of element in the remaining input
 * @param result      the value produced by the parse; never {@code null}
 * @param remainder   the input left over after the parse; never {@code null}
 */
public record ParserResult<Output, Input>(
  @NonNull Output result,
  @NonNull ParserInput<Input> remainder
) {

	/**
	 * Returns a copy of this result with the given value substituted for
	 * {@link #result()}, keeping the same {@link #remainder()}.
	 *
	 * <p>This is useful for combinators (such as {@code map}) that need to
	 * transform the parsed value while leaving the remaining input untouched.
	 *
	 * @param newValue      the replacement result value
	 * @param <OutputNew>   the type of the replacement value
	 * @return a new {@code ParserResult} with {@code newValue} as its result
	 *   and this result's remainder
	 */
	public <OutputNew> ParserResult<OutputNew, Input> with(OutputNew newValue) {
		return new ParserResult<>(newValue, remainder());
	}

	/**
	 * A pair of two parsed values produced by an {@code and} combinator.
	 *
	 * @param <First>   the type of the first element
	 * @param <Second>  the type of the second element
	 * @param first     the value from the left (first) parser
	 * @param second    the value from the right (second) parser
	 */
	public static record Sequence<First, Second>(First first, Second second) {}

	/**
	 * The result type of an {@code orElse} combinator, representing a value
	 * from one of two heterogeneous parsers.
	 *
	 * @param <LeftType>   the output type of the left parser
	 * @param <RightType>  the output type of the right parser
	 */
	public static sealed interface Either<LeftType, RightType> {

		/**
		 * The {@link Either} variant produced when the left parser succeeds.
		 *
		 * @param <LeftType>   the output type of the left parser
		 * @param <RightType>  the output type of the right parser
		 * @param value        the value produced by the left parser
		 */
		public static record Left<LeftType, RightType>(LeftType value)
		  implements Either<LeftType, RightType> {}

		/**
		 * The {@link Either} variant produced when the right parser succeeds
		 * (after the left parser fails).
		 *
		 * @param <LeftType>   the output type of the left parser
		 * @param <RightType>  the output type of the right parser
		 * @param value        the value produced by the right parser
		 */
		public static record Right<LeftType, RightType>(RightType value)
		  implements Either<LeftType, RightType> {}
	}
}
