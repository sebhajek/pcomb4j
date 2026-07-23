package io.github.sebhajek.pcomb4j.combinators;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.parsers.CombinatorParser;
import io.github.sebhajek.pcomb4j.parsers.DelegateParser;
import io.github.sebhajek.pcomb4j.parsers.decorator.ErrorParserLabel;
import io.github.sebhajek.pcomb4j.parsers.decorator.ErrorParserMessage;
import io.github.sebhajek.pcomb4j.parsers.decorator.ErrorParserSupplied;

import java.util.function.BiFunction;

/**
 * Combinator that intercepts failures from this parser and replaces or enriches
 * the thrown {@link ParserError} with a more descriptive one.
 *
 * <p>Three variants are provided:
 *
 * <ul>
 *   <li>{@link #labelError(String)} — attaches a fixed label string.
 *   <li>{@link #labelError(BiFunction)} — computes a dynamic label from the
 * current input and the original error. <li>{@link #withError(BiFunction)} —
 * replaces the error entirely using a factory function.
 * </ul>
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public interface ErrorCombinator<Output, Input>
  extends DelegateParser<Output, Input> {

	/**
	 * Creates a parser that, on failure, wraps the original error in an {@link
	 * io.github.sebhajek.pcomb4j.parsers.decorator.ErrorLabeledError} with the
	 * given fixed {@code label}.
	 *
	 * @param label a static human-readable description of what was expected
	 * @return a new {@link ErrorParserLabel} wrapping this parser
	 */
	public default CombinatorParser<Output, Input> labelError(
	  final String label
	) {
		return new ErrorParserLabel<>(
		  label, DelegateParser.getDelegate(getParser()), getLogger()
		);
	}

	/**
	 * Creates a parser that, on failure, wraps the original error in an {@link
	 * io.github.sebhajek.pcomb4j.parsers.decorator.ErrorLabeledError} whose
	 * message is computed dynamically from the current input and the original
	 * error.
	 *
	 * @param messageFactory a function receiving the current {@link
	 *   ParserInput} and the original
	 *     {@link ParserError}, returning a message string
	 * @return a new {@link ErrorParserMessage} wrapping this parser
	 */
	public default CombinatorParser<Output, Input> labelError(
	  final BiFunction<ParserInput<Input>, ParserError, String> messageFactory
	) {
		return new ErrorParserMessage<>(
		  messageFactory, DelegateParser.getDelegate(getParser()), getLogger()
		);
	}

	/**
	 * Creates a parser that, on failure, replaces the original error using a
	 * factory function that receives the current input and the original error.
	 *
	 * @param errorFactory a function receiving the current {@link ParserInput}
	 *   and the original
	 *     {@link ParserError}, returning the replacement {@link ParserError} to
	 * throw
	 * @return a new {@link ErrorParserSupplied} wrapping this parser
	 */
	public default CombinatorParser<Output, Input> withError(
	  final BiFunction<ParserInput<Input>, ParserError, ParserError>
	        errorFactory
	) {
		return new ErrorParserSupplied<>(
		  errorFactory, DelegateParser.getDelegate(getParser()), getLogger()
		);
	}
}
