package org.codeberg.sebhajek.pcomb4j.combinators;

import java.util.function.Function;

import org.codeberg.sebhajek.pcomb4j.ParserCombinator;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface MapCombinator<A, B> extends DelegateParser<A, B> {

	public static final Logger LOGGER =
	  LoggerFactory.getLogger(MapCombinator.class);

	public default<C> ParserCombinator<C, B> pure(final C output) {
		LOGGER.debug("building `pure` parser");
		return ParserCombinator.withLogger(getLogger())
		  .from(getParser())
		  .map((_) -> output);
	}

	public default<C> ParserCombinator<C, B> map(
	  final Function<A, C> transformer
	) {
		LOGGER.debug("building `map` parser");
		final var logger = getLogger();
		return ParserCombinator.withLogger(logger).from((input) -> {
			final var result      = getParser().parse(input);
			final var transformed = transformer.apply(result.result());
			logger.info(
			  "processing `map` parser: {} -> {}", result.result(), transformed
			);
			return result.with(transformed);
		});
	}

	public default<C> ParserCombinator<C, B> widen(Class<C> type) {
		LOGGER.debug("building `widen` parser to {}", type.getName());
		return ParserCombinator.withLogger(getLogger()).from(input -> {
			final var result = getParser().parse(input);
			getLogger().atInfo().log(() -> {
				return "processing `widen` parser from %s to %s".formatted(
				  result.result().getClass().getName(), type.getName()
				);
			});
			return result.with(type.cast(result.result()));
		});
	}
}
