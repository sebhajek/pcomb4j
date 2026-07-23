package io.github.sebhajek.pcomb4j.parsers.cardinal;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractParser;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Parser} that applies an element parser repeatedly, governed
 * independently by a {@link Cardinality} policy (how many matches are required
 * or permitted) and a {@link Separator} policy (what must appear between
 * matches), collecting all results into an unmodifiable {@link List}.
 *
 * <p>This single engine replaces what was previously five separate classes
 * ({@code CardinalParser},
 * {@code SkipCardinalParser}, {@code ExactCountParser}, {@code
 * SeparatedCardinalParser}, {@code UntilCardinalParser}): each of those was
 * really one loop shape with two independent axes of variation. Composing the
 * axes instead of enumerating their product also yields combinations none of
 * the originals supported, e.g. a separated sequence with a sentinel.
 *
 * @param <Output> the type of value produced by the element parser
 * @param <OutputSeparator> the type of value produced by the separator parser
 *   (irrelevant when
 *     {@link Separator.None} is used)
 * @param <Input> the type of element consumed from the input
 */
public final class CardinalParser<Output, OutputSeparator, Input>
  extends AbstractParser<List<Output>, Input> {

	/**
	 * A synthetic terminal error for cases with no underlying cause to
	 * propagate.
	 */
	private static final class InsufficientMatchError extends ParserError.Leaf {
		InsufficientMatchError(final String message) { super(message); }
	}

	private final Parser<Output, Input> element;

	private final Cardinality<Output, Input> cardinality;

	private final Separator<OutputSeparator, Input> separator;

	/**
	 * Creates a new {@code RepeatParser}.
	 *
	 * @param element the parser for each element; never {@code null}
	 * @param cardinality the match-count policy; never {@code null}
	 * @param separator the between-element policy; never {@code null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public CardinalParser(
	  final Parser<Output, Input> element,
	  final Cardinality<Output, Input> cardinality,
	  final Separator<OutputSeparator, Input> separator,
	  final Logger                            logger
	) {
		super(logger);
		this.element     = element;
		this.cardinality = cardinality;
		this.separator   = separator;
	}

	@Override
	public ParserResult<List<Output>, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var   logger           = getLogger();
		final var   results          = new ArrayList<Output>();
		var         current          = parserInput;
		ParserError lastElementError = null;
		logger.debug("processing `repeat` parser");

		while (!current.isEmpty() && !reachedExactBound(results.size())) {
			if (sentinelMatches(current)) {
				logger.trace("sentinel detected, stopping");
				break;
			}

			final boolean isFirst        = results.isEmpty();
			var           afterSeparator = current;

			if (!isFirst
			    && (separator instanceof final Separator.Between<OutputSeparator, Input> between)) {
				try {
					afterSeparator =
					  between.parser().parse(current).remainder();
					logger.trace("separator matched, continuing");
				} catch (final ParserError separatorError) {
					logger.trace("end of sequence (no separator)");
					break;
				}
			}

			try {
				final var result = element.parse(afterSeparator);
				results.addLast(result.result());
				current          = result.remainder();
				lastElementError = null;
			} catch (final ParserError elementError) {
				lastElementError = elementError;
				if (!isFirst
				    && (separator instanceof final Separator.Between<OutputSeparator, Input> between)) {
					if (!between.trailingAccepted()) {
						logger.trace(
						  "separator without following element propagated"
						);
						throw elementError;
					}
					logger.trace("accepted trailing separator");
					current = afterSeparator;
				} else {
					logger.trace("encountered end of series");
				}
				break;
			}
		}

		enforceCardinality(results, lastElementError);
		logger.trace(
		  "`repeat` parser succeeded: matched {} elements", results.size()
		);
		return new ParserResult<>(
		  Collections.unmodifiableList(results), current
		);
	}

	private boolean reachedExactBound(final int matched) {
		return cardinality
		  instanceof Cardinality.Exactly<Output, Input>(final var count)
		  && matched >= count;
	}

	private boolean sentinelMatches(final ParserInput<Input> input) {
		if (!(cardinality
		      instanceof final Cardinality.Until<Output, Input> until)) {
			return false;
		}
		return switch (until.sentinel()) {
			case Sentinel.PredicateBased<Output, Input>(final var predicate) ->
				matchesPredicateSentinel(input, predicate);
			case Sentinel.ParserBased<Output, Input>(final var sentinelParser)
			  ->
				matchesParserSentinel(input, sentinelParser);
		};
	}

	private boolean matchesPredicateSentinel(
	  final ParserInput<Input> input,
	  final java.util.function.Predicate<Output> predicate
	) {
		try {
			final var result = element.parse(input);
			if (predicate.test(result.result())) {
				getLogger().trace("predicate sentinel matched");
				return true;
			}
		} catch (final ParserError err) {
			getLogger().trace("element parse failed during sentinel check");
		}
		return false;
	}

	private boolean matchesParserSentinel(
	  final ParserInput<Input> input,
	  final Parser<Output, Input> sentinelParser
	) {
		try {
			sentinelParser.parse(input);
			getLogger().trace("parser sentinel matched");
			return true;
		} catch (final ParserError err) { return false; }
	}

	private void enforceCardinality(
	  final List<Output> results,
	  final @Nullable ParserError  lastElementError
	) throws ParserError {
		final boolean needsAtLeastOne = switch (cardinality) {
			case final Cardinality.ZeroOrMore<Output, Input> _ -> false;
			case final Cardinality.OneOrMore<Output, Input> _ ->  true;
			case Cardinality.Exactly<Output, Input>(final var count) ->
				count > 0;
			case Cardinality
			  .Until<Output, Input>(final var _, final var atLeastOne)
			  ->
				atLeastOne;
		};

		if (needsAtLeastOne && results.isEmpty()) {
			throw lastElementError != null
			  ? lastElementError
			  : new InsufficientMatchError(
			      "expected at least one element to match"
			    );
		}

		if (cardinality
		      instanceof Cardinality.Exactly<Output, Input>(final var count)
		    && results.size() != count) {
			throw lastElementError != null
			  ? lastElementError
			  : new InsufficientMatchError(
			      "expected exactly " + count + " elements, got "
			      + results.size()
			    );
		}
	}
}
