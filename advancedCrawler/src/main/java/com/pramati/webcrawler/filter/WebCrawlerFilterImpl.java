package com.pramati.webcrawler.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebCrawlerFilterImpl implements Filter {

	public boolean isEmail(Document doc) {
		String content = null;
		Pattern pattern = null;
		Matcher matcher = null;
		if (doc != null) {
			content = doc.text();
			pattern = Pattern.compile("From");
			matcher = pattern.matcher(content);
			if (!matcher.find()) {
				return false;
			}

			pattern = Pattern.compile("[dD]ate");
			matcher = pattern.matcher(content);

			if (!matcher.find()) {
				return false;
			}

			pattern = Pattern.compile("[sS]ubject");
			matcher = pattern.matcher(content);

			if (!matcher.find()) {
				return false;
			}
		}

		return true;
	}

	private static boolean isNotEmpty(String str) {
		if (str != null && str.length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean matchForYear(Document doc, String year) {
		Elements elements = null;
		String dateString = null;
		if (doc != null && isNotEmpty(year)) {
			elements = doc.select("td");
			for (int i = 0; i < elements.size(); i++) {
				if ("Date".equalsIgnoreCase(elements.get(i).text())) {
					dateString = elements.get(++i).text();
					dateString = dateString.substring(12, 16);
					if (year.equalsIgnoreCase(dateString)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * public boolean isEmail(Element element) { String href = null; if(element
	 * != null){ href = element.attr("href"); if(isNotEmpty(href)){
	 * if(!href.contains("@")){ return false; } if(href.subSequence(0,
	 * href.indexOf('@')).length() == 0){ return false; } if(href.indexOf('@')
	 * == href.length()-1){ return false; } } } return true; }
	 */
}
