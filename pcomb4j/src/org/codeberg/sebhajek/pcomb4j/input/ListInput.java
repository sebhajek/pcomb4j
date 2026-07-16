package org.codeberg.sebhajek.pcomb4j.input;

import java.util.List;

import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;

public class ListInput<A> implements ParserInput<A> {

	private final List<A> input;
	private final int     currentPosition;

	public ListInput(final List<A> input) { this(0, input); }

	private ListInput(final int currentPosition, final List<A> input) {
		this.currentPosition = currentPosition;
		this.input           = input;
	}

	@Override
	public ParserInput<A> advance() throws ParserError {
		if (isEmpty()) { throw new ParserInputError.EOF(); }
		return new ListInput<>(currentPosition + 1, input);
	}

	@Override
	public boolean isEmpty() {
		return currentPosition >= input.size();
	}

	@Override
	public A getCurrent() throws ParserError {
		if (isEmpty()) { throw new ParserInputError.EOF(); }
		final var element = input.get(currentPosition);
		return element;
	}
}
