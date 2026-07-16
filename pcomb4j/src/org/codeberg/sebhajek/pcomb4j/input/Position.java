package org.codeberg.sebhajek.pcomb4j.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Position {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(Position.class);

	public static final Position getZero() { return new Position(0, 1, 1); }

	private final int index;
	private final int line;
	private final int character;

	private Position(final int index, final int line, final int character) {
		LOGGER.debug("building new position: {},{},{}", index, line, character);
		this.index     = index;
		this.line      = line;
		this.character = character;
	}

	@Override
	public final String toString() {
		return "(%d,%d,%d)".formatted(getIndex(), getLine(), getCharacter());
	}

	public final int getIndex() { return index; }

	public final int getLine() { return line; }

	public final int getCharacter() { return character; }

	public final Position advance(final char inputCharacter) {
		LOGGER.debug("advancing position {} on `{}`", this, inputCharacter);
		return new Position(
		  getIndex() + 1,
		  (inputCharacter == '\n') ? getLine() + 1 : getLine(),
		  (inputCharacter == '\n') ? 1 : getCharacter() + 1
		);
	}
}
