package com.pramati.webcrawler.thread;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pramati.webcrawler.downloader.Downloader;
import com.pramati.webcrawler.downloader.DownloaderImpl;
import com.pramati.webcrawler.filter.Filter;
import com.pramati.webcrawler.parser.Parser;
import com.pramati.webcrawler.thread.trigger.Trigger;

public class URLProcesser implements Runnable{
	
	private BlockingQueue<String> urlsQueue = null;
	private Parser parser = null;
	private Filter filter = null;
	
	private int criteriaNo = 0;
	private String downloadPath = null;
	private String baseUrl = null;
	private String homeAddress = null;
	private Map<String, String> seenUrls = null;
	
	private static Log logger = LogFactory.getLog(URLProcesser.class);
	
	private final String HTTP = "http://";
	private final String HREF = "href";
	
	private Trigger trigger = null;
	
	public URLProcesser(BlockingQueue<String> urlsQueue, Parser parser,
			Filter filter, int criteriaNo, String downloadPath, String baseUrl,
			String homeAddress, Map<String, String> seenUrls,Trigger trigger) {
		super();
		this.urlsQueue = urlsQueue;
		this.parser = parser;
		this.filter = filter;
		this.criteriaNo = criteriaNo;
		this.downloadPath = downloadPath;
		this.baseUrl = baseUrl;
		this.homeAddress = homeAddress;
		this.seenUrls = seenUrls;
		this.trigger = trigger;
	}
	
	public void run() {
		String url = null;
			try{
				while(true){
					if(trigger.isTaskComplete()){
						logger.info(Thread.currentThread().getName()+" isTaskComplete :"+trigger.isTaskComplete());
						break;
					}
					url = fetchURL();
					if(url != null){
						processReqForUrl(url);
					}
				}
			}catch(InterruptedException e){
				
			}
	}
	
	private boolean duplicateURL(String url){
		boolean isDuplicate = false;
		if(isNotEmpty(url)){
			synchronized (seenUrls) {
				if(seenUrls.containsKey(url)){
					isDuplicate = true;
				}
				seenUrls.notifyAll();
			}
		}
		return isDuplicate;
	}
	
	private void processReqForUrl(String url) throws InterruptedException{
		Document responseObj = null;
		Elements links = null;
		Downloader downloader = null;

		if (isNotEmpty(url)) {
			responseObj = getResponse(url);
			if (responseObj != null) {
				links = parser.parseForAnchors(responseObj);
				populateUrlsCollection(url, links);
				if (filter.isEmail(responseObj)) {
					if (criteriaNo == 0) {
						downloader = new DownloaderImpl();
						downloader.downloadEmail(downloadPath, responseObj);
					}else if(criteriaNo == 1){
						downloader = new DownloaderImpl();
						downloader.downloadEmail(downloadPath, responseObj);
					}
				}
			} else {
				logger.info(" Could not get any response from the url " + url);
			}
		}
	}
	
	private void populateUrlsCollection(String url, Elements links) throws InterruptedException {
		String href = null;
		String addUrl = null;
		if (links != null && links.size() > 0) {
			if (urlsQueue != null) {
				for (Element link : links) {
					href = link.attr(HREF);
					if (href.startsWith(HTTP)) {
						if (href.startsWith(baseUrl)) {
							if(!duplicateURL(href)){
								addToUrlMap(href);
								urlsQueue.put(href);
							}
						}
					} else {
						addUrl = createUrl(url, href);
						if (addUrl.startsWith(baseUrl)) {
							if (!duplicateURL(addUrl)) {
								addToUrlMap(addUrl);
								urlsQueue.put(addUrl);
							}
						}
					}
				}
			}
		}
	}
	
	private void addToUrlMap(String url){
		if(isNotEmpty(url)){
			synchronized (seenUrls) {
				seenUrls.put(url, url);
				seenUrls.notifyAll();
			}
		}
	}
	
	private String createUrl(String url, String href) {
		StringBuilder newUrl = new StringBuilder();
		if (isNotEmpty(url)) {
			if (href.indexOf('/') == 0) {
				newUrl = newUrl.append(homeAddress).append(href);
			} else {
				newUrl = newUrl.append(
						url.substring(0, url.lastIndexOf('/') + 1))
						.append(href);
			}
		}
		return newUrl.toString();
	}
	
	private Document getResponse(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
		} 
		
		return doc;
	}
	
	private boolean isNotEmpty(String str) {
		if (str != null && str.length() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private String fetchURL() throws InterruptedException{
		String url = null;
		if(urlsQueue != null){
			url = urlsQueue.poll(5000, TimeUnit.MILLISECONDS);
		}
		return url;
	}
}