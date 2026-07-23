package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;

public interface SourcedParser<Output, OutputSource, Input>
  extends CombinatorParser<Output, Input> {
	/**
	 * Returns the inner source parser.
	 *
	 * @return the source parser; never {@code null}
	 */
	Parser<OutputSource, Input> getParserSource();
}
