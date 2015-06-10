package com.pramati.webcrawler.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class URLProcessorData {
	
	private BlockingQueue<String> urlsQueue;
	private Map<String, String> seenUrls;
	
	public URLProcessorData() {
		super();
		
		urlsQueue = new LinkedBlockingDeque<String>();
		seenUrls = new HashMap<String, String>();
	}
	
	public BlockingQueue<String> getUrlsQueue() {
		return urlsQueue;
	}
	public void setUrlsQueue(BlockingQueue<String> urlsQueue) {
		this.urlsQueue = urlsQueue;
	}
	public Map<String, String> getSeenUrls() {
		return seenUrls;
	}
	public void setSeenUrls(Map<String, String> seenUrls) {
		this.seenUrls = seenUrls;
	}
}
