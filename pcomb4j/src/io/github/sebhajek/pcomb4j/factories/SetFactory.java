package io.github.sebhajek.pcomb4j.factories;

import io.github.sebhajek.pcomb4j.parsers.SetParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public interface SetFactory extends LoggedFactory {

	@SuppressWarnings("unchecked")
	public default<Input> SetParser.Any<Input> anyOf(final Input... values) {
		var logger = getLogger();
		logger.info("building `anyOf` parser");
		var set = Arrays.stream(values).collect(Collectors.toSet());
		return new SetParser.Any<>(set, logger);
	}

	public default<Input> SetParser.Any<Input> anyOf(Collection<Input> values) {
		var logger = getLogger();
		logger.info("building `anyOf` parser");
		var set = values.stream().collect(Collectors.toSet());
		return new SetParser.Any<>(set, logger);
	}

	@SuppressWarnings("unchecked")
	public default<Input> SetParser.None<Input> noneOf(final Input... values) {
		var logger = getLogger();
		logger.info("building `noneOf` parser");
		var set = Arrays.stream(values).collect(Collectors.toSet());
		return new SetParser.None<>(set, logger);
	}

	public default<Input> SetParser.None<Input> noneOf(
	  Collection<Input> values
	) {
		var logger = getLogger();
		logger.info("building `noneOf` parser");
		var set = values.stream().collect(Collectors.toSet());
		return new SetParser.None<>(set, logger);
	}
}
