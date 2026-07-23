package io.github.sebhajek.pcomb4j.parsers.chain;

/**
 * A function that combines a left operand, an operator, and a right operand
 * into a new result.
 *
 * @param <Output> the type of the operands and the result
 * @param <OutputInfix> the type of the operator value
 */
@FunctionalInterface
public interface AssociativeChainParserCombiner<Output, OutputInfix> {

	/**
	 * Combines two operands with an operator into a single result.
	 *
	 * @param left the left operand (the accumulated fold so far)
	 * @param infix the operator value produced by the infix parser
	 * @param right the right operand (the next term or right fold)
	 * @return the combined result
	 */
	Output combine(Output left, OutputInfix infix, Output right);
}