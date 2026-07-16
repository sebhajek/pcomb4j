package org.codeberg.sebhajek.pcomb4j.factories;

import org.codeberg.sebhajek.pcomb4j.parsers.AnyParser;

public interface AnyFactory extends LoggedFactory {

	public default<Input> AnyParser<Input> any() {
		final var logger = getLogger();
		logger.debug("building `any` parser");
		return new AnyParser<>(logger);
	}
}
