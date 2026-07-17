package org.codeberg.sebhajek.pcomb4j.input;

import org.codeberg.sebhajek.pcomb4j.ParserError;

public abstract sealed class ParserInputError extends ParserError.Leaf {
	public ParserInputError(String message) {
		super(message);
	}

	public static final class EOF extends ParserInputError {

		public EOF() {
			super("End of file.");
		}
	}

	public static final class CouldNotRead extends ParserInputError {

		public CouldNotRead() {
			super("Could not read.");
		}
	}
}
