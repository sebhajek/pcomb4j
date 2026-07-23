package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;

import org.slf4j.Logger;

/**
 * Abstract base class for parsers that delegate to a single <em>source</em>
 * {@link Parser} to obtain an intermediate result, then post-process it.
 *
 * <p>Examples of such parsers are {@code MapParser}, {@code FilterParser},
 * {@code FlatMapParser},
 * {@code OptionalParser}, and {@code ErrorParser} — all of which parse via an
 * inner "source" parser and transform or guard the result.
 *
 * @param <Output> the type of value produced by this parser after
 *   post-processing
 * @param <OutputSource> the type of value produced by the inner source parser
 * @param <Input> the type of element consumed from the input
 */
public abstract class AbstractSourcedParser<Output, OutputSource, Input>
  extends AbstractParser<Output, Input> {

	private final Parser<OutputSource, Input> parserSource;

	/**
	 * Creates a new {@code AbstractSourcedParser}.
	 *
	 * @param parserSource the inner parser whose result this parser operates
	 *   on; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public AbstractSourcedParser(
	  final Parser<OutputSource, Input> parserSource,
	  final Logger                      logger
	) {
		super(logger);
		this.parserSource = parserSource;
	}

	/**
	 * Returns the inner source parser.
	 *
	 * @return the source parser; never {@code null}
	 */
	protected Parser<OutputSource, Input> getParserSource() {
		return parserSource;
	}
}
