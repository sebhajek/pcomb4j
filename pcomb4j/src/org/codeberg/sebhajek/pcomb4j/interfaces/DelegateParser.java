package org.codeberg.sebhajek.pcomb4j.interfaces;

import org.codeberg.sebhajek.pcomb4j.Parser;
import org.slf4j.Logger;

public interface DelegateParser<Output, Input> extends Parser<Output, Input> {

	public Parser<Output, Input> getParser();

	public Logger getLogger();
}
