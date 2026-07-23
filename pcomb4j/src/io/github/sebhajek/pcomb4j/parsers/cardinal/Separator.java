package io.github.sebhajek.pcomb4j.parsers.cardinal;

import io.github.sebhajek.pcomb4j.Parser;

/**
 * Policy controlling what, if anything, must appear between successive
 * elements.
 *
 * @param <OutputSeparator> the type of value produced by the separator parser
 * @param <Input> the type of element consumed from the input
 */
public sealed interface Separator<OutputSeparator, Input> {

	/** No separator: elements are matched back-to-back. */
	public static record None<OutputSeparator, Input>()
	  implements Separator<OutputSeparator, Input> {}

	/**
	 * A separator parser tried between elements.
	 *
	 * @param parser the separator parser; never {@code null}
	 * @param trailingAccepted whether a separator with no following element is
	 *   accepted at the end of
	 *     the sequence, rather than propagated as an error
	 */
	public static record Between<OutputSeparator, Input>(
	  Parser<OutputSeparator, Input> parser,
	  boolean                        trailingAccepted
	) implements Separator<OutputSeparator, Input> {}
}
