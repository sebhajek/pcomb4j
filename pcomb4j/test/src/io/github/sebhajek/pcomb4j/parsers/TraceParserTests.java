package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserError;
import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TraceParserTests {

	@Test
	void traceCombinator() throws ParserError {
		final var mockLogger = mock(Logger.class);
		final var factory    = ParserFactory.withLogger(mockLogger);
		final var parser     = factory.<Character>any().trace("test");
		final var input      = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		verify(mockLogger).info(eq("parser: `{}` @ {}"), eq("test"), any());
	}

	@Test
	void traceWithLogger() throws ParserError {
		final var mockLogger          = mock(Logger.class);
		final var mockDedicatedLogger = mock(Logger.class);
		final var factory             = ParserFactory.withLogger(mockLogger);
		final var parser = factory.<Character>any().trace(mockDedicatedLogger);
		final var input  = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		verify(mockDedicatedLogger).info(eq("parser @ {}"), (Object) any());
		verify(mockLogger, never()).info(eq("parser @ {}"), (Object) any());
	}

	@Test
	void traceWithMessageAndLogger() throws ParserError {
		final var mockLogger          = mock(Logger.class);
		final var mockDedicatedLogger = mock(Logger.class);
		final var factory             = ParserFactory.withLogger(mockLogger);
		final var parser =
		  factory.<Character>any().trace("custom message", mockDedicatedLogger);
		final var input = ParserInput.fromString("abc");

		final var result = parser.parse(input);

		assertThat(result.result()).isEqualTo('a');
		verify(mockDedicatedLogger)
		  .info(eq("parser: `{}` @ {}"), eq("custom message"), any());
		verify(mockLogger, never())
		  .info(eq("parser: `{}` @ {}"), eq("custom message"), any());
	}
}
