package io.github.sebhajek.pcomb4j.parsers.choice;

import io.github.sebhajek.pcomb4j.ParserError;

/**
 * Thrown when both branches of an alternation fail.
 *
 * <p>The errors from both branches are preserved as child errors for
 * diagnostic purposes.
 */
public class NeitherWasSuccessful extends ParserError.Branch {

	/**
	 * Creates a new {@code NeitherWasSuccessful} error.
	 *
	 * @param errorLeft the error from the left branch
	 * @param errorRight the error from the right branch
	 */
	public NeitherWasSuccessful(ParserError errorLeft, ParserError errorRight) {
		super("Neither was successful", errorLeft, errorRight);
	}
}