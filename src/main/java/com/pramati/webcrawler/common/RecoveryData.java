package com.pramati.webcrawler.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class RecoveryData {
	
	private BlockingQueue<String> adddedURLQueue;
	private BlockingQueue<String> removedURLQueue;
	private BlockingQueue<String> visitedUrls;
	
	public RecoveryData() {
		super();
		
		adddedURLQueue = new LinkedBlockingDeque<String>();
		removedURLQueue = new LinkedBlockingDeque<String>();
		visitedUrls = new LinkedBlockingDeque<String>();
	}
	
	public BlockingQueue<String> getAdddedURLQueue() {
		return adddedURLQueue;
	}

	public void setAdddedURLQueue(BlockingQueue<String> adddedURLQueue) {
		this.adddedURLQueue = adddedURLQueue;
	}

	public BlockingQueue<String> getRemovedURLQueue() {
		return removedURLQueue;
	}
	public void setRemovedURLQueue(BlockingQueue<String> removedURLQueue) {
		this.removedURLQueue = removedURLQueue;
	}
	public BlockingQueue<String> getVisitedUrls() {
		return visitedUrls;
	}
	public void setVisitedUrls(BlockingQueue<String> visitedUrls) {
		this.visitedUrls = visitedUrls;
	}
}
