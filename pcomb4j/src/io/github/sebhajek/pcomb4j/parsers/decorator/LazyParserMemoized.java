package io.github.sebhajek.pcomb4j.parsers.decorator;

import io.github.sebhajek.pcomb4j.Parser;

import org.slf4j.Logger;

import java.util.Optional;

import java.util.function.Supplier;

/**
 * A {@link LazyParser} variant that evaluates the supplier on the first parse
 * call and caches the result for all subsequent calls.
 *
 * <p>This is useful when the inner parser is expensive to construct or when it
 * should be created exactly once.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input>  the type of element consumed from the input
 */
public class LazyParserMemoized<Output, Input>
  extends LazyParser<Output, Input> {

	private Optional<Parser<Output, Input>> parser;

	/**
	 * Creates a new {@code LazyParserMemoized}.
	 *
	 * @param supplier a supplier that produces the inner parser on first call;
	 *                 never {@code null}
	 * @param logger   the logger used for debug output; never {@code null}
	 */
	public LazyParserMemoized(
	  Supplier<Parser<Output, Input>> supplier,
	  Logger                          logger
	) {
		super(supplier, logger);
		this.parser = Optional.empty();
	}

	/**
	 * Returns the inner parser, evaluating the supplier on the first call and
	 * caching the result.
	 *
	 * @return the inner parser; never {@code null}
	 */
	@Override
	public Parser<Output, Input> getParser() {
		if (parser.isEmpty()) { parser = Optional.of(getSupplier().get()); }
		return parser.get();
	}
}
