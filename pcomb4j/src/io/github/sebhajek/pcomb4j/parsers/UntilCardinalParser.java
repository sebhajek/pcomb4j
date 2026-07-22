package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.function.Predicate;

/**
 * A {@link Parser} that applies an inner element parser repeatedly until a
 * sentinel is detected, collecting all non-sentinel results into an
 * unmodifiable {@link List}.
 *
 * <p>The sentinel element is <strong>not</strong> consumed: parsing stops at
 * the position before the sentinel. Two sentinel types are available via the
 * {@link Sentinel} sealed interface:
 *
 * <ul>
 *   <li>{@link Sentinel.PredicateBased} — a {@link Predicate} tested against
 *   each parsed element. <li>{@link Sentinel.ParserBased} — a {@link Parser}
 *   tried against the input before each element parse.
 * </ul>
 *
 * @param <Output> the type of value produced by the element parser
 * @param <Input> the type of element consumed from the input
 */
public abstract sealed class UntilCardinalParser<Output, Input>
  extends       AbstractParser<List<Output>, Input> {

	/**
	 * The sentinel that signals the end of the sequence.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <Input> the type of element consumed from the input
	 */
	public sealed interface Sentinel<Output, Input> {

		/**
		 * A sentinel based on a {@link Predicate} applied to each parsed
		 * element.
		 *
		 * @param <Output> the type of value produced by the element parser
		 * @param <Input> the type of element consumed from the input
		 * @param predicate the predicate that identifies the sentinel element;
		 *   never {@code null}
		 */
		public static record PredicateBased<Output, Input>(
		  Predicate<Output> predicate
		) implements Sentinel<Output, Input> {}

		/**
		 * A sentinel based on a {@link Parser} tried against the input before
		 * each element parse.
		 *
		 * @param <Output> the type of value produced by the element parser
		 * @param <Input> the type of element consumed from the input
		 * @param parser the parser that identifies the sentinel; never {@code
		 *   null}
		 */
		public static record ParserBased<Output, Input>(
		  Parser<Output, Input> parser
		) implements Sentinel<Output, Input> {}
	}

	/**
	 * An {@link UntilCardinalParser} that applies the element parser zero or
	 * more times until the sentinel is detected.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class ZeroOrMore<Output, Input>
	  extends UntilCardinalParser<Output, Input> {

		/**
		 * Creates a new {@code ZeroOrMore} until parser.
		 *
		 * @param elementParser the parser for each non-sentinel element; never
		 *   {@code null}
		 * @param sentinel the sentinel that signals the end; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public ZeroOrMore(
		  final Parser<Output, Input> elementParser,
		  final Sentinel<Output, Input> sentinel,
		  final Logger                  logger
		) {
			super(elementParser, sentinel, logger);
		}

		@Override
		protected boolean isOneOrMore() {
			return false;
		}
	}

	/**
	 * An {@link UntilCardinalParser} that applies the element parser one or
	 * more times until the sentinel is detected.
	 *
	 * @param <Output> the type of value produced by the element parser
	 * @param <Input> the type of element consumed from the input
	 */
	public static final class OneOrMore<Output, Input>
	  extends UntilCardinalParser<Output, Input> {

		/**
		 * Creates a new {@code OneOrMore} until parser.
		 *
		 * @param elementParser the parser for each non-sentinel element; never
		 *   {@code null}
		 * @param sentinel the sentinel that signals the end; never {@code null}
		 * @param logger the logger used for debug output; never {@code null}
		 */
		public OneOrMore(
		  final Parser<Output, Input> elementParser,
		  final Sentinel<Output, Input> sentinel,
		  final Logger                  logger
		) {
			super(elementParser, sentinel, logger);
		}

		@Override
		protected boolean isOneOrMore() {
			return true;
		}
	}

	private final Parser<Output, Input> elementParser;

	private final Sentinel<Output, Input> sentinel;

	/**
	 * Creates a new {@code UntilCardinalParser}.
	 *
	 * @param elementParser the parser for each non-sentinel element; never
	 *   {@code null}
	 * @param sentinel the sentinel that signals the end; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public UntilCardinalParser(
	  final Parser<Output, Input> elementParser,
	  final Sentinel<Output, Input> sentinel,
	  final Logger                  logger
	) {
		super(logger);
		this.elementParser = elementParser;
		this.sentinel      = sentinel;
	}

	/**
	 * Returns whether at least one non-sentinel match is required.
	 *
	 * @return {@code true} if at least one match is required
	 */
	protected abstract boolean isOneOrMore();

	/**
	 * Returns the element parser.
	 *
	 * @return the element parser; never {@code null}
	 */
	protected Parser<Output, Input> getElementParser() { return elementParser; }

	/**
	 * Returns the sentinel.
	 *
	 * @return the sentinel; never {@code null}
	 */
	protected Sentinel<Output, Input> getSentinel() { return sentinel; }

	/**
	 * Applies the element parser repeatedly until the sentinel is detected
	 * or the input is exhausted, collecting non-sentinel results into an
	 * unmodifiable list.
	 *
	 * <p>The sentinel element is not consumed.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} with the collected elements
	 * @throws ParserError if the cardinality is one-or-more and no
	 *   non-sentinel element could be parsed
	 */
	@Override
	public ParserResult<List<Output>, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger       = getLogger();
		final var results      = new ArrayList<Output>();
		var       currentInput = parserInput;
		logger.debug("processing `until` parser");

		while (!currentInput.isEmpty()) {
			if (isSentinel(currentInput)) {
				logger.trace("sentinel detected, stopping");
				break;
			}

			try {
				final var result = getElementParser().parse(currentInput);
				results.addLast(result.result());
				currentInput = result.remainder();
			} catch (final ParserError err) {
				if (isOneOrMore() && results.isEmpty()) { throw err; }
				break;
			}
		}

		logger.trace(
		  "`until` parser succeeded: matched {} elements", results.size()
		);
		return new ParserResult<>(
		  Collections.unmodifiableList(results), currentInput
		);
	}

	private boolean isSentinel(final ParserInput<Input> input) {
		return switch (getSentinel()) {
			case Sentinel.PredicateBased(var predicate) ->
				isSentinelPredicate(input, predicate);
			case Sentinel.ParserBased(var parser) ->
				isSentinelParser(input, parser);
		};
	}

	private boolean isSentinelPredicate(
	  final ParserInput<Input> input,
	  final Predicate<Output> predicate
	) {
		try {
			final var result = getElementParser().parse(input);
			if (predicate.test(result.result())) {
				getLogger().trace("predicate sentinel matched");
				return true;
			}
		} catch (final ParserError err) {
			getLogger().trace("element parse failed during sentinel check");
		}
		return false;
	}

	private boolean isSentinelParser(
	  final ParserInput<Input> input,
	  final Parser<Output, Input> parser
	) {
		try {
			parser.parse(input);
			getLogger().trace("parser sentinel matched");
			return true;
		} catch (final ParserError err) {
			getLogger().trace("parser sentinel not matched");
			return false;
		}
	}
}
