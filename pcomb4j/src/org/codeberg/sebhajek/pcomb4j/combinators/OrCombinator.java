package org.codeberg.sebhajek.pcomb4j.combinators;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserCombinator;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface OrCombinator<A, B> extends DelegateParser<A, B> {

	public static sealed interface Either<L, R> {
		public static record Left<L, R>(L value) implements Either<L, R> {}

		public static record Right<L, R>(R value) implements Either<L, R> {}
	}

	public static final Logger LOGGER =
	  LoggerFactory.getLogger(OrCombinator.class);

	public default ParserCombinator<A, B> or(final Parser<A, B> parserOther) {
		LOGGER.debug("building `or` parser");
		final var logger = getLogger();
		return ParserCombinator.withLogger(logger).from((input) -> {
			logger.info("processing `or` parser");
			try {
				var result = getParser().parse(input);
				logger.debug(
				  "first `or` parser succeeded: {}", result.result()
				);
				return result;
			} catch (final ParserError err) {
				try {
					var result = parserOther.parse(input);
					logger.debug(
					  "second `or` parser succeeded: {}", result.result()
					);
					return result;
				} catch (final ParserError errInner) { throw errInner; }
			}
		});
	}

	public default<C> ParserCombinator<Either<A, C>, B> orElse(
	  final Parser<C, B> parserOther
	) {
		LOGGER.debug("building `orElse` parser");
		final var logger = getLogger();
		return ParserCombinator.withLogger(logger).from((input) -> {
			logger.info("processing `orElse` parser");
			try {
				final var result = getParser().parse(input);
				logger.debug(
				  "left `orElse` parser succeeded: {}", result.result()
				);
				return new ParserResult<>(
				  new Either.Left<A, C>(result.result()), result.remainder()
				);
			} catch (final ParserError err) {
				final var result = parserOther.parse(input);
				logger.debug(
				  "right `orElse` parser succeeded: {}", result.result()
				);
				return new ParserResult<>(
				  new Either.Right<A, C>(result.result()), result.remainder()
				);
			}
		});
	}
}
