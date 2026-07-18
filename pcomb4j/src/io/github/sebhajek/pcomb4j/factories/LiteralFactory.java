package io.github.sebhajek.pcomb4j.factories;

import io.github.sebhajek.pcomb4j.parsers.LiteralParser;

import org.jspecify.annotations.Nullable;

import java.util.Comparator;

/**
 * Factory mix-in that creates {@link LiteralParser} instances.
 *
 * <p>A {@code literal} parser succeeds when the current input element equals
 * the expected value, optionally using a custom {@link Comparator} instead of
 * {@link Object#equals(Object)}.
 */
public interface LiteralFactory extends LoggedFactory {

	/**
	 * Creates a {@link LiteralParser} that matches input elements equal to
	 * {@code value} using {@link Object#equals(Object)}.
	 *
	 * @param value the expected value; must not be {@code null} in null-marked
	 *   code
	 * @param <Input> the type of element to match
	 * @return a new {@link LiteralParser}
	 */
	public default<Input> LiteralParser<Input> literal(final Input value) {
		var logger = getLogger();
		logger.info("building `literal` parser");
		return new LiteralParser<>(value, logger);
	}

	/**
	 * Creates a {@link LiteralParser} that matches input elements equal to
	 * {@code value} using the supplied {@link Comparator}.
	 *
	 * @param value the expected value; must not be {@code null} in null-marked
	 *   code
	 * @param comparator the comparator to use for equality testing; if {@code
	 *   null} falls back to
	 *     {@link Object#equals(Object)}
	 * @param <Input> the type of element to match
	 * @return a new {@link LiteralParser}
	 */
	public default<Input> LiteralParser<Input>
	literal(final Input value, @Nullable final Comparator<Input> comparator) {
		var logger = getLogger();
		logger.info("building `literal` parser");
		return new LiteralParser<>(value, comparator, logger);
	}
}
