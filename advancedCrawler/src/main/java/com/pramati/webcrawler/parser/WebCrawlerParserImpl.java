package com.pramati.webcrawler.parser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebCrawlerParserImpl implements Parser {

	public Elements parseForAnchors(Document doc) {
		Elements elements = null;
		if (doc != null) {
			elements = doc.select("a");
		}
		return elements;
	}
}
