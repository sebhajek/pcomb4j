package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractPairParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

/**
 * Abstract base for parsers that implement ordered choice (alternation).
 *
 * <p>The left parser is tried first; if it fails the right parser is tried on
 * the <em>same</em> (unchanged) input. If both fail a {@link
 * NeitherWasSuccessful} error — containing both child errors — is thrown.
 *
 * <p>Two concrete subclasses are provided:
 *
 * <ul>
 *   <li>{@link Or} — both branches must produce the same output type; the
 * result is just {@code Output}. <li>{@link Either} — the branches may produce
 * different types; the result is a {@link ParserResult.Either}.
 * </ul>
 *
 * @param <Output> the type of value produced by this parser
 * @param <OutputLeft> the type produced by the left parser
 * @param <OutputRight> the type produced by the right parser
 * @param <Input> the type of element consumed from the input
 */
public abstract class OrParser<Output, OutputLeft, OutputRight, Input>
  extends AbstractPairParser<Output, OutputLeft, OutputRight, Input> {

	/**
	 * Thrown when both branches of an alternation fail.
	 *
	 * <p>The errors from both branches are preserved as child errors for
	 * diagnostic purposes.
	 */
	public static class NeitherWasSuccessful extends ParserError.Branch {
		/**
		 * Creates a new {@code NeitherWasSuccessful} error.
		 *
		 * @param errorLeft the error from the left branch
		 * @param errorRight the error from the right branch
		 */
		public NeitherWasSuccessful(
		  ParserError errorLeft,
		  ParserError errorRight
		) {
			super("Neither was successful", errorLeft, errorRight);
		}
	}

	/**
	 * An alternation parser where both branches produce the same output type.
	 *
	 * <p>If the left parser succeeds its result is returned. If it fails the
	 * right parser is tried. If both fail a {@link NeitherWasSuccessful} error
	 * is thrown.
	 *
	 * @param <Output> the shared output type
	 * @param <Input> the type of element consumed from the input
	 */
	public static class Or<Output, Input>
	  extends OrParser<Output, Output, Output, Input> {

		/**
		 * Creates a new {@code Or} parser.
		 *
		 * @param parserLeft the first (preferred) parser; never {@code null}
		 * @param parserRight the fallback parser; never {@code null}
		 * @param logger the logger for debug output; never {@code null}
		 */
		public Or(
		  final Parser<Output, Input> parserLeft,
		  final Parser<Output, Input> parserRight,
		  final Logger                logger
		) {
			super(parserLeft, parserRight, logger);
		}

		/**
		 * Tries the left parser; on failure tries the right parser on the same
		 * input.
		 *
		 * @param input the input to parse; never {@code null}
		 * @return the result of whichever branch succeeded first
		 * @throws NeitherWasSuccessful if both branches fail
		 */
		@Override
		public ParserResult<Output, Input> parse(
		  @NonNull final ParserInput<Input> input
		) throws ParserError {
			getLogger().debug("processing `or` parser");
			try {
				return getResultLeft(input);
			} catch (final ParserError errorLeft) {
				try {
					return getResultRight(input);
				} catch (final ParserError errorRight) {
					throw new NeitherWasSuccessful(errorLeft, errorRight);
				}
			}
		}

		private ParserResult<Output, Input> getResultRight(
		  final ParserInput<Input> input
		) throws ParserError {
			final var result = getParserRight().parse(input);
			getLogger().debug(
			  "second `or` parser succeeded: {}", result.result()
			);
			return result;
		}

		private ParserResult<Output, Input> getResultLeft(
		  final ParserInput<Input> input
		) throws ParserError {
			final var result = getParserLeft().parse(input);
			getLogger().debug(
			  "first `or` parser succeeded: {}", result.result()
			);
			return result;
		}
	}

	/**
	 * An alternation parser where the two branches produce different output
	 * types.
	 *
	 * <p>The result is wrapped in a {@link ParserResult.Either}: a {@link
	 * ParserResult.Either.Left} if the left branch succeeds or a {@link
	 * ParserResult.Either.Right} if only the right branch succeeds. If both
	 * fail a {@link NeitherWasSuccessful} error is thrown.
	 *
	 * @param <OutputLeft> the type produced by the left parser
	 * @param <OutputRight> the type produced by the right parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static class Either<OutputLeft, OutputRight, Input> extends OrParser<
	  ParserResult.Either<OutputLeft, OutputRight>,
	  OutputLeft,
	  OutputRight,
	  Input> {

		/**
		 * Creates a new {@code Either} parser.
		 *
		 * @param parserLeft the left (preferred) parser; never {@code null}
		 * @param parserRight the right (fallback) parser; never {@code null}
		 * @param logger the logger for debug output; never {@code null}
		 */
		public Either(
		  final Parser<OutputLeft, Input> parserLeft,
		  final Parser<OutputRight, Input> parserRight,
		  final Logger                     logger
		) {
			super(parserLeft, parserRight, logger);
		}

		/**
		 * Tries the left parser; on failure tries the right parser.
		 *
		 * @param input the input to parse; never {@code null}
		 * @return a result wrapping the successful branch's value in {@link
		 *   ParserResult.Either.Left}
		 *     or {@link ParserResult.Either.Right}
		 * @throws NeitherWasSuccessful if both branches fail
		 */
		@Override
		public ParserResult<ParserResult.Either<OutputLeft, OutputRight>, Input>
		parse(@NonNull final ParserInput<Input> input) throws ParserError {
			getLogger().debug("processing `orElse` parser");
			try {
				return getResultLeft(input);
			} catch (final ParserError errorLeft) {
				try {
					return getResultRight(input);
				} catch (final ParserError errorRight) {
					throw new NeitherWasSuccessful(errorLeft, errorRight);
				}
			}
		}

		private ParserResult<
		  ParserResult.Either<OutputLeft, OutputRight>,
		  Input>
		getResultRight(final ParserInput<Input> input) throws ParserError {
			final var result = getParserRight().parse(input);
			getLogger().trace(
			  "right `orElse` parser succeeded: {}", result.result()
			);
			return new ParserResult<>(
			  new ParserResult.Either.Right<>(result.result()),
			  result.remainder()
			);
		}

		private ParserResult<
		  ParserResult.Either<OutputLeft, OutputRight>,
		  Input>
		getResultLeft(final ParserInput<Input> input) throws ParserError {
			final var result = getParserLeft().parse(input);
			getLogger().trace(
			  "left `orElse` parser succeeded: {}", result.result()
			);
			return new ParserResult<>(
			  new ParserResult.Either.Left<>(result.result()),
			  result.remainder()
			);
		}
	}

	/**
	 * Creates a new {@code OrParser}.
	 *
	 * @param parserLeft the left (preferred) parser; never {@code null}
	 * @param parserRight the right (fallback) parser; never {@code null}
	 * @param logger the logger for debug output; never {@code null}
	 */
	public OrParser(
	  final Parser<OutputLeft, Input> parserLeft,
	  final Parser<OutputRight, Input> parserRight,
	  final Logger                     logger
	) {
		super(parserLeft, parserRight, logger);
	}
}
