package io.github.sebhajek.pcomb4j.parsers.primitive;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * A {@link Parser} that always fails with a {@link ParserError} obtained from a
 * {@link Supplier}, regardless of the input.
 *
 * <p>This is useful for defining unreachable branches or placeholder parsers
 * that should never be evaluated in a correct grammar.
 *
 * @param <Output> the output type (never produced)
 * @param <Input> the type of element consumed from the input
 */
public class FailureParser<Output, Input>
  extends AbstractParser<Output, Input> {

	private final Supplier<ParserError> supplier;

	/**
	 * Creates a new {@code FailureParser}.
	 *
	 * @param supplier a supplier of the {@link ParserError} to throw on every
	 *   parse attempt; never
	 *     {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public FailureParser(Supplier<ParserError> supplier, Logger logger) {
		super(logger);
		this.supplier = supplier;
	}

	/**
	 * Returns the error supplier.
	 *
	 * @return the supplier; never {@code null}
	 */
	protected Supplier<ParserError> getSupplier() { return supplier; }

	/**
	 * Always throws a {@link ParserError} without consuming any input.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return never returns normally
	 * @throws ParserError always
	 */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().trace("processing `failure` parser");
		throw getSupplier().get();
	}
}
