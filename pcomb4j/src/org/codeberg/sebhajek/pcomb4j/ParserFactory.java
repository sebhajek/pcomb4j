package org.codeberg.sebhajek.pcomb4j;

import org.codeberg.sebhajek.pcomb4j.factories.AbstractFactory;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

/**
 * Entry point for constructing basic, ready-made {@link Parser} instances.
 *
 * <p>{@code ParserFactory} aggregates several factory interfaces, giving
 * callers a single, convenient object from which to create parsers.
 *
 * <p>Instances are obtained via {@link #withLogger(Logger)}:
 *
 * <pre>{@code
 * ParserFactory factory = ParserFactory.withLogger(logger);
 * }</pre>
 */
public final class ParserFactory implements AbstractFactory {

	/**
	 * Creates a new {@link ParserFactory} that uses the given {@link Logger}
	 * for any parsers it produces.
	 *
	 * @param logger the logger to associate with this factory
	 * @return a new {@link ParserFactory}
	 */
	public static final ParserFactory withLogger(@NonNull final Logger logger) {
		return new ParserFactory(logger);
	}

	private final Logger logger;

	private ParserFactory(@NonNull final Logger logger) {
		this.logger = logger;
	}

	/** {@inheritDoc} */
	@Override
	public final Logger getLogger() {
		return logger;
	}
}
