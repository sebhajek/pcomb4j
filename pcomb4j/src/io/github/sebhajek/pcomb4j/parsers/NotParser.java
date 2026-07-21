package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NotParser<OutputNegative, Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	static final class NegativeParserSuccess extends ParserError.Leaf {

		public NegativeParserSuccess(final String message) {
			super(
			  "negative parser succeeded with result: %s".formatted(message)
			);
		}
	}

	private final Parser<OutputNegative, Input> parserNegative;

	public NotParser(
	  final Parser<Output, Input> parserSource,
	  final Parser<OutputNegative, Input> parserNegative,
	  final Logger                        logger
	) {
		super(parserSource, logger);
		this.parserNegative = parserNegative;
	}

	public Parser<OutputNegative, Input> getParserNegative() {
		return parserNegative;
	}

	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			final var result = getResultFuture(parserInput, executor);
			final ParserResult<OutputNegative, Input> resultNegative;
			try {
				resultNegative = getParserNegative().parse(parserInput);
			} catch (final ParserError _) { return returnResult(result); }
			throw new NegativeParserSuccess(resultNegative.toString());
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(ex);
		}
	}

	private ParserResult<Output, Input> returnResult(
	  final Future<ParserResult<Output, Input>> result
	) throws InterruptedException, ParserError {
		try {
			return result.get();
		} catch (final ExecutionException ex) {
			final Throwable cause = ex.getCause();
			if (cause instanceof final ParserError parserError) {
				throw parserError;
			}
			throw new RuntimeException(cause);
		}
	}

	private final Future<ParserResult<Output, Input>> getResultFuture(
	  final ParserInput<Input> parserInput,
	  final ExecutorService    executor
	) {
		final var result =
		  executor.submit(() -> getParserSource().parse(parserInput));
		return result;
	}
}
