package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.function.Supplier;

public class FailureParser<Output, Input>
  extends AbstractParser<Output, Input> {

	Supplier<ParserError> supplier;

	public FailureParser(Supplier<ParserError> supplier, Logger logger) {
		super(logger);
		this.supplier = supplier;
	}

	protected Supplier<ParserError> getSupplier() { return supplier; }

	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		throw getSupplier().get();
	}
}
