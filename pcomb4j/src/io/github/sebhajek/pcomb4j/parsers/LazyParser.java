package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.Optional;

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

	/**
	 * A {@link LazyParser} variant that evaluates the supplier on the first
	 * parse call and caches the result for all subsequent calls.
	 *
	 * <p>This is useful when the inner parser is expensive to construct or when
	 * it should be created exactly once.
	 *
	 * @param <Output> the type of value produced by a successful parse
	 * @param <Input> the type of element consumed from the input
	 */
	public static class Memoized<Output, Input>
	  extends LazyParser<Output, Input> {

		private Optional<Parser<Output, Input>> parser;

		/**
		 * Creates a new {@code Memoized} lazy parser.
		 *
		 * @param supplier a supplier that produces the inner parser on first
		 *   call; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public Memoized(
		  Supplier<Parser<Output, Input>> supplier,
		  Logger                          logger
		) {
			super(supplier, logger);
			this.parser = Optional.empty();
		}

		/**
		 * Returns the inner parser, evaluating the supplier on the first call
		 * and caching the result.
		 *
		 * @return the inner parser; never {@code null}
		 */
		@Override
		public Parser<Output, Input> getParser() {
			if (parser.isEmpty()) { parser = Optional.of(getSupplier().get()); }
			return parser.get();
		}
	}

	private final Supplier<Parser<Output, Input>> supplier;

	/**
	 * Returns the supplier that produces the inner parser.
	 *
	 * @return the supplier; never {@code null}
	 */
	protected Supplier<Parser<Output, Input>> getSupplier() { return supplier; }

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
		return getSupplier().get();
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
