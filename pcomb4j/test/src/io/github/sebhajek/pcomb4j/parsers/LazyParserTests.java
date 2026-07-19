package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.ParserFactory;
import io.github.sebhajek.pcomb4j.ParserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LazyParserTests {

	private static final Logger LOGGER =
	  LoggerFactory.getLogger(LazyParserTests.class);
	private static final ParserFactory PARSER_FACTORY =
	  ParserFactory.withLogger(LOGGER);

	@Test
	@SuppressWarnings("unchecked")
	void lazyViaFactory() throws Exception {
		final var supplier = mock(Supplier.class);
		when(supplier.get()).thenReturn(PARSER_FACTORY.any());
		final var lazy = PARSER_FACTORY.lazy(supplier);
		final var input = ParserInput.fromString("abc");

		final var result1 = lazy.parse(input);
		final var result2 = lazy.parse(result1.remainder());

		assertThat(result1.result()).isEqualTo('a');
		assertThat(result2.result()).isEqualTo('b');
		verify(supplier, times(2)).get();
	}

	@Test
	@SuppressWarnings("unchecked")
	void lazyMemoizedViaFactory() throws Exception {
		final var supplier = mock(Supplier.class);
		when(supplier.get()).thenReturn(PARSER_FACTORY.any());
		final var lazyMemoized = PARSER_FACTORY.lazyMemoized(supplier);
		final var input = ParserInput.fromString("abc");

		final var result1 = lazyMemoized.parse(input);
		final var result2 = lazyMemoized.parse(result1.remainder());

		assertThat(result1.result()).isEqualTo('a');
		assertThat(result2.result()).isEqualTo('b');
		verify(supplier, times(1)).get();
	}
}
