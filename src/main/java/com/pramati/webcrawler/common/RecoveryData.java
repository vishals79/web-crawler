package com.pramati.webcrawler.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class RecoveryData {
	
	private BlockingQueue<String> adddedURLQueue;
	private BlockingQueue<String> removedURLQueue;
	private Map<String, String> seenUrls;
	
	
	public RecoveryData() {
		super();
		
		adddedURLQueue = new LinkedBlockingDeque<String>();
		removedURLQueue = new LinkedBlockingDeque<String>();
		seenUrls = new HashMap<String, String>();
	}
	public BlockingQueue<String> getUrlsQueue() {
		return adddedURLQueue;
	}
	public void setUrlsQueue(BlockingQueue<String> urlsQueue) {
		this.adddedURLQueue = urlsQueue;
	}
	public BlockingQueue<String> getRemovedURLQueue() {
		return removedURLQueue;
	}
	public void setRemovedURLQueue(BlockingQueue<String> removedURLQueue) {
		this.removedURLQueue = removedURLQueue;
	}
	public Map<String, String> getSeenUrls() {
		return seenUrls;
	}
	public void setSeenUrls(Map<String, String> seenUrls) {
		this.seenUrls = seenUrls;
	}
	
	
}
