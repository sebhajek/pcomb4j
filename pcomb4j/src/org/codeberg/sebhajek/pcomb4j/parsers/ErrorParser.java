package org.codeberg.sebhajek.pcomb4j.parsers;

import java.util.function.BiFunction;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.codeberg.sebhajek.pcomb4j.ParserError;
import org.codeberg.sebhajek.pcomb4j.ParserInput;
import org.codeberg.sebhajek.pcomb4j.ParserResult;
import org.codeberg.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public abstract class ErrorParser<Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	public static class LabeledError extends ParserError.Wrapped {

		public LabeledError(
		  final String      message,
		  final ParserError errorInner
		) {
			super(message, errorInner);
		}
	}

	public static class Supplied<Output, Input>
	  extends ErrorParser<Output, Input> {

		private final BiFunction<ParserInput<Input>, ParserError, ParserError>
		              errorFactory;

		public Supplied(
		  BiFunction<ParserInput<Input>, ParserError, ParserError> errorFactory,
		  Parser<Output, Input>                                    parserSource,
		  Logger                                                   logger
		) {
			super(parserSource, logger);
			this.errorFactory = errorFactory;
		}

		@Override
		public ParserResult<Output, Input> parse(
		  @NonNull final ParserInput<Input> parserInput
		) throws ParserError {
			try {
				return getParserSource().parse(parserInput);
			} catch (final ParserError errorInner) {
				throw getErrorFactory().apply(parserInput, errorInner);
			}
		}

		protected BiFunction<ParserInput<Input>, ParserError, ParserError>
		getErrorFactory() {
			return errorFactory;
		}
	}

	public static class Message<Output, Input>
	  extends ErrorParser<Output, Input> {

		private final BiFunction<ParserInput<Input>, ParserError, String>
		              messageFactory;

		public Message(
		  final BiFunction<ParserInput<Input>, ParserError, String>
		        messageFactory,
		  final Parser<Output, Input> parserSource,
		  final Logger                logger
		) {
			super(parserSource, logger);
			this.messageFactory = messageFactory;
		}

		@Override
		public ParserResult<Output, Input> parse(
		  @NonNull final ParserInput<Input> parserInput
		) throws ParserError {
			try {
				return getParserSource().parse(parserInput);
			} catch (final ParserError errorInner) {
				final var message =
				  getMessageFactory().apply(parserInput, errorInner);
				throw new LabeledError(message, errorInner);
			}
		}

		protected BiFunction<ParserInput<Input>, ParserError, String>
		getMessageFactory() {
			return messageFactory;
		}
	}

	public static class Label<Output, Input>
	  extends ErrorParser<Output, Input> {

		private final String message;

		public Label(
		  final String message,
		  final Parser<Output, Input> parserSource,
		  final Logger                logger
		) {
			super(parserSource, logger);
			this.message = message;
		}

		@Override
		public ParserResult<Output, Input> parse(
		  @NonNull final ParserInput<Input> parserInput
		) throws ParserError {
			try {
				return getParserSource().parse(parserInput);
			} catch (final ParserError errorInner) {
				throw new LabeledError(getMessage(), errorInner);
			}
		}

		protected String getMessage() { return message; }
	}

	public ErrorParser(
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
	}
}
