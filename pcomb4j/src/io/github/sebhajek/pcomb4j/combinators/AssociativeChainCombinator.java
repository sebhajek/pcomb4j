package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.parsers.CombinatorParser;
import io.github.sebhajek.pcomb4j.parsers.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.chain.AssociativeChainParserCombiner;
import io.github.sebhajek.pcomb4j.parsers.chain.AssociativeChainParserLeft;
import io.github.sebhajek.pcomb4j.parsers.chain.AssociativeChainParserRight;

/**
 * Combinator that parses a left-associative or right-associative chain of
 * binary-operator expressions.
 *
 * <p>Given a <em>term</em> parser and an <em>operator</em> parser, {@code
 * chainLeft} parses {@code term (operator term)*} and folds the sequence left,
 * while {@code chainRight} folds it right.
 *
 * <p>Example -- parsing a sum of digits with {@code +} (left fold):
 *
 * <pre>{@code
 * Parser<Integer, Character> digit = /* ... *\/;
 * Parser<Integer, Character> sum =
 *     digit.chainLeft(factory.literal('+'), (a, op, b) -> a + b);
 * // "1+2+3" -> 6  parsed as ((1+2)+3)
 * }</pre>
 *
 * @param <Output> the type of value produced by each term and by the result
 * @param <Input> the type of element consumed from the input
 */
public interface AssociativeChainCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that left-fold-associates a chain of binary operators.
	 *
	 * <p>The resulting parser first parses a single term, then repeatedly
	 * parses an operator followed by another term. Each result is combined
	 * left-to-right via {@code combiner}, making the parse left-associative:
	 * {@code a op b op c} is evaluated as {@code ((a op b) op c)}.
	 *
	 * @param parserInfix the parser for the infix operator; never {@code null}
	 * @param combiner the function that combines a left operand, an operator,
	 *   and a right operand into a new result; never {@code null}
	 * @param <OutputInfix> the type of value produced by the operator parser
	 * @return a new {@link CombinatorParser} wrapping this parser
	 */
	@SuppressWarnings("unchecked")
	public default<OutputInfix> CombinatorParser<Output, Input> chainLeft(
	  final Parser<OutputInfix, Input> parserInfix,
	  final AssociativeChainParserCombiner<Output, OutputInfix> combiner
	) {
		final var logger = getLogger();
		logger.info("building `chainLeft` parser");
		return new AssociativeChainParserLeft<Output, OutputInfix, Input>(
		  DelegateParser.getDelegate(getParser()), parserInfix, combiner, logger
		);
	}

	/**
	 * Creates a parser that right-fold-associates a chain of binary operators.
	 *
	 * <p>The resulting parser first parses a single term, then tries to parse
	 * an operator. If an operator is found, the remainder is parsed recursively
	 * as another right-associative chain. Results are combined right-to-left:
	 * {@code a op b op c} is evaluated as {@code a op (b op c)}.
	 *
	 * @param parserInfix the parser for the infix operator; never {@code null}
	 * @param combiner the function that combines a left operand, an operator,
	 *   and a right operand into a new result; never {@code null}
	 * @param <OutputInfix> the type of value produced by the operator parser
	 * @return a new {@link CombinatorParser} wrapping this parser
	 */
	@SuppressWarnings("unchecked")
	public default<OutputInfix> CombinatorParser<Output, Input> chainRight(
	  final Parser<OutputInfix, Input> parserInfix,
	  final AssociativeChainParserCombiner<Output, OutputInfix> combiner
	) {
		final var logger = getLogger();
		logger.info("building `chainRight` parser");
		return new AssociativeChainParserRight<Output, OutputInfix, Input>(
		  DelegateParser.getDelegate(getParser()), parserInfix, combiner, logger
		);
	}
}