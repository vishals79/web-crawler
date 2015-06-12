package com.pramati.webcrawler.thread.worker;

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

import com.pramati.webcrawler.common.InputData;
import com.pramati.webcrawler.common.RecoveryData;
import com.pramati.webcrawler.common.URLProcessorData;
import com.pramati.webcrawler.downloader.Downloader;
import com.pramati.webcrawler.downloader.DownloaderImpl;
import com.pramati.webcrawler.filter.Filter;
import com.pramati.webcrawler.parser.Parser;
import com.pramati.webcrawler.thread.trigger.Trigger;

public class URLProcessorWorker implements Runnable{
	
	private BlockingQueue<String> urlsQueue = null;
	private Parser parser = null;
	private Filter filter = null;
	
	private int criteriaNo = 0;
	private String downloadPath = null;
	private String baseUrl = null;
	private String homeAddress = null;
	private Map<String, String> seenUrls = null;
	
	private InputData inputData;
	private URLProcessorData urlProcessorData;
	
	private RecoveryData recoveryData;
	private BlockingQueue<String> adddedURLQueue;
	private BlockingQueue<String> removedURLQueue;
	private BlockingQueue<String> visitedUrls;
	
	private static Log logger = LogFactory.getLog(URLProcessorWorker.class);
	
	private final String HTTP = "http://";
	private final String HREF = "href";
	
	private Trigger trigger = null;
	
	public URLProcessorWorker(InputData inputData) {
		super();
		if(inputData != null){
			this.inputData = inputData;
			this.criteriaNo = inputData.getCriteriaNo();
			this.downloadPath = inputData.getDownloadPath();
			this.baseUrl = inputData.getBaseUrl();
			this.homeAddress = inputData.getHomeAddress();
		}
	}
	
	private void initialize(){
		if(urlProcessorData != null){
			this.urlsQueue = urlProcessorData.getUrlsQueue();
			this.seenUrls = urlProcessorData.getSeenUrls();
		}
		if(recoveryData != null){
			this.adddedURLQueue = recoveryData.getAdddedURLQueue();
			this.removedURLQueue = recoveryData.getRemovedURLQueue();
			this.visitedUrls = recoveryData.getVisitedUrls();
		}
	}
	
	public void run() {
		String url = null;
			try{
				initialize();
				while(true){
					if(trigger.isTaskComplete()){
						logger.info(Thread.currentThread().getName()+" isTaskComplete :"+trigger.isTaskComplete());
						break;
					}
					url = fetchURL();
					if(url != null){
						processReqForUrl(url);
						removedURLQueue.put(url);
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
								adddedURLQueue.put(href);
							}
						}
					} else {
						addUrl = createUrl(url, href);
						if (addUrl.startsWith(baseUrl)) {
							if (!duplicateURL(addUrl)) {
								addToUrlMap(addUrl);
								urlsQueue.put(addUrl);
								adddedURLQueue.put(addUrl);
							}
						}
					}
				}
			}
		}
	}
	
	private void addToUrlMap(String url) {
		if (isNotEmpty(url)) {
			synchronized (seenUrls) {
				seenUrls.put(url, url);
				seenUrls.notifyAll();
			}
			try {
				visitedUrls.put(url);
			} catch (InterruptedException e) {
				e.printStackTrace();
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

	public InputData getInputData() {
		return inputData;
	}

	public void setInputData(InputData inputData) {
		this.inputData = inputData;
	}

	public URLProcessorData getUrlProcessorData() {
		return urlProcessorData;
	}

	public void setUrlProcessorData(URLProcessorData urlProcessorData) {
		this.urlProcessorData = urlProcessorData;
	}

	public RecoveryData getRecoveryData() {
		return recoveryData;
	}

	public void setRecoveryData(RecoveryData recoveryData) {
		this.recoveryData = recoveryData;
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

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
}