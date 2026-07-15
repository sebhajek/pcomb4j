package org.codeberg.sebhajek.pcomb4j;

/**
 * Base class for all errors raised while parsing with pcomb4j.
 *
 * <p>Concrete parser implementations should extend this class to signal
 * specific failure conditions (e.g. unexpected end of input, an unmatched
 * literal, or a failed predicate).
 */
public abstract class ParserError extends Exception {}
