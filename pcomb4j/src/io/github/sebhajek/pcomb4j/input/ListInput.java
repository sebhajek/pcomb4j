package io.github.sebhajek.pcomb4j.input;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * An immutable {@link ParserInput} backed by a {@link List}.
 *
 * <p>Elements are addressed by an integer cursor that starts at 0 and advances
 * by 1 with each call to {@link #advance()}. The underlying list is never
 * mutated; backtracking is achieved by retaining a reference to an earlier
 * {@code ListInput} instance.
 *
 * @param <A> the type of element in the list
 */
public class ListInput<A> implements ParserInput<A> {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(ListInput.class);

	private final List<A> input;
	private final int     currentPosition;

	/**
	 * Creates a {@code ListInput} positioned at the first element of {@code
	 * input}.
	 *
	 * @param input the list to parse; never {@code null}
	 */
	public ListInput(final List<A> input) { this(0, input); }

	private ListInput(final int currentPosition, final List<A> input) {
		LOGGER.info("building `ListInput`");
		this.currentPosition = currentPosition;
		this.input           = input;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws ParserInputError.EOF if the input is already exhausted
	 */
	@Override
	public ParserInput<A> advance() throws ParserError {
		if (isEmpty()) { throw new ParserInputError.EOF(); }
		return new ListInput<>(currentPosition + 1, input);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return currentPosition >= input.size();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws ParserInputError.EOF if the input is exhausted
	 */
	@Override
	public A getCurrent() throws ParserError {
		if (isEmpty()) { throw new ParserInputError.EOF(); }
		final var element = input.get(currentPosition);
		return element;
	}
}
