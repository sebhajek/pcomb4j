package io.github.sebhajek.pcomb4j.parsers.primitive;

import io.github.sebhajek.pcomb4j.ParserError;

public final class SetParserNotMatched extends ParserError.Leaf {

	public SetParserNotMatched() { super("Set not matched."); }
}
