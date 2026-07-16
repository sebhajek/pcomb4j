package org.codeberg.sebhajek.pcomb4j.input;

import org.codeberg.sebhajek.pcomb4j.ParserError;

public sealed class ParserInputError extends ParserError {
	public static final class EOF extends ParserInputError {}

	public static final class CouldNotRead extends ParserInputError {}
}
