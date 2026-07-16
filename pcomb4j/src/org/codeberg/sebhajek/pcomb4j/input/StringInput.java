package org.codeberg.sebhajek.pcomb4j.input;

import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StringInput implements ParserInput<Character> {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(StringInput.class);

	private final String   input;
	private final Position currentPosition;

	public StringInput(String input, Position currentPosition) {
		this.input           = input;
		this.currentPosition = currentPosition;
	}

	public StringInput(String input) { this(input, Position.getZero()); }

	public final String getInput() { return input; }

	public final Position getCurrentPosition() { return currentPosition; }

	@Override
	public ParserInput<Character> advance() throws ParserError {
		LOGGER.debug("advancing input");
		if (isEmpty()) { throw new ParserInputError.EOF(); }
		return new StringInput(
		  getInput(), getCurrentPosition().advance(getCurrent())
		);
	}

	@Override
	public boolean isEmpty() {
		return getCurrentPosition().getIndex() >= getInput().length();
	}

	@Override
	public Character getCurrent() throws ParserError {
		if (isEmpty()) { throw new ParserInputError.EOF(); }
		final var charAt = getInput().charAt(getCurrentPosition().getIndex());
		LOGGER.debug("getting current character: {}", charAt);
		return charAt;
	}
}
