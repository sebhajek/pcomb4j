package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

public class LookAheadParser<Output, OutputAhead, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	static final class ParseAheadFailed extends ParserError.Wrapped {
		public ParseAheadFailed(
		  final String      message,
		  final ParserError errorInner
		) {
			super(message, errorInner);
		}
	}

	private final Parser<OutputAhead, Input> parserAhead;

	public LookAheadParser(
	  final Parser<Output, Input> parserSource,
	  final Parser<OutputAhead, Input> parserAhead,
	  final Logger                     logger
	) {
		super(parserSource, logger);
		this.parserAhead = parserAhead;
	}

	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var result = getParserSource().parse(parserInput);
		try {
			final var _ = getParserAhead().parse(result.remainder());
		} catch (final ParserError error) {
			throw new ParseAheadFailed("look ahead failed", error);
		}
		return result;
	}

	protected Parser<OutputAhead, Input> getParserAhead() {
		return parserAhead;
	}
}
