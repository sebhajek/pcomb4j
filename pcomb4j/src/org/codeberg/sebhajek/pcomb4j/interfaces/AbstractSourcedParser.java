package org.codeberg.sebhajek.pcomb4j.interfaces;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.slf4j.Logger;

public abstract class AbstractSourcedParser<Output, OutputSource, Input>
		extends AbstractParser<Output, Input> {

	private final Parser<OutputSource, Input> parserSource;

	public AbstractSourcedParser(
			final Parser<OutputSource, Input> parserSource,
			final Logger logger) {
		super(logger);
		this.parserSource = parserSource;
	}

	protected Parser<OutputSource, Input> getParserSource() {
		return parserSource;
	}
}
