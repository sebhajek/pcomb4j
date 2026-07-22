package io.github.sebhajek.pcomb4j.interfaces;

import io.github.sebhajek.pcomb4j.ParserCombinator;
import io.github.sebhajek.pcomb4j.combinators.AndCombinator;
import io.github.sebhajek.pcomb4j.combinators.AssociativeChainCombinator;
import io.github.sebhajek.pcomb4j.combinators.CardinalityCombinator;
import io.github.sebhajek.pcomb4j.combinators.ErrorCombinator;
import io.github.sebhajek.pcomb4j.combinators.FilterCombinator;
import io.github.sebhajek.pcomb4j.combinators.FlatMapCombinator;
import io.github.sebhajek.pcomb4j.combinators.LookAheadCombinator;
import io.github.sebhajek.pcomb4j.combinators.MapCombinator;
import io.github.sebhajek.pcomb4j.combinators.NotCombinator;
import io.github.sebhajek.pcomb4j.combinators.OptionalCombinator;
import io.github.sebhajek.pcomb4j.combinators.OrCombinator;
import io.github.sebhajek.pcomb4j.combinators.SurroundCombinator;
import io.github.sebhajek.pcomb4j.combinators.TraceCombinator;

/**
 * A mixin interface that assembles all combinator interfaces into a single
 * type, providing the complete fluent combinator API available on a {@link
 * ParserCombinator}.
 *
 * <p>By implementing this interface (typically by extending {@link
 * AbstractParser}), a parser gains default implementations of {@code map},
 * {@code flatMap}, {@code filter}, {@code filterLookAhead},
 * {@code and}, {@code or}, {@code optional}, {@code zeroOrMore}, {@code
 * oneOrMore}, {@code labelError}, and more.
 *
 * @param <Output> the type of value produced by a successful parse
 * @param <Input> the type of element consumed from the input
 */
public interface CombinatorParser<Output, Input>
  extends FilterCombinator<Output, Input>, MapCombinator<Output, Input>,
          FlatMapCombinator<Output, Input>, AndCombinator<Output, Input>,
          OrCombinator<Output, Input>, OptionalCombinator<Output, Input>,
          CardinalityCombinator<Output, Input>, ErrorCombinator<Output, Input>,
          TraceCombinator<Output, Input>, NotCombinator<Output, Input>,
          LookAheadCombinator<Output, Input>,
          SurroundCombinator<Output, Input>,
          AssociativeChainCombinator<Output, Input> {}
