package io.github.sebhajek.pcomb4j.parsers.map;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.slf4j.Logger;

public final class MapParserCast<OutputMapped, Output, Input>
  extends MapParser<OutputMapped, Output, Input> {

	private final Class<OutputMapped> type;

	public MapParserCast(
	  final Parser<Output, Input> parserSource,
	  final Class<OutputMapped> type,
	  final Logger              logger
	) {
		super(parserSource, logger);
		this.type = type;
	}

	Class<OutputMapped> getType() { return type; }

	@Override
	protected OutputMapped getValueMapped(
	  final ParserResult<Output, Input> result
	) {
		getLogger().atDebug().log(() -> {
			return "processing `cast` parser: %s -> %s".formatted(
			  result.result().getClass().getName(), getType().getName()
			);
		});
		return getType().cast(result.result());
	}
}
