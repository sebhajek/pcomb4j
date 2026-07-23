package io.github.sebhajek.pcomb4j.parsers.map;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.function.Function;

/**
 * A {@link Parser} that implements monadic bind (flatMap): it first applies an
 * inner "source" parser, passes the result to a {@code binder} function that
 * returns the <em>next</em> parser to apply, and then applies that next parser
 * to the remaining input.
 *
 * <p>This is the parser-combinator equivalent of {@code >>= } (bind) and
 * enables grammars whose structure depends on previously parsed values.
 *
 * @param <OutputNext> the type produced by the second (bound) parser
 * @param <Output> the type produced by the source parser
 * @param <Input> the type of element consumed from the input
 */
public class FlatMapParser<OutputNext, Output, Input>
  extends AbstractSourcedParser<OutputNext, Output, Input> {

	private final Function<Output, Parser<OutputNext, Input>> binder;

	/**
	 * Creates a new {@code FlatMapParser}.
	 *
	 * @param parserSource the first parser to apply; never {@code null}
	 * @param binder a function from the first parser's result to the second
	 *   parser; never {@code
	 *     null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public FlatMapParser(
	  final Parser<Output, Input> parserSource,
	  final Function<Output, Parser<OutputNext, Input>> binder,
	  final Logger                                      logger
	) {
		super(parserSource, logger);
		this.binder = binder;
	}

	/**
	 * Applies the source parser, passes its result to the binder to obtain the
	 * next parser, and applies that next parser to the remaining input.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return the result of the bound parser
	 * @throws ParserError if either the source or the bound parser fails
	 */
	@Override
	public ParserResult<OutputNext, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().debug("processing `flatMap` parser");
		final var resultInitial = getResultInitial(parserInput);
		return getResultNext(resultInitial);
	}

	/**
	 * Returns the binder function.
	 *
	 * @return the binder; never {@code null}
	 */
	protected Function<Output, Parser<OutputNext, Input>> getBinder() {
		return binder;
	}

	private ParserResult<OutputNext, Input> getResultNext(
	  final ParserResult<Output, Input> resultInitial
	) throws ParserError {
		final var parserNext = getBinder().apply(resultInitial.result());
		final var resultNext = parserNext.parse(resultInitial.remainder());
		getLogger().trace(
		  "bounded `flatMap` parser succeeded: {}", resultNext.result()
		);
		return resultNext;
	}

	private ParserResult<Output, Input> getResultInitial(
	  final ParserInput<Input> parserInput
	) throws ParserError {
		final var resultInitial = getParserSource().parse(parserInput);
		getLogger().trace(
		  "initial `flatMap` parser succeeded: {}", resultInitial.result()
		);
		return resultInitial;
	}
}
