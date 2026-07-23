package io.github.sebhajek.pcomb4j.parsers.chain;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.slf4j.Logger;

/**
 * Abstract base for parsers that handle left- or right-associative chains of
 * binary operators.
 *
 * <p>Parses a <em>term</em> followed by zero or more {@code (operator term)}
 * pairs, combining them via a user-supplied {@link
 * AssociativeChainParserCombiner}. Two concrete subclasses define the
 * associativity:
 *
 * <ul>
 *   <li>{@link AssociativeChainParserLeft} — left-associative: combines terms
 * as they are parsed (left fold). <li>{@link AssociativeChainParserRight} —
 * right-associative: collects all terms and operators, then folds from the
 * right.
 * </ul>
 *
 * @param <Output> the type of the final folded result
 * @param <OutputInfix> the type produced by the operator parser
 * @param <Input> the type of element consumed from the input
 */
public abstract sealed class AssociativeChainParser<Output, OutputInfix, Input>
  extends       AbstractSourcedParser<Output, Output, Input> permits
                AssociativeChainParserLeft,
          AssociativeChainParserRight {

	private final Parser<OutputInfix, Input> parserInfix;

	private final AssociativeChainParserCombiner<Output, OutputInfix> combiner;

	/**
	 * Creates a new {@code AssociativeChainParser}.
	 *
	 * @param parserSource the term parser; never {@code null}
	 * @param parserInfix the operator parser; never {@code null}
	 * @param combiner the combining function; never {@code null}
	 * @param logger the logger for debug output; never {@code null}
	 */
	public AssociativeChainParser(
	  final Parser<Output, Input> parserSource,
	  final Parser<OutputInfix, Input> parserInfix,
	  final AssociativeChainParserCombiner<Output, OutputInfix> combiner,
	  final Logger                                              logger
	) {
		super(parserSource, logger);
		this.parserInfix = parserInfix;
		this.combiner    = combiner;
	}

	/**
	 * Returns the combiner used to fold terms and operators.
	 *
	 * @return the combiner; never {@code null}
	 */
	protected AssociativeChainParserCombiner<Output, OutputInfix>
	getCombiner() {
		return combiner;
	}

	/**
	 * Returns the parser used to parse the infix operator.
	 *
	 * @return the infix parser; never {@code null}
	 */
	protected Parser<OutputInfix, Input> getParserInfix() {
		return parserInfix;
	}
}