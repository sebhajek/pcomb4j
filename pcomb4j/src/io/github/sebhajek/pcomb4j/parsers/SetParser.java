package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.Collections;
import java.util.Set;

public abstract sealed class SetParser<Input>
  extends       AbstractParser<Input, Input> {

	public static final class SetNotMatched extends ParserError.Leaf {

		public SetNotMatched() { super("Set not matched."); }
	}

	public static final class Any<Input> extends SetParser<Input> {

		public Any(Set<Input> inputSet, Logger logger) {
			super(inputSet, logger);
		}

		@Override
		public ParserResult<Input, Input> parse(
		  @NonNull ParserInput<Input> parserInput
		) throws ParserError {
			final var current = parserInput.getCurrent();
			if (getInputSet().contains(current)) {
				getLogger().trace("getting `anyOf`: {}", current);
				return new ParserResult<Input, Input>(
				  current, parserInput.advance()
				);
			} else {
				throw new SetNotMatched();
			}
		}
	}

	public static final class None<Input> extends SetParser<Input> {

		public None(Set<Input> inputSet, Logger logger) {
			super(inputSet, logger);
		}

		@Override
		public ParserResult<Input, Input> parse(
		  @NonNull ParserInput<Input> parserInput
		) throws ParserError {
			final var current = parserInput.getCurrent();
			if (getInputSet().contains(current)) {
				throw new SetNotMatched();
			} else {
				getLogger().trace("getting `noneOf`: {}", current);
				return new ParserResult<Input, Input>(
				  current, parserInput.advance()
				);
			}
		}
	}

	private final Set<Input> inputSet;

	public SetParser(Set<Input> inputSet, Logger logger) {
		super(logger);
		this.inputSet = Collections.unmodifiableSet(inputSet);
	}

	protected Set<Input> getInputSet() { return inputSet; }
}
