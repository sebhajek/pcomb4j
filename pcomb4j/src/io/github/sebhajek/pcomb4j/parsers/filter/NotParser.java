package io.github.sebhajek.pcomb4j.parsers.filter;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.parsers.AbstractSourcedParser;

import org.jspecify.annotations.NonNull;

import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A {@link Parser} that succeeds only when an inner <em>source</em> parser
 * succeeds <em>and</em> a separate <em>negative</em> parser fails on the same
 * input.
 *
 * <p>The source and negative parsers run concurrently: the source is submitted
 * to a virtual-thread executor while the negative runs in the current thread.
 * If the negative parser succeeds, a {@link NotParserNegativeParserSuccess}
 * error is thrown. If the negative parser fails, the result of the source
 * parser is returned (or its error propagated).
 *
 * @param <OutputNegative> the output type of the negative parser
 * @param <Output> the output type of this parser (same as the source parser's
 *   output)
 * @param <Input> the type of element consumed from the input
 */
public class NotParser<OutputNegative, Output, Input>
  extends AbstractSourcedParser<Output, Output, Input> {

	private final Parser<OutputNegative, Input> parserNegative;

	/**
	 * Creates a new {@code NotParser}.
	 *
	 * @param parserSource the source parser that must succeed; never {@code
	 *   null}
	 * @param parserNegative the negative parser that must fail; never {@code
	 *   null}
	 * @param logger the logger used for debug output; never {@code null}
	 */
	public NotParser(
	  final Parser<Output, Input> parserSource,
	  final Parser<OutputNegative, Input> parserNegative,
	  final Logger                        logger
	) {
		super(parserSource, logger);
		this.parserNegative = parserNegative;
	}

	/**
	 * Returns the negative parser.
	 *
	 * @return the negative parser; never {@code null}
	 */
	public Parser<OutputNegative, Input> getParserNegative() {
		return parserNegative;
	}

	/**
	 * Parses the input by running the source and negative parsers concurrently.
	 *
	 * <p>The source parser is submitted to a virtual-thread executor. If the
	 * negative parser succeeds, a {@link NotParserNegativeParserSuccess} error
	 * is thrown. If the negative parser fails, the result of the source parser
	 * is returned.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return the result of the source parser if the negative parser failed
	 * @throws ParserError if the input is empty, the negative parser succeeds,
	 *   or the source parser
	 *     fails
	 */
	@Override
	public ParserResult<Output, Input> parse(
	  @NonNull final ParserInput<Input> parserInput
	) throws ParserError {
		final var logger = getLogger();
		logger.debug("processing `not` parser");
		try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			final var result = getResultFuture(parserInput, executor);
			final ParserResult<OutputNegative, Input> resultNegative;
			try {
				resultNegative = getParserNegative().parse(parserInput);
			} catch (final ParserError _) {
				logger.trace("`not` parser succeeded");
				return returnResult(result);
			}
			logger.trace(
			  "`not` parser failed: negative parser succeeded with {}",
			  resultNegative.result()
			);
			throw new NotParserNegativeParserSuccess(resultNegative.toString());
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
