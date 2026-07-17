package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;
import org.slf4j.Logger;

public abstract class TraceParser<Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> {
	// TODO
	public TraceParser(Parser<Output, Input> parserSource, Logger logger) {
		super(parserSource, logger);
	}
}
