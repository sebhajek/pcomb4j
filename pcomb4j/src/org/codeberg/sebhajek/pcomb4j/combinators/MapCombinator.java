package org.codeberg.sebhajek.pcomb4j.combinators;

import java.util.function.Function;

import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.codeberg.sebhajek.pcomb4j.parsers.MapParser;

public interface MapCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default<OutputMapped> MapParser.Pure<OutputMapped, Output, Input>
	                             pure(final OutputMapped value) {
		final var logger = getLogger();
		logger.debug("building `pure` parser");
		return new MapParser.Pure<>(getParser(), value, logger);
	}

	public default<OutputMapped> MapParser
	  .Transform<OutputMapped, Output, Input>
	  map(final Function<Output, OutputMapped> mapper) {
		final var logger = getLogger();
		logger.debug("building `map` parser");
		return new MapParser.Transform<>(getParser(), mapper, logger);
	}

	// this junk should be `Output extends OutputMapped` but java can't model
	// that correctly with the generics
	public default<OutputMapped> MapParser.Widen<OutputMapped, Output, Input>
	                             widen(final Class<OutputMapped> type) {
		final var logger = getLogger();
		logger.debug("building `widen` parser to {}", type.getName());
		return new MapParser.Widen<>(getParser(), type, logger);
	}
}
