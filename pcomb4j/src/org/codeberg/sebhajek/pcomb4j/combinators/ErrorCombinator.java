package org.codeberg.sebhajek.pcomb4j.combinators;

import java.util.function.BiFunction;

import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.interfaces.DelegateParser;
import org.codeberg.sebhajek.pcomb4j.parsers.ErrorParser;

public interface ErrorCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	public default ErrorParser.Label<Output, Input> labelError(
	  final String label
	) {
		return new ErrorParser.Label<>(label, getParser(), getLogger());
	}

	public default ErrorParser.Message<Output, Input> labelError(
	  final BiFunction<ParserInput<Input>, ParserError, String> messageFactory
	) {
		return new ErrorParser.Message<>(
		  messageFactory, getParser(), getLogger()
		);
	}

	public default ErrorParser.Supplied<Output, Input> withError(
	  final BiFunction<ParserInput<Input>, ParserError, ParserError>
	        errorFactory
	) {
		return new ErrorParser.Supplied<>(
		  errorFactory, getParser(), getLogger()
		);
	}
}
