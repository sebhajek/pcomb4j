package io.github.sebhajek.pcomb4j.interfaces;

import io.github.sebhajek.pcomb4j.Parser;

import org.slf4j.Logger;

/**
 * Abstract base class for {@link CombinatorParser} implementations that act as
 * their own parse source (i.e. they <em>are</em> the parser rather than
 * wrapping one).
 *
 * <p>Subclasses implement {@link #parse} directly and inherit the full
 * combinator API from {@link CombinatorParser}.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public abstract class AbstractParser<Output, Input>
  implements CombinatorParser<Output, Input> {

	private final Logger logger;

	/**
	 * Creates a new {@code AbstractParser} with the given logger.
	 *
	 * @param logger the logger used for debug output during parsing; never
	 *   {@code null}
	 */
	public AbstractParser(Logger logger) { this.logger = logger; }

	/**
	 * Returns {@code this}, since a standalone parser is its own delegate.
	 *
	 * @return this parser
	 */
	public Parser<Output, Input> getParser() { return this; }

	/** {@inheritDoc} */
	@Override
	public final Logger getLogger() {
		return logger;
	}
}
