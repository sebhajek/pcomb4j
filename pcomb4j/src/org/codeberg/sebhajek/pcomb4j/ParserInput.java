package org.codeberg.sebhajek.pcomb4j;

import java.util.List;

import org.codeberg.sebhajek.pcomb4j.input.ListInput;
import org.codeberg.sebhajek.pcomb4j.input.StringInput;
import org.jspecify.annotations.NonNull;

/**
 * Represents the (immutable) input stream consumed by a {@link Parser}.
 *
 * <p>A {@code ParserInput} exposes the current element via {@link
 * #getCurrent()} and yields a new
 * {@code ParserInput} representing the remaining elements via {@link
 * #advance()}, without mutating the original instance. This allows parsers to
 * backtrack simply by holding on to an earlier {@code ParserInput}.
 *
 * <p>Convenience factory methods are provided for the two built-in input
 * sources: {@link #fromString(String)} for parsing character streams and {@link
 * #fromList(List)} for parsing arbitrary lists of elements.
 *
 * @param <Element> the type of element produced by this input
 */
public interface ParserInput<Element> {

	/**
	 * Creates a {@link ParserInput} over the characters of the given string.
	 *
	 * @param input the string to parse
	 * @return a new {@code ParserInput} backed by {@code input}, starting at
	 *   its first character
	 */
	public static ParserInput<Character> fromString(
	  @NonNull final String input
	) {
		return new StringInput(input);
	}

	/**
	 * Creates a {@link ParserInput} over the elements of the given list.
	 *
	 * @param input the list to parse
	 * @param <Element> the type of element in the list
	 * @return a new {@code ParserInput} backed by {@code input}, starting at
	 *   its first element
	 */
	public static <Element> ParserInput<Element> fromList(
	  @NonNull final List<Element> input
	) {
		return new ListInput<>(input);
	}

	/**
	 * Returns a new {@code ParserInput} representing the input that remains
	 * after consuming the current element.
	 *
	 * @return the remaining input, with the current element consumed
	 * @throws ParserError if there is no current element to advance past (i.e.
	 *   the input is empty)
	 */
	@NonNull
	ParserInput<Element> advance() throws ParserError;

	/**
	 * Indicates whether there are any elements left to consume.
	 *
	 * @return {@code true} if this input has no more elements, {@code false}
	 *   otherwise
	 */
	public boolean isEmpty();

	/**
	 * Returns the element at the current position of this input, without
	 * consuming it.
	 *
	 * @return the current element
	 * @throws ParserError if the input is empty and has no current element
	 */
	@NonNull
	public Element getCurrent() throws ParserError;
}
