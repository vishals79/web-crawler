package com.pramati.webcrawler.filter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public interface Filter {
	public boolean isEmail(Document doc);
	
	public boolean matchForYear(Document doc,String year);
}
