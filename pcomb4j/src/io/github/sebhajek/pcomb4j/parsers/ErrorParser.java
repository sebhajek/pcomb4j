package io.github.sebhajek.pcomb4j.parsers;

import java.util.function.BiFunction;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

/**
 * Abstract base for parsers that intercept failures from an inner parser and
 * replace or enrich the thrown {@link ParserError}.
 *
 * <p>Three concrete subclasses are provided:
 *
 * <ul>
 *   <li>{@link Label} — static message label.
 *   <li>{@link Message} — dynamic message computed from input and error.
 *   <li>{@link Supplied} — the replacement error is fully computed by a factory
 * function.
 * </ul>
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public abstract class ErrorParser<Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	/**
	 * A {@link ParserError.Wrapped} that carries a contextual label applied by
	 * one of the {@link ErrorParser} subclasses.
	 */
	public static class LabeledError extends ParserError.Wrapped {
		/**
		 * Creates a new {@code LabeledError}.
		 *
		 * @param message the contextual label
		 * @param errorInner the underlying error
		 */
		public LabeledError(
		  final String      message,
		  final ParserError errorInner
		) {
			super(message, errorInner);
		}
	}

	/**
	 * Replaces the error using a factory function.
	 *
	 * @param <Output> the type of value produced by a successful parse
	 * @param <Input> the type of element consumed from the input
	 */
	public static class Supplied<Output, Input>
	  extends ErrorParser<Output, Input> {

		private final BiFunction<ParserInput<Input>, ParserError, ParserError>
		              errorFactory;

		/**
		 * Creates a new {@code Supplied} error parser.
		 *
		 * @param errorFactory function receiving input + original error,
		 *   returning the replacement
		 *     error
		 * @param parserSource inner parser to delegate to
		 * @param logger logger for debug output
		 */
		public Supplied(
		  BiFunction<ParserInput<Input>, ParserError, ParserError> errorFactory,
		  Parser<Output, Input>                                    parserSource,
		  Logger                                                   logger
		) {
			super(parserSource, logger);
			this.errorFactory = errorFactory;
		}

		/** {@inheritDoc} */
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

		/**
		 * Returns the error factory function.
		 *
		 * @return the error factory; never {@code null}
		 */
		protected BiFunction<ParserInput<Input>, ParserError, ParserError>
		getErrorFactory() {
			return errorFactory;
		}
	}

	/**
	 * Wraps the original error in a {@link LabeledError} with a dynamically
	 * computed message.
	 *
	 * @param <Output> the type of value produced by a successful parse
	 * @param <Input> the type of element consumed from the input
	 */
	public static class Message<Output, Input>
	  extends ErrorParser<Output, Input> {

		private final BiFunction<ParserInput<Input>, ParserError, String>
		              messageFactory;

		/**
		 * Creates a new {@code Message} error parser.
		 *
		 * @param messageFactory function computing the label string
		 * @param parserSource inner parser to delegate to
		 * @param logger logger for debug output
		 */
		public Message(
		  final BiFunction<ParserInput<Input>, ParserError, String>
		        messageFactory,
		  final Parser<Output, Input> parserSource,
		  final Logger                logger
		) {
			super(parserSource, logger);
			this.messageFactory = messageFactory;
		}

		/** {@inheritDoc} */
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

		/**
		 * Returns the message factory function.
		 *
		 * @return the message factory; never {@code null}
		 */
		protected BiFunction<ParserInput<Input>, ParserError, String>
		getMessageFactory() {
			return messageFactory;
		}
	}

	/**
	 * Wraps the original error in a {@link LabeledError} with a fixed static
	 * message.
	 *
	 * @param <Output> the type of value produced by a successful parse
	 * @param <Input> the type of element consumed from the input
	 */
	public static class Label<Output, Input>
	  extends ErrorParser<Output, Input> {

		private final String message;

		/**
		 * Creates a new {@code Label} error parser.
		 *
		 * @param message the static label to attach to any failure
		 * @param parserSource inner parser to delegate to
		 * @param logger logger for debug output
		 */
		public Label(
		  final String message,
		  final Parser<Output, Input> parserSource,
		  final Logger                logger
		) {
			super(parserSource, logger);
			this.message = message;
		}

		/** {@inheritDoc} */
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

		/**
		 * Returns the static label message.
		 *
		 * @return the label; never {@code null}
		 */
		protected String getMessage() { return message; }
	}

	/**
	 * Creates a new {@code ErrorParser}.
	 *
	 * @param parserSource the inner parser to delegate to
	 * @param logger the logger used for debug output
	 */
	public ErrorParser(
	  final Parser<Output, Input> parserSource,
	  final Logger                logger
	) {
		super(parserSource, logger);
	}
}
