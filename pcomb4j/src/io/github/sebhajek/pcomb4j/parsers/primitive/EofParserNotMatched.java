package io.github.sebhajek.pcomb4j.parsers.primitive;

import io.github.sebhajek.pcomb4j.ParserError;

public final class EofParserNotMatched extends ParserError.Leaf {

	public EofParserNotMatched() { super("Expected EOF."); }
}
