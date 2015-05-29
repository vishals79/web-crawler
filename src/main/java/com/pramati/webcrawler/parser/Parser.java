package com.pramati.webcrawler.parser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public interface Parser {
	public Elements parseForAnchors(Document doc);
}
