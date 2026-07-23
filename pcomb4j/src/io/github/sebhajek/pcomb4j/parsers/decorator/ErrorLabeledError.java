package io.github.sebhajek.pcomb4j.parsers.decorator;

import io.github.sebhajek.pcomb4j.ParserError;

/**
 * A {@link ParserError.Wrapped} that carries a contextual label applied by one
 * of the {@link ErrorParser} subclasses.
 */
public class ErrorLabeledError extends ParserError.Wrapped {

	/**
	 * Creates a new {@code ErrorLabeledError}.
	 *
	 * @param message    the contextual label
	 * @param errorInner the underlying error
	 */
	public ErrorLabeledError(
	  final String      message,
	  final ParserError errorInner
	) {
		super(message, errorInner);
	}
}
