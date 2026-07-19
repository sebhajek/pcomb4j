package io.github.sebhajek.pcomb4j.factories;

import org.slf4j.Logger;

/**
 * Factory mix-in that provides access to the {@link Logger} shared by all
 * parsers created through this factory.
 *
 * <p>All other factory interfaces extend this interface so that their default
 * methods can forward the logger to newly created parsers.
 */
public interface LoggedFactory {

	/**
	 * Returns the {@link Logger} that will be passed to any parser created by
	 * this factory.
	 *
	 * @return the logger; never {@code null}
	 */
	public Logger getLogger();
}
