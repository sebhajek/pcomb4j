package io.github.sebhajek.pcomb4j.input;

import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An immutable {@link ParserInput} backed by a {@link String}.
 *
 * <p>The current position within the string is tracked as a {@link Position}
 * value (index, line, column), which is updated on each call to
 * {@link #advance()} by consuming the character at the current index.
 *
 * <p>Use {@link ParserInput#fromString(String)}
 * as the preferred factory method; this constructor is public only to allow
 * construction from an existing {@link Position} (e.g. when resuming from a
 * known offset).
 */
public final class StringInput implements ParserInput<Character> {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(StringInput.class);

	private final String   input;
	private final Position currentPosition;

	/**
	 * Creates a {@code StringInput} positioned at the given
	 * {@code currentPosition} within {@code input}.
	 *
	 * @param input           the string to parse; never {@code null}
	 * @param currentPosition the position to start from; never {@code null}
	 */
	public StringInput(String input, Position currentPosition) {
		this.input           = input;
		this.currentPosition = currentPosition;
	}

	/**
	 * Creates a {@code StringInput} positioned at the very start of
	 * {@code input} (line 1, column 1).
	 *
	 * @param input the string to parse; never {@code null}
	 */
	public StringInput(String input) { this(input, Position.getZero()); }

	/**
	 * Returns the full underlying string, regardless of the current position.
	 *
	 * @return the raw input string; never {@code null}
	 */
	public final String getInput() { return input; }

	/**
	 * Returns the current position within the string.
	 *
	 * @return the current {@link Position}; never {@code null}
	 */
	public final Position getCurrentPosition() { return currentPosition; }

	/**
	 * {@inheritDoc}
	 *
	 * @throws ParserInputError.EOF if the input is already exhausted
	 */
	@Override
	public ParserInput<Character> advance() throws ParserError {
		LOGGER.debug("advancing input");
		if (isEmpty()) { throw new ParserInputError.EOF(); }
		return new StringInput(
		  getInput(), getCurrentPosition().advance(getCurrent())
		);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return getCurrentPosition().getIndex() >= getInput().length();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws ParserInputError.EOF if the input is exhausted
	 */
	@Override
	public Character getCurrent() throws ParserError {
		if (isEmpty()) { throw new ParserInputError.EOF(); }
		final var charAt = getInput().charAt(getCurrentPosition().getIndex());
		LOGGER.debug("getting current character: {}", charAt);
		return charAt;
	}
}
