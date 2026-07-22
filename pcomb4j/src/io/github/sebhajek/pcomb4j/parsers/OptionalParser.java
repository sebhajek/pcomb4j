package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.Optional;

/**
 * A {@link Parser} that wraps another parser's result in an {@link Optional},
 * never failing.
 *
 * <p>If the inner parser succeeds, its result is wrapped in {@link
 * Optional#of(Object)}. If the inner parser throws a {@link ParserError}, the
 * error is silently swallowed and {@link Optional#empty()} is returned —
 * leaving the input unchanged.
 *
 * @param <Output> the type of value produced by the inner parser
 * @param <Input> the type of element consumed from the input
 */
public class OptionalParser<Output, Input>
  extends AbstractSourcedParser<Optional<Output>, Output, Input> {

	/**
	 * A {@link Parser} that falls back to a default value when the inner parser
	 * fails, never failing.
	 *
	 * <p>If the inner parser succeeds, its result is returned directly. If the
	 * inner parser throws a
	 * {@link ParserError}, the error is silently swallowed and {@code
	 * defaultValue} is returned instead — leaving the input unchanged.
	 *
	 * @param <Output> the type of value produced by the inner parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static class Default<Output, Input>
	  extends AbstractSourcedParser<Output, Output, Input> {
		private final Output defaultValue;

		/**
		 * Creates a new {@code Default} parser.
		 *
		 * @param parserSource the inner parser to try; never {@code null}
		 * @param defaultValue the value to return when the inner parser fails
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public Default(
		  final Parser<Output, Input> parserSource,
		  final Output                defaultValue,
		  final Logger                logger
		) {
			super(parserSource, logger);
			this.defaultValue = defaultValue;
		}

		/**
		 * Tries the inner parser; on success returns the result directly, on
		 * failure returns {@code defaultValue} without consuming any input.
		 *
		 * @param parserInput the input to parse; never {@code null}
		 * @return a {@link ParserResult} whose value is always non-null
		 * @throws ParserError never — this parser always succeeds
		 */
		@Override
		public ParserResult<Output, Input> parse(
		  @NonNull final ParserInput<Input> parserInput
		) throws ParserError {
			final var logger = getLogger();
			logger.debug("processing `optionalDefault` parser");
			try {
				final var result = getParserSource().parse(parserInput);
				logger.trace(
				  "`optionalDefault` parser succeeded: {}", result.result()
				);
				return result;
			} catch (final ParserError err) {
				logger.trace("`optionalDefault` parser failed");
				return new ParserResult<>(getDefaultValue(), parserInput);
			}
		}

		/**
		 * Returns the default value used when the inner parser fails.
		 *
		 * @return the default value
		 */
		protected Output getDefaultValue() { return defaultValue; }
	}

	/**
	 * Creates a new {@code OptionalParser}.
	 *
	 * @param parserSource the inner parser to try; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public OptionalParser(
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
	}

	/**
	 * Tries the inner parser; on success wraps the result in {@link
	 * Optional#of(Object)}, on failure returns {@link Optional#empty()} without
	 * consuming any input.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} whose value is always non-null
	 * @throws ParserError never — this parser always succeeds
	 */
	@Override
	public ParserResult<Optional<Output>, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `optional` parser");
		try {
			final var result = getParserSource().parse(parserInput);
			logger.trace("`optional` parser succeeded: {}", result.result());
			return result.with(Optional.of(result.result()));
		} catch (final ParserError err) {
			logger.trace("`optional` parser failed");
			return new ParserResult<>(Optional.empty(), parserInput);
		}
	}
}
