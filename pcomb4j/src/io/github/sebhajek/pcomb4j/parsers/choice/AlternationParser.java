package io.github.sebhajek.pcomb4j.parsers.choice;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.parsers.AbstractPairParser;

import org.slf4j.Logger;

/**
 * Abstract base for parsers that implement ordered choice (alternation).
 *
 * <p>The left parser is tried first; if it fails the right parser is tried on
 * the <em>same</em> (unchanged) input. If both fail a {@link
 * NeitherWasSuccessful} error — containing both child errors — is thrown.
 *
 * <p>Two concrete subclasses are provided:
 *
 * <ul>
 *   <li>{@link OrParser} — both branches must produce the same output type; the
 * result is just {@code Output}.
 *   <li>{@link EitherParser} — the branches may produce different types; the
 * result is a {@link io.github.sebhajek.pcomb4j.ParserResult.Either}.
 * </ul>
 *
 * @param <Output> the type of value produced by this parser
 * @param <OutputLeft> the type produced by the left parser
 * @param <OutputRight> the type produced by the right parser
 * @param <Input> the type of element consumed from the input
 */
public abstract
          sealed class AlternationParser<Output, OutputLeft, OutputRight, Input>
  extends AbstractPairParser<Output, OutputLeft, OutputRight, Input> permits
            OrParser,
          EitherParser {

	/**
	 * Creates a new {@code AlternationParser}.
	 *
	 * @param parserLeft the left (preferred) parser; never {@code null}
	 * @param parserRight the right (fallback) parser; never {@code null}
	 * @param logger the logger for debug output; never {@code null}
	 */
	public AlternationParser(
	  final Parser<OutputLeft, Input> parserLeft,
	  final Parser<OutputRight, Input> parserRight,
	  final Logger                     logger
	) {
		super(parserLeft, parserRight, logger);
	}
}