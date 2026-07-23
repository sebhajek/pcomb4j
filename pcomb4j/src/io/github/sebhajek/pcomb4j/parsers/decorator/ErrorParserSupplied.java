package io.github.sebhajek.pcomb4j.parsers.decorator;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.function.BiFunction;

/**
 * Replaces the error using a factory function.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input>  the type of element consumed from the input
 */
public final class ErrorParserSupplied<Output, Input>
  extends ErrorParser<Output, Input> {

	private final BiFunction<ParserInput<Input>, ParserError, ParserError>
	              errorFactory;

	/**
	 * Creates a new {@code ErrorParserSupplied}.
	 *
	 * @param errorFactory function receiving input + original error, returning
	 *                     the replacement error
	 * @param parserSource inner parser to delegate to
	 * @param logger       logger for debug output
	 */
	public ErrorParserSupplied(
	  BiFunction<ParserInput<Input>, ParserError, ParserError> errorFactory,
	  Parser<Output, Input>                                    parserSource,
	  Logger                                                   logger
	) {
		super(parserSource, logger);
		this.errorFactory = errorFactory;
	}

	/** {@inheritDoc} */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().debug("processing `error` (supplied) parser");
		try {
			return getParserSource().parse(parserInput);
		} catch (final ParserError errorInner) {
			throw getErrorFactory().apply(parserInput, errorInner);
		}
	}

	/**
	 * Returns the error factory function.
	 *
	 * @return the error factory; never {@code null}
	 */
	BiFunction<ParserInput<Input>, ParserError, ParserError> getErrorFactory() {
		return errorFactory;
	}
}
