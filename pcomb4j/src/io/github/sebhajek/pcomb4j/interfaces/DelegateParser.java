package io.github.sebhajek.pcomb4j.interfaces;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.combinators.FilterCombinator;
import io.github.sebhajek.pcomb4j.combinators.MapCombinator;

import org.slf4j.Logger;

/**
 * A {@link Parser} that delegates its actual parsing work to an inner {@link
 * Parser} obtained via
 * {@link #getParser()}.
 *
 * <p>This interface is the common base of all combinator interfaces (e.g.
 * {@link MapCombinator},
 * {@link FilterCombinator}). Each combinator's default method creates a new
 * concrete parser wrapping the parser returned by {@link #getParser()}.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public interface DelegateParser<Output, Input> extends Parser<Output, Input> {

	/**
	 * Unwraps a parser if it is a {@link DelegateParser}, returning its inner
	 * delegate; otherwise returns the parser unchanged.
	 *
	 * <p>This is used by combinator default methods to avoid nesting
	 * unnecessary layers of delegation.
	 *
	 * @param parser the parser to unwrap; never {@code null}
	 * @param <Output> the output type
	 * @param <Input> the input type
	 * @return the inner delegate if {@code parser} is a {@code DelegateParser},
	 *   otherwise {@code
	 *     parser} itself
	 */
	public static <Output, Input> Parser<Output, Input> getDelegate(
	  Parser<Output, Input> parser
	) {
		return parser instanceof DelegateParser<Output, Input> delegateParser
		  ? delegateParser
		  : parser;
	}

	/**
	 * Returns the underlying parser to which parsing is delegated.
	 *
	 * @return the inner parser; never {@code null}
	 */
	public Parser<Output, Input> getParser();

	/**
	 * Returns the {@link Logger} used by this parser and its child parsers for
	 * debug output.
	 *
	 * @return the logger; never {@code null}
	 */
	public Logger getLogger();
}
