package org.codeberg.sebhajek.pcomb4j.parsers;

import java.util.function.Supplier;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class LazyParser<Output, Input> extends AbstractParser<Output, Input> {

	private final Supplier<Parser<Output, Input>> supplier;

	public LazyParser(Supplier<Parser<Output, Input>> supplier, Logger logger) {
		super(logger);
		this.supplier = supplier;
	}

	@Override
	public Parser<Output, Input> getParser() {
		return this.supplier.get();
	}

	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		getLogger().info("evaluating `lazy` parser");
		return this.getParser().parse(parserInput);
	}
}
