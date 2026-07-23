package io.github.sebhajek.pcomb4j.factories;

import io.github.sebhajek.pcomb4j.parsers.primitive.SetParserAny;
import io.github.sebhajek.pcomb4j.parsers.primitive.SetParserNone;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Factory mix-in for constructing parsers that match input elements against a
 * fixed set of values
 * ({@code anyOf} / {@code noneOf}).
 *
 * <p>Part of the {@link io.github.sebhajek.pcomb4j.factories.AbstractFactory}
 * aggregate so that all methods are available through {@link
 * io.github.sebhajek.pcomb4j.ParserFactory}.
 */
public interface SetFactory extends LoggedFactory {

	/**
	 * Creates a parser that accepts the current input element if it is
	 * contained in the given varargs.
	 *
	 * @param values the set of accepted values (duplicates are ignored); never
	 *   {@code null}
	 * @param <Input> the type of element consumed from the input
	 * @return a new {@link SetParserAny} parser backed by the factory's logger
	 */
	@SuppressWarnings("unchecked")
	public default<Input> SetParserAny<Input> anyOf(final Input... values) {
		var logger = getLogger();
		logger.info("building `anyOf` parser");
		var set = Arrays.stream(values).collect(Collectors.toSet());
		return new SetParserAny<>(set, logger);
	}

	/**
	 * Creates a parser that accepts the current input element if it is
	 * contained in the given collection.
	 *
	 * @param values the collection of accepted values (duplicates are ignored);
	 *   never {@code null}
	 * @param <Input> the type of element consumed from the input
	 * @return a new {@link SetParserAny} parser backed by the factory's logger
	 */
	public default<Input> SetParserAny<Input> anyOf(Collection<Input> values) {
		var logger = getLogger();
		logger.info("building `anyOf` parser");
		var set = values.stream().collect(Collectors.toSet());
		return new SetParserAny<>(set, logger);
	}

	/**
	 * Creates a parser that rejects the current input element if it is
	 * contained in the given varargs (accepting everything else).
	 *
	 * @param values the set of rejected values (duplicates are ignored); never
	 *   {@code null}
	 * @param <Input> the type of element consumed from the input
	 * @return a new {@link SetParserNone} parser backed by the factory's
	 *   logger
	 */
	@SuppressWarnings("unchecked")
	public default<Input> SetParserNone<Input> noneOf(final Input... values) {
		var logger = getLogger();
		logger.info("building `noneOf` parser");
		var set = Arrays.stream(values).collect(Collectors.toSet());
		return new SetParserNone<>(set, logger);
	}

	/**
	 * Creates a parser that rejects the current input element if it is
	 * contained in the given collection (accepting everything else).
	 *
	 * @param values the collection of rejected values (duplicates are ignored);
	 *   never {@code null}
	 * @param <Input> the type of element consumed from the input
	 * @return a new {@link SetParserNone} parser backed by the factory's
	 *   logger
	 */
	public default<Input> SetParserNone<Input> noneOf(
	  Collection<Input> values
	) {
		var logger = getLogger();
		logger.info("building `noneOf` parser");
		var set = values.stream().collect(Collectors.toSet());
		return new SetParserNone<>(set, logger);
	}
}
