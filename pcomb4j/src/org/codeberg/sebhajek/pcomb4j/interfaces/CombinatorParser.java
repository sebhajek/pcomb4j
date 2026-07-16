package org.codeberg.sebhajek.pcomb4j.interfaces;

import org.codeberg.sebhajek.pcomb4j.combinators.AndCombinator;
import org.codeberg.sebhajek.pcomb4j.combinators.CardinalityCombinator;
import org.codeberg.sebhajek.pcomb4j.combinators.FilterCombinator;
import org.codeberg.sebhajek.pcomb4j.combinators.FlatMapCombinator;
import org.codeberg.sebhajek.pcomb4j.combinators.MapCombinator;
import org.codeberg.sebhajek.pcomb4j.combinators.OptionalCombinator;
import org.codeberg.sebhajek.pcomb4j.combinators.OrCombinator;

public interface CombinatorParser<Output, Input>
  extends FilterCombinator<Output, Input>, MapCombinator<Output, Input>,
          FlatMapCombinator<Output, Input>, AndCombinator<Output, Input>,
          OrCombinator<Output, Input>, OptionalCombinator<Output, Input>,
          CardinalityCombinator<Output, Input> {}
