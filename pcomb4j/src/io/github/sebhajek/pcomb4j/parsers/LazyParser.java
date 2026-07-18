package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * A {@link Parser} that defers the creation of its inner parser until the first
 * parse call.
 *
 * <p>This is essential for defining mutually-recursive or self-referential
 * grammars. Because a
 * {@link Supplier} can close over a variable that has not yet been initialised
 * at the time {@code LazyParser} is constructed, the actual inner parser can be
 * defined after the {@code LazyParser} is created.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public class LazyParser<Output, Input> extends AbstractParser<Output, Input> {

	private final Supplier<Parser<Output, Input>> supplier;

	/**
	 * Creates a new {@code LazyParser}.
	 *
	 * @param supplier a supplier that produces the inner parser when called;
	 *   never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public LazyParser(Supplier<Parser<Output, Input>> supplier, Logger logger) {
		super(logger);
		this.supplier = supplier;
	}

	/**
	 * Obtains the inner parser from the supplier on each invocation.
	 *
	 * @return the inner parser; never {@code null}
	 */
	@Override
	public Parser<Output, Input> getParser() {
		return this.supplier.get();
	}

	/**
	 * Evaluates the inner parser (via the supplier) and delegates parsing to
	 * it.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return the result produced by the inner parser
	 * @throws ParserError if the inner parser fails
	 */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().info("evaluating `lazy` parser");
		return this.getParser().parse(parserInput);
	}
}
