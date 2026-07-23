package io.github.sebhajek.pcomb4j.parsers.primitive;

import io.github.sebhajek.pcomb4j.parsers.AbstractParser;

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
 *   <li>{@link SetParserAny} — succeeds when the current element <em>is</em>
 *       in the set
 *   <li>{@link SetParserNone} — succeeds when the current element
 *       <em>is not</em> in the set
 * </ul>
 *
 * <p>In both cases the matched element is consumed and returned as the parse
 * result. On failure a
 * {@link SetParserNotMatched} error is thrown.
 *
 * @param <Input> the type of element consumed from the input
 */
public abstract sealed class SetParser<Input>
  extends AbstractParser<Input, Input> permits SetParserAny, SetParserNone {

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
