package org.codeberg.sebhajek.pcomb4j.combinators;

import java.util.function.Function;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.codeberg.sebhajek.pcomb4j.parsers.FlatMapParser;

public interface FlatMapCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default<OutputNext> FlatMapParser<OutputNext, Output, Input> flatMap(
	  final Function<Output, Parser<OutputNext, Input>> binder
	) {
		final var logger = getLogger();
		logger.debug("building `flatMap` parser");
		return new FlatMapParser<>(getParser(), binder, logger);
	}
}
