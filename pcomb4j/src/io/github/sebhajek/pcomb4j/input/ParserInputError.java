package io.github.sebhajek.pcomb4j.input;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;

/**
 * Sealed hierarchy of errors produced when reading from a {@link ParserInput}
 * fails.
 *
 * <p>Two concrete subtypes are defined:
 *
 * <ul>
 *   <li>{@link EOF} raised when the end of the input has been reached and no
 * more elements are available. <li>{@link CouldNotRead} raised when the current
 * element exists but could not be read for an implementation-specific reason.
 * </ul>
 */
public abstract sealed class ParserInputError extends ParserError.Leaf {

	/**
	 * Creates a new {@code ParserInputError} with the given message.
	 *
	 * @param message a human-readable description of the read failure
	 */
	public ParserInputError(String message) { super(message); }

	/**
	 * Signals that the end of the input has been reached and no more elements
	 * are available to consume.
	 */
	public static final class EOF extends ParserInputError {

		/** Creates a new {@code EOF} error. */
		public EOF() { super("End of file."); }
	}

	/**
	 * Signals that an element is logically present in the input but could not
	 * be read successfully.
	 */
	public static final class CouldNotRead extends ParserInputError {

		/** Creates a new {@code CouldNotRead} error. */
		public CouldNotRead() { super("Could not read."); }
	}
}
