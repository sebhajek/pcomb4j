package io.github.sebhajek.pcomb4j.parsers.cardinal;

import io.github.sebhajek.pcomb4j.Parser;

import java.util.function.Predicate;

/**
 * A condition that signals the end of a repeated parse, without being consumed
 * as an element itself.
 *
 * @param <Output> the type of value produced by the element parser
 * @param <Input> the type of element consumed from the input
 */
public sealed interface Sentinel<Output, Input> {

	/**
	 * A sentinel based on a {@link Predicate} applied to each parsed element.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <Input> the type of element consumed from the input
	 * @param predicate the predicate that identifies the sentinel element;
	 *   never {@code null}
	 */
	public static record PredicateBased<Output, Input>(
	  Predicate<Output> predicate
	) implements Sentinel<Output, Input> {}

	/**
	 * A sentinel based on a {@link Parser} tried against the input before each
	 * element parse.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <Input> the type of element consumed from the input
	 * @param parser the parser that identifies the sentinel; never {@code null}
	 */
	public static record ParserBased<Output, Input>(
	  Parser<Output, Input> parser
	) implements Sentinel<Output, Input> {}
}
