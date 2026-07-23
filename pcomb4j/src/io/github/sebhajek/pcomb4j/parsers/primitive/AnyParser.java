package io.github.sebhajek.pcomb4j.parsers.primitive;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * A {@link Parser} that matches any single input element unconditionally.
 *
 * <p>{@code AnyParser} always succeeds as long as the input is not empty,
 * consuming and returning the current element. It is the building block for
 * predicates (combined with {@code filter}) and character-class parsers.
 *
 * @param <Input> the type of element to consume (also used as the output type)
 */
public class AnyParser<Input> extends AbstractParser<Input, Input> {

	/**
	 * Creates a new {@code AnyParser} with the given logger.
	 *
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public AnyParser(Logger logger) { super(logger); }

	/**
	 * Consumes and returns the current element of {@code parserInput}.
	 *
	 * @param parserInput the input to consume from; never {@code null}
	 * @return a {@link ParserResult} containing the consumed element and the
	 *   advanced input
	 * @throws ParserError if the input is empty
	 */
	@Override
	public final ParserResult<Input, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		final var current = parserInput.getCurrent();
		getLogger().trace("getting `any`: {}", current);
		return new ParserResult<Input, Input>(current, parserInput.advance());
	}
}
