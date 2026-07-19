package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.interfaces.CombinatorParser;
import io.github.sebhajek.pcomb4j.interfaces.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.TraceParser;

import org.slf4j.Logger;

public interface TraceCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default CombinatorParser<Output, Input>
	trace(String message, Logger loggerDedicated) {
		final var logger = getLogger();
		logger.debug("building `trace` parser");
		return new TraceParser<>(message, loggerDedicated, getParser(), logger);
	}

	public default CombinatorParser<Output, Input> trace(
	  Logger loggerDedicated
	) {
		final var logger = getLogger();
		logger.debug("building `trace` parser");
		return new TraceParser<>(loggerDedicated, getParser(), logger);
	}

	public default CombinatorParser<Output, Input> trace(String message) {
		final var logger = getLogger();
		logger.debug("building `trace` parser");
		return new TraceParser<>(message, getParser(), logger);
	}
}
