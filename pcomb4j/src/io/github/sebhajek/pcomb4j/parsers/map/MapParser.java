package io.github.sebhajek.pcomb4j.parsers.map;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * Abstract base for parsers that delegate to an inner parser and then transform
 * the parsed value.
 *
 * <p>Three concrete subclasses define how the transformation is computed:
 *
 * <ul>
 *   <li>{@link MapParserTransform} — applies a {@link Function}.
 *   <li>{@link MapParserPure} — ignores the parsed value and returns a
 *       constant.
 *   <li>{@link MapParserCast} — casts the value to a supertype via
 *       {@link Class#cast(Object)}.
 * </ul>
 *
 * @param <OutputMapped> the type produced after transformation
 * @param <Output> the type produced by the inner parser
 * @param <Input> the type of element consumed from the input
 */
public abstract sealed class MapParser<OutputMapped, Output, Input>
  extends       AbstractSourcedParser<OutputMapped, Output, Input> permits
                MapParserTransform,
          MapParserPure, MapParserCast {

	/**
	 * Creates a new {@code MapParser}.
	 *
	 * @param parserSource the inner parser; never {@code null}
	 * @param logger the logger for debug output; never {@code null}
	 */
	public MapParser(
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
	}

	/** {@inheritDoc} */
	@Override
	public ParserResult<OutputMapped, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		return getResultMapped(parserInput);
	}

	/**
	 * Computes the transformed value from the inner parser's result.
	 *
	 * @param result the inner parser's raw result; never {@code null}
	 * @return the transformed value; never {@code null} in null-marked code
	 */
	protected abstract OutputMapped
	getValueMapped(final ParserResult<Output, Input> result);

	private ParserResult<OutputMapped, Input> getResultMapped(
	  final ParserInput<Input> parserInput
	) throws ParserError {
		final var result      = getResult(parserInput);
		final var valueMapped = getValueMapped(result);
		return result.with(valueMapped);
	}

	private ParserResult<Output, Input> getResult(
	  final ParserInput<Input> parserInput
	) throws ParserError {
		final var result = getParserSource().parse(parserInput);
		return result;
	}
}
