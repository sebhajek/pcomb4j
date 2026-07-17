package io.github.sebhajek.pcomb4j.parsers;

import java.util.Optional;

import io.github.sebhajek.pcomb4j.Parser;
import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserInput;
import io.github.sebhajek.pcomb4j.ParserResult;
import io.github.sebhajek.pcomb4j.interfaces.AbstractSourcedParser;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

/**
 * A {@link Parser} that wraps another parser's result in an
 * {@link Optional}, never failing.
 *
 * <p>If the inner parser succeeds, its result is wrapped in
 * {@link Optional#of(Object)}. If the inner parser throws a
 * {@link ParserError}, the error is silently swallowed and
 * {@link Optional#empty()} is returned — leaving the input unchanged.
 *
 * @param <Output>  the type of value produced by the inner parser
 * @param <Input>   the type of element consumed from the input
 */
public class OptionalParser<Output, Input>
  extends AbstractSourcedParser<Optional<Output>, Output, Input> {

	/**
	 * Creates a new {@code OptionalParser}.
	 *
	 * @param parserSource the inner parser to try; never {@code null}
	 * @param logger       the logger used for debug output; never {@code null}
	 */
	public OptionalParser(Parser<Output, Input> parserSource, Logger logger) {
		super(parserSource, logger);
	}

	/**
	 * Tries the inner parser; on success wraps the result in
	 * {@link Optional#of(Object)}, on failure returns
	 * {@link Optional#empty()} without consuming any input.
	 *
	 * @param parserInput the input to parse; never {@code null}
	 * @return a {@link ParserResult} whose value is always non-null
	 * @throws ParserError never — this parser always succeeds
	 */
	@Override
	public ParserResult<Optional<Output>, Input> parse(
	  @NonNull ParserInput<Input> parserInput
	) throws ParserError {
		var logger = getLogger();
		logger.info("processing `optional` parser");
		try {
			final var result = getParserSource().parse(parserInput);
			logger.debug("`optional` parser succeeded: {}", result.result());
			return result.with(Optional.of(result.result()));
		} catch (final ParserError err) {
			logger.debug("`optional` parser failed");
			return new ParserResult<>(Optional.empty(), parserInput);
		}
	}
}
