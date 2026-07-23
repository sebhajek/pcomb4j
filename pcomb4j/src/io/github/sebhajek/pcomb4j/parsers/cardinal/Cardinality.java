package io.github.sebhajek.pcomb4j.parsers.cardinal;

/**
 * Policy controlling how many times an element parser must or may match.
 *
 * @param <Output> the type of value produced by the element parser
 * @param <Input> the type of element consumed from the input
 */
public sealed interface Cardinality<Output, Input> {

	/** Zero or more matches; never fails on its own account. */
	public static record ZeroOrMore<Output, Input>()
	  implements Cardinality<Output, Input> {}

	/** One or more matches; the first match is mandatory. */
	public static record OneOrMore<Output, Input>()
	  implements Cardinality<Output, Input> {}

	/**
	 * Exactly {@code count} matches, no more, no fewer.
	 *
	 * @param count the required number of matches; must be non-negative
	 */
	public static record Exactly<Output, Input>(int count)
	  implements Cardinality<Output, Input> {
		public Exactly {
			if (count < 0) {
				throw new IllegalArgumentException(
				  "count must be non-negative, got: " + count
				);
			}
		}
	}

	/**
	 * Matches until {@code sentinel} is detected; the sentinel itself is not
	 * consumed.
	 *
	 * @param sentinel the condition that ends the sequence; never {@code null}
	 * @param atLeastOne whether at least one non-sentinel match is required
	 */
	public static record Until<Output, Input>(
	  Sentinel<Output, Input> sentinel,
	  boolean                 atLeastOne
	) implements Cardinality<Output, Input> {}
}
