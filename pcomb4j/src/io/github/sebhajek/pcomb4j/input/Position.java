package io.github.sebhajek.pcomb4j.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Immutable value object that tracks a character's position in a source string,
 * used by {@link StringInput} to report where in the input a parse is taking
 * place.
 *
 * <p>A {@code Position} records three coordinates:
 *
 * <ul>
 *   <li>{@link #getIndex()} the zero-based character offset from the start of
 * the string. <li>{@link #getLine()} the one-based line number (increments on
 * every {@code '\n'} character). <li>{@link #getCharacter()} the one-based
 * column within the current line (resets to 1 on every newline).
 * </ul>
 *
 * <p>Use {@link #getZero()} to obtain the initial position at the very start of
 * a string, then call
 * {@link #advance(char)} for each character consumed.
 */
public final class Position {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(Position.class);

	/**
	 * Returns the initial position representing the very start of a string
	 * (index 0, line 1, column 1).
	 *
	 * @return the zero position
	 */
	public static final Position getZero() { return new Position(0, 1, 1); }

	private final int index;
	private final int line;
	private final int character;

	private Position(final int index, final int line, final int character) {
		LOGGER.trace("building new position: {},{},{}", index, line, character);
		this.index     = index;
		this.line      = line;
		this.character = character;
	}

	/**
	 * Returns a string representation of this position in the form {@code
	 * (index,line,column)}.
	 *
	 * @return a human-readable position string
	 */
	@Override
	public final String toString() {
		return "(%d,%d,%d)".formatted(getIndex(), getLine(), getCharacter());
	}

	/**
	 * Returns the zero-based character index from the start of the string.
	 *
	 * @return the absolute character offset
	 */
	public final int getIndex() { return index; }

	/**
	 * Returns the one-based line number.
	 *
	 * @return the current line
	 */
	public final int getLine() { return line; }

	/**
	 * Returns the one-based column number within the current line.
	 *
	 * @return the current column
	 */
	public final int getCharacter() { return character; }

	/**
	 * Returns a new {@code Position} representing the position after consuming
	 * {@code inputCharacter}.
	 *
	 * <p>If {@code inputCharacter} is {@code '\n'}, the line counter is
	 * incremented and the column resets to 1; otherwise only the index and
	 * column advance.
	 *
	 * @param inputCharacter the character that was just consumed
	 * @return the next position
	 */
	public final Position advance(final char inputCharacter) {
		LOGGER.trace("advancing position {} on `{}`", this, inputCharacter);
		return new Position(
		  getIndex() + 1,
		  (inputCharacter == '\n') ? getLine() + 1 : getLine(),
		  (inputCharacter == '\n') ? 1 : getCharacter() + 1
		);
	}
}
