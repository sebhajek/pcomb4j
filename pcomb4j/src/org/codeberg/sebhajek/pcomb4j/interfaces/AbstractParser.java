package org.codeberg.sebhajek.pcomb4j.interfaces;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.slf4j.Logger;

public abstract class AbstractParser<Output, Input>
		implements CombinatorParser<Output, Input> {

	private final Logger logger;

	public AbstractParser(Logger logger) {
		this.logger = logger;
	}

	public Parser<Output, Input> getParser() {
		return this;
	}

	@Override
	public final Logger getLogger() {
		return logger;
	}
}
