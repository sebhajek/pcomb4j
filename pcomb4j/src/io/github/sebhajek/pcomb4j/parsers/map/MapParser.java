package io.github.sebhajek.pcomb4j.parsers.map;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.function.Function;

/**
 * Abstract base for parsers that delegate to an inner parser and then transform
 * the parsed value.
 *
 * <p>Three concrete subclasses define how the transformation is computed:
 *
 * <ul>
 *   <li>{@link Transform} — applies a {@link Function}.
 *   <li>{@link Pure} — ignores the parsed value and returns a constant.
 *   <li>{@link Cast} — casts the value to a supertype via {@link
 * Class#cast(Object)}.
 * </ul>
 *
 * @param <OutputMapped> the type produced after transformation
 * @param <Output> the type produced by the inner parser
 * @param <Input> the type of element consumed from the input
 */
public abstract class MapParser<OutputMapped, Output, Input>
  extends AbstractSourcedParser<OutputMapped, Output, Input> {

	/**
	 * A {@link MapParser} that applies a {@link Function} to transform the
	 * inner parser's result.
	 *
	 * @param <OutputMapped> the type produced after applying the function
	 * @param <Output> the type produced by the inner parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static class Transform<OutputMapped, Output, Input>
	  extends MapParser<OutputMapped, Output, Input> {

		private final Function<Output, OutputMapped> mapper;

		/**
		 * Creates a new {@code Transform} parser.
		 *
		 * @param parserSource the inner parser; never {@code null}
		 * @param mapper the mapping function; never {@code null}
		 * @param logger the logger for debug output; never {@code null}
		 */
		public Transform(
		  final Parser<Output, Input> parserSource,
		  final Function<Output, OutputMapped> mapper,
		  final Logger                         logger
		) {
			super(parserSource, logger);
			this.mapper = mapper;
		}

		/**
		 * Returns the mapping function.
		 *
		 * @return the mapper; never {@code null}
		 */
		protected Function<Output, OutputMapped> getMapper() { return mapper; }

		/** {@inheritDoc} */
		@Override
		protected OutputMapped getValueMapped(
		  final ParserResult<Output, Input> result
		) {
			final var valueMapped = getMapper().apply(result.result());
			getLogger().debug(
			  "processing `map` parser: {} -> {}", result.result(), valueMapped
			);
			return valueMapped;
		}
	}

	/**
	 * A {@link MapParser} that ignores the inner parser's result and always
	 * returns a constant value.
	 *
	 * @param <OutputMapped> the type of the constant value
	 * @param <Output> the type produced by the inner parser (discarded)
	 * @param <Input> the type of element consumed from the input
	 */
	public static class Pure<OutputMapped, Output, Input>
	  extends MapParser<OutputMapped, Output, Input> {

		private final OutputMapped value;

		/**
		 * Creates a new {@code Pure} parser.
		 *
		 * @param parserSource the inner parser (run for its side-effects on the
		 *   input); never {@code
		 *     null}
		 * @param value the constant value to return; never {@code null} in
		 *   null-marked code
		 * @param logger the logger for debug output; never {@code null}
		 */
		public Pure(
		  final Parser<Output, Input> parserSource,
		  final OutputMapped          value,
		  final Logger                logger
		) {
			super(parserSource, logger);
			this.value = value;
		}

		/**
		 * Returns the constant value.
		 *
		 * @return the value; never {@code null} in null-marked code
		 */
		protected OutputMapped getValue() { return value; }

		/** {@inheritDoc} */
		@Override
		protected OutputMapped getValueMapped(
		  final ParserResult<Output, Input> result
		) {
			getLogger().debug("processing `pure` parser: -> {}", getValue());
			return getValue();
		}
	}

	/**
	 * A {@link MapParser} that casts the inner parser's result to a supertype
	 * via {@link Class#cast(Object)}.
	 *
	 * <p><strong>Note:</strong> This is a workaround for a Java generics
	 * limitation. A {@link ClassCastException} may be thrown at runtime if the
	 * value is incompatible with {@code type}.
	 *
	 * @param <OutputMapped> the supertype to widen to
	 * @param <Output> the type produced by the inner parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static class Cast<OutputMapped, Output, Input>
	  extends MapParser<OutputMapped, Output, Input> {

		private final Class<OutputMapped> type;

		/**
		 * Creates a new {@code Cast} parser.
		 *
		 * @param parserSource the inner parser; never {@code null}
		 * @param type the target type token; never {@code null}
		 * @param logger the logger for debug output; never {@code null}
		 */
		public Cast(
		  final Parser<Output, Input> parserSource,
		  final Class<OutputMapped> type,
		  final Logger              logger
		) {
			super(parserSource, logger);
			this.type = type;
		}

		/**
		 * Returns the target type token.
		 *
		 * @return the type; never {@code null}
		 */
		protected Class<OutputMapped> getType() { return type; }

		/** {@inheritDoc} */
		@Override
		protected OutputMapped getValueMapped(
		  final ParserResult<Output, Input> result
		) {
			getLogger().atDebug().log(() -> {
				return "processing `cast` parser: %s -> %s".formatted(
				  result.result().getClass().getName(), getType().getName()
				);
			});
			return getType().cast(result.result());
		}
	}

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
