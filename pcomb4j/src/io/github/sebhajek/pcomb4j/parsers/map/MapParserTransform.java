package io.github.sebhajek.pcomb4j.parsers.map;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.slf4j.Logger;

import java.util.function.Function;

public final class MapParserTransform<OutputMapped, Output, Input>
  extends MapParser<OutputMapped, Output, Input> {

	private final Function<Output, OutputMapped> mapper;

	public MapParserTransform(
	  final Parser<Output, Input> parserSource,
	  final Function<Output, OutputMapped> mapper,
	  final Logger                         logger
	) {
		super(parserSource, logger);
		this.mapper = mapper;
	}

	Function<Output, OutputMapped> getMapper() { return mapper; }

	@Override
	protected OutputMapped getValueMapped(
	  final ParserResult<Output, Input> result
	) {
		final var valueMapped = getMapper().apply(result.result());
		getLogger().debug(
		  "processing `map` parser: {} -> {}", result.result(), valueMapped
		);
		return valueMapped;
	}
}
