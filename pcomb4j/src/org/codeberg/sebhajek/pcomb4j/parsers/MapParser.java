package org.codeberg.sebhajek.pcomb4j.parsers;

import java.util.function.Function;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public abstract class MapParser<OutputMapped, Output, Input>
  extends AbstractSourcedParser<OutputMapped, Output, Input> {

	public static class Transform<OutputMapped, Output, Input>
	  extends MapParser<OutputMapped, Output, Input> {

		private final Function<Output, OutputMapped> mapper;

		public Transform(
		  final Parser<Output, Input> parserSource,
		  final Function<Output, OutputMapped> mapper,
		  final Logger                         logger
		) {
			super(parserSource, logger);
			this.mapper = mapper;
		}

		protected Function<Output, OutputMapped> getMapper() { return mapper; }

		@Override
		protected OutputMapped getValueMapped(
		  final ParserResult<Output, Input> result
		) {
			final var valueMapped = getMapper().apply(result.result());
			getLogger().info(
			  "processing `map` parser: {} -> {}", result.result(), valueMapped
			);
			return valueMapped;
		}
	}

	public static class Pure<OutputMapped, Output, Input>
	  extends MapParser<OutputMapped, Output, Input> {

		private final OutputMapped value;

		public Pure(
		  final Parser<Output, Input> parserSource,
		  final OutputMapped          value,
		  final Logger                logger
		) {
			super(parserSource, logger);
			this.value = value;
		}

		protected OutputMapped getValue() { return value; }

		@Override
		protected OutputMapped getValueMapped(
		  final ParserResult<Output, Input> result
		) {
			getLogger().info("processing `pure` parser: -> {}", getValue());
			return getValue();
		}
	}

	// this junk should be `Output extends OutputMapped` but java can't model
	// that correctly with the generics
	public static class Widen<OutputMapped, Output, Input>
	  extends MapParser<OutputMapped, Output, Input> {

		private final Class<OutputMapped> type;

		public Widen(
		  final Parser<Output, Input> parserSource,
		  final Class<OutputMapped> type,
		  final Logger              logger
		) {
			super(parserSource, logger);
			this.type = type;
		}

		protected Class<OutputMapped> getType() { return type; }

		@Override
		protected OutputMapped getValueMapped(
		  final ParserResult<Output, Input> result
		) {
			getLogger().atInfo().log(() -> {
				return "processing `widen` parser: %s -> %s".formatted(
				  result.result().getClass().getName(), getType().getName()
				);
			});
			return getType().cast(result.result());
		}
	}

	public MapParser(
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
	}

	@Override
	public ParserResult<OutputMapped, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		return getResultMapped(parserInput);
	}

	protected abstract OutputMapped
	getValueMapped(final ParserResult<Output, Input> result);

	private ParserResult<OutputMapped, Input> getResultMapped(
	  final ParserInput<Input> parserInput
	) throws ParserError {
		final var result      = getResult(parserInput);
		final var valueMapped = getValueMapped(result);
		return result.with(valueMapped);
	}

	private ParserResult<Output, Input> getResult(
	  final ParserInput<Input> parserInput
	) throws ParserError {
		final var result = getParserSource().parse(parserInput);
		return result;
	}
}
