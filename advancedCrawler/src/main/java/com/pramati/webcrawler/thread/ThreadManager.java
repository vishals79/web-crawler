package com.pramati.webcrawler.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.pramati.webcrawler.filter.Filter;
import com.pramati.webcrawler.parser.Parser;

public class ThreadManager {
	
	private Parser parser = null;
	private Filter filter = null;
	
	private BlockingQueue<String> urlsQueue = null;
	private Map<String, String> seenUrls = null;
	
	private int criteriaNo = 0;
	private String downloadPath = null;
	private String baseUrl = null;
	private String homeAddress = null;
	
	public ThreadManager(){
		urlsQueue = new LinkedBlockingDeque<String>();
		seenUrls = new HashMap<String, String>();
	}
	
	public int startCrawling(){
		long start = System.currentTimeMillis();
		long end = 0;
		
		try {
			urlsQueue.put(getBaseUrl());
			seenUrls.put(getBaseUrl(), getBaseUrl());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		URLProcesser urlProcessor1 = new URLProcesser(urlsQueue, parser, filter, criteriaNo, downloadPath, baseUrl, homeAddress, seenUrls);
		URLProcesser urlProcessor2 = new URLProcesser(urlsQueue, parser, filter, criteriaNo, downloadPath, baseUrl, homeAddress, seenUrls);
		URLProcesser urlProcessor3 = new URLProcesser(urlsQueue, parser, filter, criteriaNo, downloadPath, baseUrl, homeAddress, seenUrls);
		URLProcesser urlProcessor4 = new URLProcesser(urlsQueue, parser, filter, criteriaNo, downloadPath, baseUrl, homeAddress, seenUrls);
		URLProcesser urlProcessor5 = new URLProcesser(urlsQueue, parser, filter, criteriaNo, downloadPath, baseUrl, homeAddress, seenUrls);
		URLProcesser urlProcessor6 = new URLProcesser(urlsQueue, parser, filter, criteriaNo, downloadPath, baseUrl, homeAddress, seenUrls);
		URLProcesser urlProcessor7 = new URLProcesser(urlsQueue, parser, filter, criteriaNo, downloadPath, baseUrl, homeAddress, seenUrls);
		URLProcesser urlProcessor8 = new URLProcesser(urlsQueue, parser, filter, criteriaNo, downloadPath, baseUrl, homeAddress, seenUrls);
		
		Thread p1 = new Thread(urlProcessor1);
		Thread p2 = new Thread(urlProcessor2);
		Thread p3 = new Thread(urlProcessor3);
		Thread p4 = new Thread(urlProcessor4);
		Thread p5 = new Thread(urlProcessor5);
		Thread p6 = new Thread(urlProcessor6);
		Thread p7 = new Thread(urlProcessor7);
		Thread p8 = new Thread(urlProcessor8);
		
		p1.start();
		p2.start();
		p3.start();
		p4.start();
		p5.start();
		p6.start();
		p7.start();
		p8.start();
		
		try {
			p1.join();
			p2.join();
			p3.join();
			p4.join();
			p5.join();
			p6.join();
			p7.join();
			p8.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		end = System.currentTimeMillis();
		
		System.out.println(" Time Taken :"+(end-start));
		return 1;
	}
	
	public Parser getParser() {
		return parser;
	}
	public void setParser(Parser parser) {
		this.parser = parser;
	}
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public int getCriteriaNo() {
		return criteriaNo;
	}

	public void setCriteriaNo(int criteriaNo) {
		this.criteriaNo = criteriaNo;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

}
