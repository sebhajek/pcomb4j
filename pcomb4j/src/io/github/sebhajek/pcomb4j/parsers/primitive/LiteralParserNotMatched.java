package io.github.sebhajek.pcomb4j.parsers.primitive;

import io.github.sebhajek.pcomb4j.ParserError;

public final class LiteralParserNotMatched extends ParserError.Leaf {

	public LiteralParserNotMatched() { super("Literal not matched."); }
}
