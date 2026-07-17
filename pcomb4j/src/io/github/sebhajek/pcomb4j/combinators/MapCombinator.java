package io.github.sebhajek.pcomb4j.combinators;

import java.util.function.Function;

import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.MapParser;

/**
 * Combinator that transforms the value produced by this parser.
 *
 * <p>Three variants are provided:
 *
 * <ul>
 *   <li>{@link #map(Function)} — applies an arbitrary mapping function.
 *   <li>{@link #pure(Object)} — ignores the parsed value and always returns a
 * fixed constant. <li>{@link #cast(Class)} — unsafely casts the result to a
 * supertype (useful where Java's type system cannot express the subtype
 * relationship).
 * </ul>
 *
 * @param <Output> the type of value produced by this (source) parser
 * @param <Input> the type of element consumed from the input
 */
public interface MapCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that always produces {@code value} regardless of what
	 * this parser returns (as long as it succeeds).
	 *
	 * @param value the constant value to return on success; never {@code null}
	 * @param <OutputMapped> the type of the constant value
	 * @return a new {@link MapParser.Pure}
	 */
	public default<OutputMapped> MapParser.Pure<OutputMapped, Output, Input>
	                             pure(final OutputMapped value) {
		final var logger = getLogger();
		logger.debug("building `pure` parser");
		return new MapParser.Pure<>(getParser(), value, logger);
	}

	/**
	 * Creates a parser that applies {@code mapper} to the value produced by
	 * this parser.
	 *
	 * @param mapper a function transforming {@code Output} to {@code
	 *   OutputMapped}; never {@code
	 *     null}
	 * @param <OutputMapped> the type of the transformed value
	 * @return a new {@link MapParser.Transform}
	 */
	public default<OutputMapped> MapParser
	  .Transform<OutputMapped, Output, Input>
	  map(final Function<Output, OutputMapped> mapper) {
		final var logger = getLogger();
		logger.debug("building `map` parser");
		return new MapParser.Transform<>(getParser(), mapper, logger);
	}

	/**
	 * Creates a parser that casts the value produced by this parser to {@code
	 * type}.
	 *
	 * <p><strong>Note:</strong> This should be {@code Output extends
	 * OutputMapped}, but Java cannot express that constraint with wildcards
	 * here, so an explicit cast is used instead. A {@link ClassCastException}
	 * will be thrown at runtime if the value is incompatible.
	 *
	 * @param type the target type; never {@code null}
	 * @param <OutputMapped> the supertype to widen to
	 * @return a new {@link MapParser.Cast}
	 */
	public default<OutputMapped> MapParser.Cast<OutputMapped, Output, Input>
	                             cast(final Class<OutputMapped> type) {
		final var logger = getLogger();
		logger.debug("building `cast` parser to {}", type.getName());
		return new MapParser.Cast<>(getParser(), type, logger);
	}
}
