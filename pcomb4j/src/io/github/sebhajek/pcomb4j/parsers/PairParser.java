package io.github.sebhajek.pcomb4j.parsers;

import io.github.sebhajek.pcomb4j.Parser;

public interface PairParser<Output,OutputLeft, OutputRight, Input> extends CombinatorParser<Output, Input> {
    /**
     * Returns the left (first-tried) parser.
     *
     * @return the left parser; never {@code null}
     */
    Parser<OutputLeft, Input> getParserLeft();

    /**
     * Returns the right (second) parser.
     *
     * @return the right parser; never {@code null}
     */
    Parser<OutputRight, Input> getParserRight();
}
