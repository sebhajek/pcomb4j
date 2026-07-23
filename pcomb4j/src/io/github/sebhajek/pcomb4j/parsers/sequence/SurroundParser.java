package io.github.sebhajek.pcomb4j.parsers.sequence;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractPairParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * A {@link Parser} that applies a source parser surrounded by optional or
 * mandatory left/right delimiters, discarding the delimiter results.
 *
 * <p>Two modes are available:
 *
 * <ul>
 *   <li>{@link SurroundParserOptional} - delimiters are optional; if they fail
 *       the parse continues without them.
 *   <li>{@link SurroundParserMandatory} - both delimiters are required; if
 *       either fails the entire parse fails.
 * </ul>
 *
 * @param <Output> the type of value produced by the source parser
 * @param <DiscartedLeft> the type of value produced by the left delimiter
 * @param <DiscartedRight> the type of value produced by the right delimiter
 * @param <Input> the type of element consumed from the input
 */
public abstract
  sealed class SurroundParser<Output, DiscartedLeft, DiscartedRight, Input>
  extends AbstractPairParser<Output, DiscartedLeft, DiscartedRight, Input>
            permits SurroundParserOptional, SurroundParserMandatory {

	private final Parser<Output, Input> parserSource;

	/**
	 * Creates a new {@code SurroundParser}.
	 *
	 * @param parserSource the source parser producing the main result; never
	 *   {@code null}
	 * @param parserLeft the left delimiter parser; never {@code null}
	 * @param parserRight the right delimiter parser; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public SurroundParser(
	  final Parser<Output, Input> parserSource,
	  final Parser<DiscartedLeft, Input> parserLeft,
	  final Parser<DiscartedRight, Input> parserRight,
	  final Logger                        logger
	) {
		super(parserLeft, parserRight, logger);
		this.parserSource = parserSource;
	}

	/**
	 * Returns the source parser.
	 *
	 * @return the source parser; never {@code null}
	 */
	protected Parser<Output, Input> getParserSource() { return parserSource; }
}
