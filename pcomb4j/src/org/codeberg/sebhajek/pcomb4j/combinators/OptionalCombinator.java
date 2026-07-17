package org.codeberg.sebhajek.pcomb4j.combinators;

import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.codeberg.sebhajek.pcomb4j.parsers.OptionalParser;

public interface OptionalCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default OptionalParser<Output, Input> optional() {
		final var logger = getLogger();
		logger.debug("building `optional` parser");
		return new OptionalParser<>(getParser(), logger);
	}
}
