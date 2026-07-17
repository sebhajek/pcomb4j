package org.codeberg.sebhajek.pcomb4j.parsers;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractPairParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public abstract class OrParser<Output, OutputLeft, OutputRight, Input>
		extends AbstractPairParser<Output, OutputLeft, OutputRight, Input> {

	public static class NeitherWasSuccessful extends ParserError.Branch {
		public NeitherWasSuccessful(ParserError errorLeft, ParserError errorRight) {
			super("Neither was successful", errorLeft, errorRight);
		}
	}

	public static class Or<Output, Input>
			extends OrParser<Output, Output, Output, Input> {

		public Or(
				final Parser<Output, Input> parserLeft,
				final Parser<Output, Input> parserRight,
				final Logger logger) {
			super(parserLeft, parserRight, logger);
		}

		@Override
		public ParserResult<Output, Input> parse(
				@NonNull final ParserInput<Input> input) throws ParserError {
			getLogger().info("processing `or` parser");
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
				final ParserInput<Input> input) throws ParserError {
			final var result = getParserRight().parse(input);
			getLogger().debug(
					"second `or` parser succeeded: {}", result.result());
			return result;
		}

		private ParserResult<Output, Input> getResultLeft(
				final ParserInput<Input> input) throws ParserError {
			final var result = getParserLeft().parse(input);
			getLogger().debug(
					"first `or` parser succeeded: {}", result.result());
			return result;
		}
	}

	public static class Either<OutputLeft, OutputRight, Input>
			extends OrParser<ParserResult.Either<OutputLeft, OutputRight>, OutputLeft, OutputRight, Input> {

		public Either(
				final Parser<OutputLeft, Input> parserLeft,
				final Parser<OutputRight, Input> parserRight,
				final Logger logger) {
			super(parserLeft, parserRight, logger);
		}

		@Override
		public ParserResult<ParserResult.Either<OutputLeft, OutputRight>, Input> parse(
				@NonNull final ParserInput<Input> input) throws ParserError {
			getLogger().info("processing `orElse` parser");
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

		private ParserResult<ParserResult.Either<OutputLeft, OutputRight>, Input> getResultRight(
				final ParserInput<Input> input) throws ParserError {
			final var result = getParserRight().parse(input);
			getLogger().trace(
					"right `orElse` parser succeeded: {}", result.result());
			return new ParserResult<>(
					new ParserResult.Either.Right<>(result.result()),
					result.remainder());
		}

		private ParserResult<ParserResult.Either<OutputLeft, OutputRight>, Input> getResultLeft(
				final ParserInput<Input> input) throws ParserError {
			final var result = getParserLeft().parse(input);
			getLogger().trace(
					"left `orElse` parser succeeded: {}", result.result());
			return new ParserResult<>(
					new ParserResult.Either.Left<>(result.result()),
					result.remainder());
		}
	}

	public OrParser(
			final Parser<OutputLeft, Input> parserLeft,
			final Parser<OutputRight, Input> parserRight,
			final Logger logger) {
		super(parserLeft, parserRight, logger);
	}
}
