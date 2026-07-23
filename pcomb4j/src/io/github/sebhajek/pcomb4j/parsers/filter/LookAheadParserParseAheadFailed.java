package io.github.sebhajek.pcomb4j.parsers.filter;

import io.github.sebhajek.pcomb4j.ParserError;

public class LookAheadParserParseAheadFailed extends ParserError.Wrapped {

	public LookAheadParserParseAheadFailed(
	  final String      message,
	  final ParserError errorInner
	) {
		super(message, errorInner);
	}
}
