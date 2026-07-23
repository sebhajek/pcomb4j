package io.github.sebhajek.pcomb4j.factories;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.parsers.decorator.LazyParser;

import java.util.function.Supplier;

/**
 * Factory mix-in that creates {@link LazyParser} instances.
 *
 * <p>A {@code lazy} parser defers the construction of its inner parser until
 * the first time it is invoked. This breaks circular references that arise in
 * recursive grammar definitions, since a
 * {@link Supplier} can capture an outer variable that is not yet initialised at
 * the point where
 * {@code lazy(…)} is called.
 */
public interface LazyFactory extends LoggedFactory {

	/**
	 * Creates a {@link LazyParser} whose inner parser is obtained from {@code
	 * supplier} on each parse call.
	 *
	 * @param supplier a supplier of the actual parser; never {@code null}
	 * @param <Output> the output type of the inner parser
	 * @param <Input> the type of element consumed from the input
	 * @return a new {@link LazyParser}
	 */
	public default<Output, Input> LazyParser<Output, Input> lazy(
	  final Supplier<Parser<Output, Input>> supplier
	) {
		final var logger = getLogger();
		logger.info("building `lazy` parser");
		return new LazyParser<>(supplier, logger);
	}

	/**
	 * Creates a {@link LazyParser.Memoized} whose inner parser is obtained from
	 * {@code supplier} on the first parse call and cached thereafter.
	 *
	 * @param supplier a supplier of the actual parser; never {@code null}
	 * @param <Output> the output type of the inner parser
	 * @param <Input> the type of element consumed from the input
	 * @return a new {@link LazyParser.Memoized}
	 */
	public default<Output, Input> LazyParser<Output, Input> lazyMemoized(
	  final Supplier<Parser<Output, Input>> supplier
	) {
		final var logger = getLogger();
		logger.info("building `lazy` parser");
		return new LazyParser.Memoized<>(supplier, logger);
	}
}
