package org.codeberg.sebhajek.pcomb4j.parsers;

import java.util.function.Function;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class FlatMapParser<OutputNext, Output, Input>
  extends AbstractSourcedParser<OutputNext, Output, Input> {

	private final Function<Output, Parser<OutputNext, Input>> binder;

	public FlatMapParser(
	  final Parser<Output, Input> parserSource,
	  final Function<Output, Parser<OutputNext, Input>> binder,
	  final Logger                                      logger
	) {
		super(parserSource, logger);
		this.binder = binder;
	}

	@Override
	public ParserResult<OutputNext, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().debug("processing `flatMap` parser");
		final var resultInitial = getResultInitial(parserInput);
		return getResultNext(resultInitial);
	}

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
