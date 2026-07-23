package io.github.sebhajek.pcomb4j.parsers.filter;

import io.github.sebhajek.pcomb4j.ParserError;

public class NotParserNegativeParserSuccess extends ParserError.Leaf {

	public NotParserNegativeParserSuccess(final String message) {
		super("negative parser succeeded with result: %s".formatted(message));
	}
}
