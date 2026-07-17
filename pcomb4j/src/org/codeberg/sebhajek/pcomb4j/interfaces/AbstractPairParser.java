package org.codeberg.sebhajek.pcomb4j.interfaces;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.slf4j.Logger;

public abstract class AbstractPairParser<Output, OutputLeft, OutputRight, Input>
  extends AbstractParser<Output, Input> {

	private final Parser<OutputLeft, Input> parserLeft;

	private final Parser<OutputRight, Input> parserRight;

	public AbstractPairParser(
	  final Parser<OutputLeft, Input> parserLeft,
	  final Parser<OutputRight, Input> parserRight,
	  final Logger                     logger
	) {
		super(logger);
		this.parserLeft  = parserLeft;
		this.parserRight = parserRight;
	}

	protected Parser<OutputLeft, Input> getParserLeft() { return parserLeft; }

	protected Parser<OutputRight, Input> getParserRight() {
		return parserRight;
	}
}
