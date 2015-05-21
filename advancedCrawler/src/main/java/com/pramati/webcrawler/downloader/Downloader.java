package com.pramati.webcrawler.downloader;

import org.jsoup.nodes.Document;

public interface Downloader {
	public void downloadEmail(String path, Document doc);
}
