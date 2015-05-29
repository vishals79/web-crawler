package com.pramati.webcrawler.filter;

import org.jsoup.nodes.Document;

public interface Filter {
	public boolean isEmail(Document doc);

	public boolean matchForYear(Document doc, String year);
}
