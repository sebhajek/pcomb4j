package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.combinators.AndCombinator;
import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.ParserResult.Sequence;
import io.github.sebhajek.pcomb4j.interfaces.AbstractPairParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

/**
 * A {@link Parser} that sequences two parsers and collects both of their
 * results as a {@link Sequence}.
 *
 * <p>The left parser is applied first; if it succeeds its remainder is passed
 * to the right parser. Both results are then combined into a
 * {@link Sequence}{@code <OutputLeft, OutputRight>}. If either parser fails
 * the error is propagated immediately.
 *
 * @param <OutputLeft>   the type of value produced by the left parser
 * @param <OutputRight>  the type of value produced by the right parser
 * @param <Input>        the type of element consumed from the input
 */
public class AndParser<OutputLeft, OutputRight, Input>
  extends AbstractPairParser<
    ParserResult.Sequence<OutputLeft, OutputRight>,
    OutputLeft,
    OutputRight,
    Input> {

	/**
	 * Creates a new {@code AndParser}.
	 *
	 * @param parserLeft   the first parser to apply; never {@code null}
	 * @param parserRight  the second parser to apply; never {@code null}
	 * @param logger       the logger used for debug output; never {@code null}
	 */
	public AndParser(
	  final Parser<OutputLeft, Input> parserLeft,
	  final Parser<OutputRight, Input> parserRight,
	  final Logger                     logger
	) {
		super(parserLeft, parserRight, logger);
	}

	/**
	 * Applies the left parser, then the right parser on the remaining input,
	 * and combines both results into a {@link Sequence}.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} whose result is a
	 *         {@link Sequence}{@code <OutputLeft, OutputRight>}
	 * @throws ParserError if either parser fails
	 */
	@Override
	public ParserResult<Sequence<OutputLeft, OutputRight>, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().info("processing `and` parser");
		final var resultLeft  = getResultLeft(parserInput);
		final var resultRight = getResultRight(resultLeft);
		return getResult(resultLeft, resultRight);
	}

	private final ParserResult<Sequence<OutputLeft, OutputRight>, Input>
	              getResult(
	                final ParserResult<OutputLeft, Input> resultLeft,
	                final ParserResult<OutputRight, Input> resultRight
	              ) {
		return new ParserResult<>(
		  new ParserResult.Sequence<>(
		    resultLeft.result(), resultRight.result()
		  ),
		  resultRight.remainder()
		);
	}

	private final ParserResult<OutputRight, Input> getResultRight(
	  final ParserResult<OutputLeft, Input> resultLeft
	) throws ParserError {
		final var resultRight = getParserRight().parse(resultLeft.remainder());
		getLogger().debug(
		  "second of `and` parser succeeded: {}", resultRight.result()
		);
		return resultRight;
	}

	private final ParserResult<OutputLeft, Input> getResultLeft(
	  final ParserInput<Input> parserInput
	) throws ParserError {
		final var resultLeft = getParserLeft().parse(parserInput);
		getLogger().debug(
		  "first of `and` parser succeeded: {}", resultLeft.result()
		);
		return resultLeft;
	}

	/**
	 * Returns the second element of {@code pair}, discarding the first.
	 *
	 * <p>Used by {@link
	 * AndCombinator#andSecond(Parser)}
	 * to keep only the right result.
	 *
	 * @param pair the sequence to extract from; never {@code null}
	 * @return the right value
	 */
	public final OutputRight
	discardFirst(final ParserResult.Sequence<OutputLeft, OutputRight> pair) {
		getLogger().debug(
		  "discarding the second part of `and` sequence: {}", pair
		);
		return pair.second();
	}

	/**
	 * Returns the first element of {@code pair}, discarding the second.
	 *
	 * <p>Used by {@link
	 * AndCombinator#andFirst(Parser)}
	 * to keep only the left result.
	 *
	 * @param pair the sequence to extract from; never {@code null}
	 * @return the left value
	 */
	public final OutputLeft
	discardSecond(final Sequence<OutputLeft, OutputRight> pair) {
		getLogger().debug(
		  "discarding the second part of `and` sequence: {}", pair
		);
		return pair.first();
	}
}
