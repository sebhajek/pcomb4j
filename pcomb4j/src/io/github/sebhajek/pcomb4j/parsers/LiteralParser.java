package io.github.sebhajek.pcomb4j.parsers;

import java.util.Comparator;
import java.util.Optional;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/**
 * A {@link Parser} that succeeds when the
 * current input element equals a fixed expected value.
 *
 * <p>Equality is tested either via {@link Object#equals(Object)} (the
 * default) or via a custom {@link Comparator} supplied at construction time.
 * On success the matched element is returned; on failure a
 * {@link LiteralNotMatched} error is thrown.
 *
 * @param <Input>  the type of element to match (also used as the output type)
 */
public class LiteralParser<Input> extends AbstractParser<Input, Input> {

	/**
	 * Thrown when the current input element does not match the expected
	 * literal value.
	 */
	public static final class LiteralNotMatched extends ParserError.Leaf {

		/** Creates a new {@code LiteralNotMatched} error. */
		public LiteralNotMatched() {
			super("Literal not matched.");
		}
	}

	private final Input literal;

	private final Optional<Comparator<Input>> comparator;

	/**
	 * Creates a {@code LiteralParser} that uses a custom {@link Comparator}
	 * for equality testing.
	 *
	 * @param literal    the expected value; never {@code null} in null-marked
	 *                   code
	 * @param comparator the comparator to use; if {@code null} falls back to
	 *                   {@link Object#equals(Object)}
	 * @param logger     the logger used for debug output; never {@code null}
	 */
	public LiteralParser(
			Input literal,
			@Nullable Comparator<Input> comparator,
			Logger logger) {
		super(logger);
		this.literal = literal;
		this.comparator = Optional.ofNullable(comparator);
	}

	/**
	 * Creates a {@code LiteralParser} that uses {@link Object#equals(Object)}
	 * for equality testing.
	 *
	 * @param literal the expected value; never {@code null} in null-marked code
	 * @param logger  the logger used for debug output; never {@code null}
	 */
	public LiteralParser(Input literal, Logger logger) {
		super(logger);
		this.literal = literal;
		this.comparator = Optional.empty();
	}

	/**
	 * Compares the current input element against the expected literal value.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} containing the matched element and the
	 *   advanced input
	 * @throws ParserError if the input is empty or the current element does
	 *                     not match the expected literal
	 */
	@Override
	public ParserResult<Input, Input> parse(
			@NonNull ParserInput<Input> parserInput) throws ParserError {
		final var current = parserInput.getCurrent();
		var logger = getLogger();
		var comparison = comparator.isPresent()
				? comparator.orElseThrow().compare(current, literal) == 0
				: literal.equals(current);
		if (comparison) {
			logger.debug("getting `literal`: {} == {}", current, literal);
			return new ParserResult<>(current, parserInput.advance());
		} else {
			logger.debug("getting `literal`: {} != {}", current, literal);
			throw new LiteralNotMatched();
		}
	}
}
