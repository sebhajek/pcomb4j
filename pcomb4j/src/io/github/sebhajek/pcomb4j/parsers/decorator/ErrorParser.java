package io.github.sebhajek.pcomb4j.parsers.decorator;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.slf4j.Logger;

/**
 * Abstract base for parsers that intercept failures from an inner parser and
 * replace or enrich the thrown {@link ParserError}.
 *
 * <p>Three concrete subclasses are provided:
 *
 * <ul>
 *   <li>{@link ErrorParserLabel} static message label.
 *   <li>{@link ErrorParserMessage} dynamic message computed from input and
 *       error.
 *   <li>{@link ErrorParserSupplied} the replacement error is fully computed by
 *       a factory function.
 * </ul>
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input>  the type of element consumed from the input
 */
public abstract sealed class ErrorParser<Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> permits ErrorParserLabel,
          ErrorParserMessage, ErrorParserSupplied {

	/**
	 * Creates a new {@code ErrorParser}.
	 *
	 * @param parserSource the inner parser to delegate to
	 * @param logger       the logger used for debug output
	 */
	public ErrorParser(
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
	}
}
