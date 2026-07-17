package org.codeberg.sebhajek.pcomb4j.combinators;

import java.util.function.Predicate;

import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.codeberg.sebhajek.pcomb4j.parsers.FilterParser;

public interface FilterCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default FilterParser<Output, Input> filter(
	  final Predicate<Output> predicate
	) {
		final var logger = getLogger();
		logger.debug("building `filter` parser");
		return new FilterParser<>(getParser(), predicate, logger);
	}
}
