package io.github.sebhajek.pcomb4j.parsers.primitive;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.Collections;
import java.util.Set;

/**
 * A parser that succeeds or fails based on whether the current input element
 * belongs to a fixed
 * {@link Set}.
 *
 * <p>Two concrete subclasses are provided:
 *
 * <ul>
 *   <li>{@link Any} — succeeds when the current element <em>is</em> in the set
 *   <li>{@link None} — succeeds when the current element <em>is not</em> in the
 * set
 * </ul>
 *
 * <p>In both cases the matched element is consumed and returned as the parse
 * result. On failure a
 * {@link SetNotMatched} error is thrown.
 *
 * @param <Input> the type of element consumed from the input
 */
public abstract sealed class SetParser<Input>
  extends       AbstractParser<Input, Input> {

	/**
	 * Error raised when the current input element does not satisfy the
	 * membership test, i.e. the element is absent from an {@link Any} set or
	 * present in a {@link None} set.
	 */
	public static final class SetNotMatched extends ParserError.Leaf {

		/** Creates a new {@code SetNotMatched} error. */
		public SetNotMatched() { super("Set not matched."); }
	}

	/**
	 * A parser that succeeds if the current input element is contained in the
	 * configured set.
	 *
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class Any<Input> extends SetParser<Input> {

		/**
		 * Creates a new {@code Any} parser.
		 *
		 * @param inputSet the set of accepted elements; never {@code null}
		 * @param logger the logger for debug output; never {@code null}
		 */
		public Any(Set<Input> inputSet, Logger logger) {
			super(inputSet, logger);
		}

		/**
		 * If the current element is in the configured set, consumes and returns
		 * it; otherwise throws
		 * {@link SetNotMatched}.
		 *
		 * @param parserInput the input stream; never {@code null}
		 * @return a result containing the matched element and the advanced
		 *   input
		 * @throws ParserError if the current element is not in the set
		 */
		@Override
		public ParserResult<Input, Input> parse(
		  @NonNull ParserInput<Input> parserInput
		) throws ParserError {
			final var logger = getLogger();
			logger.debug("processing `anyOf` parser");
			final var current = parserInput.getCurrent();
			if (getInputSet().contains(current)) {
				logger.trace("`anyOf` parser succeeded: {}", current);
				return new ParserResult<Input, Input>(
				  current, parserInput.advance()
				);
			} else {
				logger.trace("`anyOf` parser failed: {}", current);
				throw new SetNotMatched();
			}
		}
	}

	/**
	 * A parser that succeeds if the current input element is <em>not</em>
	 * contained in the configured set.
	 *
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class None<Input> extends SetParser<Input> {

		/**
		 * Creates a new {@code None} parser.
		 *
		 * @param inputSet the set of rejected elements; never {@code null}
		 * @param logger the logger for debug output; never {@code null}
		 */
		public None(Set<Input> inputSet, Logger logger) {
			super(inputSet, logger);
		}

		/**
		 * If the current element is <em>not</em> in the configured set,
		 * consumes and returns it; otherwise throws {@link SetNotMatched}.
		 *
		 * @param parserInput the input stream; never {@code null}
		 * @return a result containing the matched element and the advanced
		 *   input
		 * @throws ParserError if the current element is in the set
		 */
		@Override
		public ParserResult<Input, Input> parse(
		  @NonNull ParserInput<Input> parserInput
		) throws ParserError {
			final var logger = getLogger();
			logger.debug("processing `noneOf` parser");
			final var current = parserInput.getCurrent();
			if (getInputSet().contains(current)) {
				logger.trace("`noneOf` parser failed: {}", current);
				throw new SetNotMatched();
			} else {
				logger.trace("`noneOf` parser succeeded: {}", current);
				return new ParserResult<Input, Input>(
				  current, parserInput.advance()
				);
			}
		}
	}

	private final Set<Input> inputSet;

	/**
	 * Creates a new {@code SetParser}.
	 *
	 * @param inputSet the set used for membership tests; will be wrapped in an
	 *   unmodifiable set;
	 *     never {@code null}
	 * @param logger the logger for debug output; never {@code null}
	 */
	public SetParser(Set<Input> inputSet, Logger logger) {
		super(logger);
		this.inputSet = Collections.unmodifiableSet(inputSet);
	}

	/**
	 * Returns the (unmodifiable) set used for membership tests.
	 *
	 * @return the input set; never {@code null}
	 */
	protected Set<Input> getInputSet() { return inputSet; }
}
