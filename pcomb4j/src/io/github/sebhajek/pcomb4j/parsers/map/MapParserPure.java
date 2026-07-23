package io.github.sebhajek.pcomb4j.parsers.map;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.slf4j.Logger;

public final class MapParserPure<OutputMapped, Output, Input>
  extends MapParser<OutputMapped, Output, Input> {

	private final OutputMapped value;

	public MapParserPure(
	  final Parser<Output, Input> parserSource,
	  final OutputMapped          value,
	  final Logger                logger
	) {
		super(parserSource, logger);
		this.value = value;
	}

	OutputMapped getValue() { return value; }

	@Override
	protected OutputMapped getValueMapped(
	  final ParserResult<Output, Input> result
	) {
		getLogger().debug("processing `pure` parser: -> {}", getValue());
		return getValue();
	}
}
