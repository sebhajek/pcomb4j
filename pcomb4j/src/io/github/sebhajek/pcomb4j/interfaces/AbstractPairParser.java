package io.github.sebhajek.pcomb4j.interfaces;

import io.github.sebhajek.pcomb4j.Parser;
import org.slf4j.Logger;

/**
 * Abstract base class for parsers that combine a <em>left</em> and a
 * <em>right</em> {@link Parser}, each producing its own output type.
 *
 * <p>This class is the common foundation for {@code AndParser} and
 * {@code OrParser}, both of which operate on two independently typed child
 * parsers.
 *
 * @param <Output>        the type of value produced by this parser after
 *                        combining both sub-results
 * @param <OutputLeft>    the type of value produced by the left parser
 * @param <OutputRight>   the type of value produced by the right parser
 * @param <Input>         the type of element consumed from the input
 */
public abstract class AbstractPairParser<Output, OutputLeft, OutputRight, Input>
		extends AbstractParser<Output, Input> {

	private final Parser<OutputLeft, Input> parserLeft;

	private final Parser<OutputRight, Input> parserRight;

	/**
	 * Creates a new {@code AbstractPairParser}.
	 *
	 * @param parserLeft  the left (first-tried) parser; never {@code null}
	 * @param parserRight the right (second) parser; never {@code null}
	 * @param logger      the logger used for debug output; never {@code null}
	 */
	public AbstractPairParser(
			final Parser<OutputLeft, Input> parserLeft,
			final Parser<OutputRight, Input> parserRight,
			final Logger logger) {
		super(logger);
		this.parserLeft = parserLeft;
		this.parserRight = parserRight;
	}

	/**
	 * Returns the left (first-tried) parser.
	 *
	 * @return the left parser; never {@code null}
	 */
	protected Parser<OutputLeft, Input> getParserLeft() {
		return parserLeft;
	}

	/**
	 * Returns the right (second) parser.
	 *
	 * @return the right parser; never {@code null}
	 */
	protected Parser<OutputRight, Input> getParserRight() {
		return parserRight;
	}
}
